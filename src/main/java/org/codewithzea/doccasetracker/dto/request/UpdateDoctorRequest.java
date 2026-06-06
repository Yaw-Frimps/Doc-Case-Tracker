package org.codewithzea.doccasetracker.dto.request;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UpdateDoctorRequest {

    private String fullName;

    @Email
    private String email;

    private String phoneNumber;

    private String specialtyId;

    private String hospitalName;
}
