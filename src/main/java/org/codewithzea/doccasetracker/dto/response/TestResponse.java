package org.codewithzea.doccasetracker.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TestResponse {

    private String id;
    private String testName;
    private BigDecimal price;
    private BigDecimal commission;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
