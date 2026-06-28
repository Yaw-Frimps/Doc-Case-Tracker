package org.codewithzea.doccasetracker.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class UpdateCaseRequest {

    private String doctorId;

    private String patientName;

    private List<String> testIds;
}