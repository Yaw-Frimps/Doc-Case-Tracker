package org.codewithzea.doccasetracker.service;


import org.codewithzea.doccasetracker.dto.request.CreateDoctorRequest;
import org.codewithzea.doccasetracker.dto.request.UpdateDoctorRequest;
import org.codewithzea.doccasetracker.dto.response.DoctorResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface DoctorService {

    DoctorResponse createDoctor(CreateDoctorRequest request);

    DoctorResponse updateDoctor(String doctorId,
                                UpdateDoctorRequest request);

    void deleteDoctor(String doctorId);

    DoctorResponse getDoctor(String doctorId);

    Page<DoctorResponse> getAllDoctors(Pageable pageable);

    DoctorResponse activateDoctor(String doctorId);

    DoctorResponse deactivateDoctor(String doctorId);

    Page<DoctorResponse> searchDoctors(
            String keyword,
            Pageable pageable
    );

    Page<DoctorResponse> getDoctorsBySpecialty(
            String specialtyId,
            Pageable pageable
    );
}
