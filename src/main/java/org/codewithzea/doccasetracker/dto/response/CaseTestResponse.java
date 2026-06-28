package org.codewithzea.doccasetracker.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CaseTestResponse {
    private String testId;
    private String testName;
    private BigDecimal price;
    private BigDecimal commission;
}
