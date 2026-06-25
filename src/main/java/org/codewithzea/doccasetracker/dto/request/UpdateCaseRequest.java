package org.codewithzea.doccasetracker.dto.request;

import lombok.Data;

@Data
public class UpdateCaseRequest {

    private String doctorId;

    private String patientName;

    private Integer numberOfCases;

    private String testId;
}