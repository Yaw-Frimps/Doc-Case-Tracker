package org.codewithzea.doccasetracker.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCaseRequest {

    @NotBlank(message = "Doctor ID is required")
    private String doctorId;

    @NotBlank(message = "Patient name is required")
    private String patientName;

    @NotNull(message = "Number of cases is required")
    private Integer numberOfCases;

    @NotBlank(message = "Test ID is required")
    private String testId;
}
