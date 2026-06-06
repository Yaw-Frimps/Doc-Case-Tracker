package org.codewithzea.doccasetracker.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class DoctorResponse {

    private String doctorId;

    private String fullName;

    private String email;

    private String phoneNumber;

    private String specialty;

    private String hospitalName;

    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
