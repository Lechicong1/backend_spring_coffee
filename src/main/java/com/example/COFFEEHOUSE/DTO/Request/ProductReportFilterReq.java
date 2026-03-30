package com.example.COFFEEHOUSE.DTO.Request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class ProductReportFilterReq {
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @NotNull(message = "from_date is required")
    @PastOrPresent(message = "Ngày báo cáo không được ở trong tương lai!")
    private LocalDate from_date; // Đổi thành snake_case để khớp URL

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @NotNull(message = "to_date is required")
    private LocalDate to_date; // Đổi thành snake_case

    @Pattern(regexp = "all|\\d+", message = "Invalid category_id")
    private String category_id = "all";

    @Pattern(regexp = "desc|asc|top_5_high|top_5_low", message = "Invalid sort_by value")
    private String sort_by = "desc";

    @AssertTrue(message = "Ngày bắt đầu không thể lớn hơn ngày kết thúc!")
    public boolean isDateRangeValid() {
        return from_date == null || to_date == null || !from_date.isAfter(to_date);
    }
}
