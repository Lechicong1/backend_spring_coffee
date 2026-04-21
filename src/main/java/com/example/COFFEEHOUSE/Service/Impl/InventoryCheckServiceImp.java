package com.example.COFFEEHOUSE.Service.Impl;

import com.example.COFFEEHOUSE.DTO.Request.InventoryCheckReq;
import com.example.COFFEEHOUSE.DTO.Response.InventoryCheckResp;
import com.example.COFFEEHOUSE.Entity.IngredientEntity;
import com.example.COFFEEHOUSE.Entity.InventoryCheckEntity;
import com.example.COFFEEHOUSE.Exception.DuplicateResourceException;
import com.example.COFFEEHOUSE.Exception.InvalidInputException;
import com.example.COFFEEHOUSE.Exception.ResourceNotFoundException;
import com.example.COFFEEHOUSE.Reposistory.IngredientRepo;
import com.example.COFFEEHOUSE.Reposistory.InventoryCheckRepo;
import com.example.COFFEEHOUSE.Service.InventoryCheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryCheckServiceImp implements InventoryCheckService {

    private final InventoryCheckRepo inventoryCheckRepo;
    private final IngredientRepo ingredientRepo;

    @Override
    @Transactional
    public void createCheck(InventoryCheckReq request) {
        // Validate input
        validateRequest(request);

        // Check if ingredient exists
        IngredientEntity ingredient = ingredientRepo.findByName(request.getIngredient())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Không tìm thấy nguyên liệu: " + request.getIngredient()));

        // Check if already checked today
        Optional<InventoryCheckEntity> existingCheck = inventoryCheckRepo
                .findByIngredientToday(request.getIngredient());
        if (existingCheck.isPresent()) {
            throw new DuplicateResourceException("Nguyên liệu '" + request.getIngredient()
                    + "' đã được kiểm kho hôm nay. Vui lòng sử dụng chức năng cập nhật.");
        }

        // Get theory quantity from ingredient's stock
        BigDecimal theoryQuantity = BigDecimal
                .valueOf(ingredient.getStockQuantity() != null ? ingredient.getStockQuantity() : 0.0);

        // Calculate difference
        BigDecimal difference = request.getActualQuantity().subtract(theoryQuantity);

        // Calculate status based on percent + absolute threshold by unit
        String status = calculateStatus(theoryQuantity, request.getActualQuantity(), ingredient.getUnit());

        // Build entity
        InventoryCheckEntity entity = InventoryCheckEntity.builder()
                .ingredient(request.getIngredient())
                .theoryQuantity(theoryQuantity)
                .actualQuantity(request.getActualQuantity())
                .difference(difference)
                .note(request.getNote() != null ? request.getNote() : status)
                .checkedAt(LocalDateTime.now())
                .build();

        inventoryCheckRepo.save(entity);
    }

    @Override
    @Transactional
    public void updateCheck(InventoryCheckReq request) {
        // Validate input
        validateRequest(request);

        // Check if ingredient exists
        IngredientEntity ingredient = ingredientRepo.findByName(request.getIngredient())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Không tìm thấy nguyên liệu: " + request.getIngredient()));

        // Get theory quantity from current stock
        BigDecimal theoryQuantity = BigDecimal
                .valueOf(ingredient.getStockQuantity() != null ? ingredient.getStockQuantity() : 0.0);

        // Calculate difference
        BigDecimal difference = request.getActualQuantity().subtract(theoryQuantity);

        // Calculate status based on percent + absolute threshold by unit
        String status = calculateStatus(theoryQuantity, request.getActualQuantity(), ingredient.getUnit());

        // Find existing check for today (UPSERT logic)
        Optional<InventoryCheckEntity> existingCheck = inventoryCheckRepo
                .findByIngredientToday(request.getIngredient());

        if (existingCheck.isPresent()) {
            // UPDATE existing record
            InventoryCheckEntity entity = existingCheck.get();
            entity.setTheoryQuantity(theoryQuantity);
            entity.setActualQuantity(request.getActualQuantity());
            entity.setDifference(difference);
            entity.setNote(request.getNote() != null ? request.getNote() : status);
            entity.setCheckedAt(LocalDateTime.now());
            inventoryCheckRepo.save(entity);
        } else {
            // INSERT new record
            InventoryCheckEntity entity = InventoryCheckEntity.builder()
                    .ingredient(request.getIngredient())
                    .theoryQuantity(theoryQuantity)
                    .actualQuantity(request.getActualQuantity())
                    .difference(difference)
                    .note(request.getNote() != null ? request.getNote() : status)
                    .checkedAt(LocalDateTime.now())
                    .build();
            inventoryCheckRepo.save(entity);
        }
    }

    @Override
    @Transactional
    public void deleteCheck(Long id) {
        InventoryCheckEntity entity = inventoryCheckRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy kiểm kho với id: " + id));
        inventoryCheckRepo.delete(entity);
    }

    @Override
    public List<InventoryCheckResp> findAll() {
        List<InventoryCheckEntity> checks = inventoryCheckRepo.findAllByOrderByCheckedAtDesc();
        return checks.stream()
                .map(this::toResponseWithDetails)
                .collect(Collectors.toList());
    }

    @Override
    public InventoryCheckResp findById(Long id) {
        InventoryCheckEntity entity = inventoryCheckRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy kiểm kho với id: " + id));
        return toResponseWithDetails(entity);
    }

    @Override
    public List<InventoryCheckResp> findByDate(LocalDate date) {
        List<InventoryCheckEntity> checks = inventoryCheckRepo.findByDate(date);
        return checks.stream()
                .map(this::toResponseWithDetails)
                .collect(Collectors.toList());
    }

    @Override
    public List<InventoryCheckResp> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll();
        }
        List<InventoryCheckEntity> checks = inventoryCheckRepo.search(keyword.trim());
        return checks.stream()
                .map(this::toResponseWithDetails)
                .collect(Collectors.toList());
    }

    @Override
    public List<InventoryCheckResp> getInventoryCheckDataByDate(LocalDate date) {
        // Get all ingredients
        List<IngredientEntity> allIngredients = ingredientRepo.findAllByOrderByNameAsc();

        // Get all checks for the date
        List<InventoryCheckEntity> checksForDate = inventoryCheckRepo.findByDate(date);

        // Create map of ingredient name -> check entity
        Map<String, InventoryCheckEntity> checkMap = checksForDate.stream()
                .collect(Collectors.toMap(InventoryCheckEntity::getIngredient, check -> check, (a, b) -> a));

        // Build response list
        List<InventoryCheckResp> result = new ArrayList<>();

        for (IngredientEntity ingredient : allIngredients) {
            InventoryCheckEntity check = checkMap.get(ingredient.getName());

            if (check != null) {
                // Has check for this date
                result.add(toResponseWithIngredient(check, ingredient));
            } else {
                // No check for this date - return ingredient info only
                result.add(InventoryCheckResp.builder()
                        .id(null)
                        .ingredient(ingredient.getName())
                        .unit(ingredient.getUnit())
                        .theoryQuantity(BigDecimal
                                .valueOf(ingredient.getStockQuantity() != null ? ingredient.getStockQuantity() : 0.0))
                        .actualQuantity(null)
                        .difference(null)
                        .percentDifference(null)
                        .status(null)
                        .note(null)
                        .checkedAt(null)
                        .build());
            }
        }

        return result;
    }

    @Override
    public List<InventoryCheckResp> getLossReportByDateRange(LocalDate fromDate, LocalDate toDate) {
        if (fromDate == null || toDate == null) {
            throw new InvalidInputException("from_date và to_date là bắt buộc");
        }

        if (fromDate.isAfter(toDate)) {
            throw new InvalidInputException("from_date không được lớn hơn to_date");
        }

        LocalDateTime fromDateTime = fromDate.atStartOfDay();
        LocalDateTime toDateTimeExclusive = toDate.plusDays(1).atStartOfDay();

        List<InventoryCheckRepo.InventoryCheckDateRangeProjection> rawData = inventoryCheckRepo
                .getLossReportByDateRange(fromDateTime, toDateTimeExclusive);

        return rawData.stream()
                .map(item -> InventoryCheckResp.builder()
                        .id(null)
                        .ingredient(item.getIngredient())
                        .unit(null)
                        .theoryQuantity(item.getTotalTheory())
                        .actualQuantity(item.getTotalActual())
                        .difference(item.getTotalDifference())
                        .percentDifference(null)
                        .status(null)
                        .note(null)
                        .checkedAt(item.getCheckDate() != null ? item.getCheckDate().atStartOfDay() : null)
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Logic kiểm kho tối ưu: Kết hợp % và ngưỡng tuyệt đối, đã gỡ bỏ
     * massiveDifference.
     */
    private String calculateStatus(BigDecimal theoryQuantity, BigDecimal actualQuantity, String unit) {
        BigDecimal safeTheory = theoryQuantity != null ? theoryQuantity : BigDecimal.ZERO;
        BigDecimal safeActual = actualQuantity != null ? actualQuantity : BigDecimal.ZERO;

        BigDecimal difference = safeActual.subtract(safeTheory);
        BigDecimal absDifference = difference.abs();
        BigDecimal threshold = getDefaultAbsoluteThreshold(unit);

        // Trường hợp lý thuyết = 0: không tính %, chỉ xét ngưỡng tuyệt đối
        if (safeTheory.compareTo(BigDecimal.ZERO) == 0) {
            if (safeActual.compareTo(BigDecimal.ZERO) > 0 && absDifference.compareTo(threshold) > 0) {
                return "WARNING";
            }
            return "OK";
        }

        BigDecimal percentDiff = absDifference
                .divide(safeTheory, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));

        boolean isMissing = difference.compareTo(BigDecimal.ZERO) < 0;
        boolean overPercentCritical = percentDiff.compareTo(BigDecimal.valueOf(5)) > 0;
        boolean overPercentWarning = percentDiff.compareTo(BigDecimal.valueOf(2)) > 0;
        boolean overAbsoluteThreshold = absDifference.compareTo(threshold) > 0;

        // Xử lý trạng thái an toàn và có khả năng scale tốt
        if (overAbsoluteThreshold) {
            if (overPercentCritical) {
                return isMissing ? "CRITICAL" : "WARNING";
            } else if (overPercentWarning) {
                return "WARNING";
            }
        }

        return "OK";
    }

    /**
     * Calculate percent difference
     */
    private BigDecimal calculatePercentDifference(BigDecimal theoryQuantity, BigDecimal actualQuantity) {
        if (theoryQuantity == null || theoryQuantity.compareTo(BigDecimal.ZERO) == 0) {
            if (actualQuantity != null && actualQuantity.compareTo(BigDecimal.ZERO) > 0) {
                return BigDecimal.valueOf(100.00).setScale(2, RoundingMode.HALF_UP);
            }
            return BigDecimal.ZERO;
        }

        BigDecimal difference = actualQuantity.subtract(theoryQuantity);
        return difference.abs()
                .divide(theoryQuantity, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private void validateRequest(InventoryCheckReq request) {
        if (request.getIngredient() == null || request.getIngredient().trim().isEmpty()) {
            throw new InvalidInputException("Tên nguyên liệu không được để trống");
        }
        if (request.getActualQuantity() == null) {
            throw new InvalidInputException("Số lượng thực tế không được để trống");
        }
        if (request.getActualQuantity().compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidInputException("Số lượng thực tế không được âm");
        }
    }

    private InventoryCheckResp toResponseWithDetails(InventoryCheckEntity entity) {
        // Get ingredient info
        IngredientEntity ingredient = ingredientRepo.findByName(entity.getIngredient()).orElse(null);
        return toResponseWithIngredient(entity, ingredient);
    }

    private InventoryCheckResp toResponseWithIngredient(InventoryCheckEntity entity, IngredientEntity ingredient) {
        String unit = ingredient != null ? ingredient.getUnit() : null;
        String status = calculateStatus(entity.getTheoryQuantity(), entity.getActualQuantity(), unit);
        BigDecimal percentDiff = calculatePercentDifference(entity.getTheoryQuantity(), entity.getActualQuantity());

        return InventoryCheckResp.builder()
                .id(entity.getId())
                .ingredient(entity.getIngredient())
                .unit(ingredient != null ? ingredient.getUnit() : null)
                .theoryQuantity(entity.getTheoryQuantity())
                .actualQuantity(entity.getActualQuantity())
                .difference(entity.getDifference())
                .percentDifference(percentDiff)
                .status(status)
                .note(entity.getNote())
                .checkedAt(entity.getCheckedAt())
                .build();
    }

    /**
     * Nội suy ngưỡng dung sai tuyệt đối dựa vào đơn vị tính.
     */
    private BigDecimal getDefaultAbsoluteThreshold(String unit) {
        if (unit == null || unit.trim().isEmpty()) {
            return BigDecimal.valueOf(5); // Default an toàn
        }

        String lowerUnit = unit.trim().toLowerCase();

        // 1. Nhóm CHẤT LỎNG (Sữa, Syrup, Nước cốt)
        // Chấp nhận sai số cao hơn (100ml) vì dính thành ca, rót tràn, bọt sữa
        if (lowerUnit.equals("ml")) {
            return BigDecimal.valueOf(100);
        } else if (lowerUnit.equals("l")) {
            return BigDecimal.valueOf(0.1);
        }
        // 2. Nhóm CHẤT RẮN (Cà phê hạt, Trà, Bột các loại)
        // Cần siết chặt hơn (50g) vì giá trị cao, tương đương hao hụt tối đa 2-3 ly
        else if (lowerUnit.equals("g")) {
            return BigDecimal.valueOf(100);
        } else if (lowerUnit.equals("kg")) {
            return BigDecimal.valueOf(0.1);
        }
        // 3. Nhóm ĐẾM SỐ LƯỢNG (Chai, Lon, Quả, Cái)
        else {
            return BigDecimal.valueOf(2); // Cho phép đếm nhầm/hao hụt 2 đơn vị
        }
    }
}
