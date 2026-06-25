package org.codewithzea.doccasetracker.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTestRequest {
    @NotBlank(message = "Test name is required")
    private String testName;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal price;

    @NotNull(message = "Commission is required")
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal commission;

}