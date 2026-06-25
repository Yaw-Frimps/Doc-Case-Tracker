package org.codewithzea.doccasetracker.dto;

import java.math.BigDecimal;

public interface MonthlyCommissionProjection {
    Integer getMonth();
    BigDecimal getTotal();
}
