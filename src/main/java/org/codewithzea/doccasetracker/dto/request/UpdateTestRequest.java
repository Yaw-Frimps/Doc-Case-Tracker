package org.codewithzea.doccasetracker.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateTestRequest {
    @NotBlank(message = "Test name is required")
    String testName;

    @NotNull(message = "Price is required")
    BigDecimal price;

    @NotNull(message = "Commission is required")
    BigDecimal commission;
}
