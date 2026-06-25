package org.codewithzea.doccasetracker.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TopDoctorResponse {

    private String doctorId;

    private String doctorName;

    private Long totalCases;
}
