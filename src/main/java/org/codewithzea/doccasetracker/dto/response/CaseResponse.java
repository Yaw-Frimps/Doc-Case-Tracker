package org.codewithzea.doccasetracker.dto.response;


import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

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

    private List<TestResponse> tests;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
