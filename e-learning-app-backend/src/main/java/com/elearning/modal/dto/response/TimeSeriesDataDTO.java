package com.elearning.modal.dto.response; // Gói (package) của bạn

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeSeriesDataDTO {

    private LocalDate date;

    private BigDecimal value;

}