package org.codewithzea.doccasetracker.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class MonthlyRevenueChartResponse {

    private String month;

    private BigDecimal revenue;

    private BigDecimal commission;
}
