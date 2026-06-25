package org.codewithzea.doccasetracker.dto.response;


import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaseResponse {

    private String id;

    private String doctorId;
    private String doctorName;

    private String patientName;

    private Integer numberOfCases;

    private String testId;
    private String testName;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
