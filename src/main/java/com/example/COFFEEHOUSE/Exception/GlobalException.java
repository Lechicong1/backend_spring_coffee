package com.example.COFFEEHOUSE.Exception;


import com.example.COFFEEHOUSE.DTO.ResponseData;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.nio.file.AccessDeniedException;
@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler(value = InvalidInputException.class)
    public ResponseEntity<?> invalidInput(InvalidInputException e) {
        ResponseData responseData = new ResponseData();
        responseData.setSuccess(false);
        responseData.setMessage(e.getMessage());
        responseData.setData(null);
        return ResponseEntity.badRequest().body(responseData); // 400
    }
    @ExceptionHandler(value = EmailException.class)
    public ResponseEntity<?> invalidInput(EmailException e) {
        ResponseData responseData = new ResponseData();
        responseData.setSuccess(false);
        responseData.setMessage(e.getMessage());
        responseData.setData(null);
        return ResponseEntity.badRequest().body(responseData); // 400
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleUnknownException(Exception e, HttpServletRequest request) {
//            // Ghi log chi tiết
//            log.error("💥 Unhandled exception at [{} {}]: {}",
//                    request.getMethod(),
//                    request.getRequestURI(),
//                    e.getMessage(), e);

        // Tạo response trả về client
        e.printStackTrace();
        ResponseData responseData = new ResponseData();
        responseData.setSuccess(false);
        responseData.setMessage("Đã xảy ra lỗi hệ thống: " +e);
        responseData.setData(null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseData);
    }
    // Xảy ra khi parse dữ liệu từ url param bị lỗi
    @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        ResponseData responseData = new ResponseData();
        String message = String.format("Tham số '%s' có giá trị '%s' không đúng định dạng. Phải là kiểu '%s'",
                e.getName(), e.getValue(), e.getRequiredType().getSimpleName());
        responseData.setSuccess(false);
        responseData.setMessage(message);
        responseData.setData(null);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST) // 400
                .body(responseData);
    }

    // Xảy ra khi parse dữ liệu từ JSON sang object thất bại (ví dụ: sai định dạng JSON)
    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleInvalidInput(HttpMessageNotReadableException ex) {
//            log.error("Unhandled exception caught in controller: {}", ex.getMessage(), ex);
        ResponseData responseData = new ResponseData();
        responseData.setSuccess(false);
        responseData.setMessage("Dữ liệu đầu vào không hợp lệ. Vui lòng kiểm tra định dạng JSON.");
        responseData.setData(null);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST) // 400
                .body(responseData);
    }

    @ExceptionHandler(value = BusinessLogicException.class)
    public ResponseEntity<?> handleInvalidInput(BusinessLogicException ex) {
        ResponseData responseData = new ResponseData();
        responseData.setSuccess(false);
        responseData.setMessage(ex.getMessage());
        responseData.setData(null);
        return ResponseEntity
                .status(HttpStatus.CONFLICT) // 409
                .body(responseData);
    }

    @ExceptionHandler(value = UnauthorizedException.class)
    public ResponseEntity<?> unauthorize(UnauthorizedException e) {
        ResponseData responseData = new ResponseData();
        responseData.setSuccess(false);
        responseData.setMessage(e.getMessage());
        responseData.setData(null);
        return ResponseEntity.status(401).body(responseData);
    }

    @ExceptionHandler(value = ResourceNotFoundException.class)
    public ResponseEntity<?> resourceNotFoundException(ResourceNotFoundException e) {
        ResponseData responseData = new ResponseData();
        responseData.setSuccess(false);
        responseData.setMessage(e.getMessage());
        responseData.setData(null);
        return ResponseEntity.status(404).body(responseData);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<?> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        ResponseData responseData = new ResponseData();
        responseData.setSuccess(false);

        // Lấy lỗi đầu tiên (thường đủ dùng)
        String errorMessage = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(fieldError -> fieldError.getDefaultMessage())
                .orElse("Dữ liệu không hợp lệ");

        responseData.setMessage(errorMessage); // Chỉ hiện message bạn đã ghi trong @Pattern
        responseData.setData(null);
        return ResponseEntity.badRequest().body(responseData);
    }
    @ExceptionHandler(value = AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException e) {
        ResponseData responseData = new ResponseData();
        responseData.setSuccess(false);
        responseData.setMessage("Bạn không có quyền truy cập chức năng này.");
        responseData.setData(null);
        return ResponseEntity.status(403).body(responseData);
    }
    @ExceptionHandler(value = ForbiddenException.class)
    public ResponseEntity<?> forbiddenException(ForbiddenException e) {
        ResponseData responseData = new ResponseData();
        responseData.setSuccess(false);
        responseData.setMessage(e.getMessage());
        responseData.setData(null);
        return ResponseEntity.status(403).body(responseData);
    }

    @ExceptionHandler(value = DuplicateResourceException.class)
    public ResponseEntity<?> duplicateResourceException(DuplicateResourceException e) {
        ResponseData responseData = new ResponseData();
        responseData.setSuccess(false);
        responseData.setMessage(e.getMessage());
        responseData.setData(null);
        return ResponseEntity.status(409).body(responseData);
    }
}
