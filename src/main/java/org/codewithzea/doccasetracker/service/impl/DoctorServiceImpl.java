package org.codewithzea.doccasetracker.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.codewithzea.doccasetracker.dto.request.CreateDoctorRequest;
import org.codewithzea.doccasetracker.dto.request.UpdateDoctorRequest;
import org.codewithzea.doccasetracker.dto.response.DoctorResponse;
import org.codewithzea.doccasetracker.entity.Doctor;
import org.codewithzea.doccasetracker.entity.DoctorStatus;
import org.codewithzea.doccasetracker.entity.Specialty;
import org.codewithzea.doccasetracker.entity.User;
import org.codewithzea.doccasetracker.exception.ResourceNotFoundException;
import org.codewithzea.doccasetracker.mapper.DoctorMapper;
import org.codewithzea.doccasetracker.repository.DoctorRepository;
import org.codewithzea.doccasetracker.repository.SpecialtyRepository;
import org.codewithzea.doccasetracker.service.AuditLogService;
import org.codewithzea.doccasetracker.service.DoctorService;
import org.codewithzea.doccasetracker.util.AuthenticatedUserProvider;
import org.codewithzea.doccasetracker.util.SpecialtyService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.codewithzea.doccasetracker.util.AuditActions.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DoctorServiceImpl implements DoctorService {

    public static final String DOCTOR_CACHE = "doctor";
    public static final String DOCTOR = "DOCTOR";
    public static final String DOCTORS_CACHE = "doctors";

    private final DoctorRepository doctorRepository;
    private final SpecialtyRepository specialtyRepository;
    private final SpecialtyService specialtyService;
    private final DoctorMapper doctorMapper;
    private final AuditLogService auditLogService;
    private final AuthenticatedUserProvider userProvider;

    @Override
    @CacheEvict(
            value = {DOCTOR_CACHE,DOCTORS_CACHE},
            allEntries = true
    )
    public DoctorResponse createDoctor(CreateDoctorRequest request) {

        log.debug("Creating doctor: {}", request.getFullName());

        Specialty specialty = specialtyService.getOrCreateSpecialty(request.getSpecialtyName());


        Doctor doctor = Doctor.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .hospitalName(request.getHospitalName())
                .specialization(specialty)
                .status(DoctorStatus.ACTIVE)
                .build();

        Doctor savedDoctor = doctorRepository.save(doctor);

        User admin = userProvider.getCurrentUser();

        auditLogService.log(
                DOCTOR_CREATED,
                DOCTOR,
                savedDoctor.getDoctorId(),
                "Doctor created: " + savedDoctor.getFullName(),
                admin.getEmail(),
                admin.getId(),
                admin.getRole().getName().name()
        );

        log.info("Doctor created successfully: {}",
                savedDoctor.getDoctorId());

        return doctorMapper.toResponse(savedDoctor);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = DOCTOR_CACHE, key = "#doctorId")
    public DoctorResponse getDoctor(String doctorId) {

        log.debug("Fetching doctor {}", doctorId);

        Doctor doctor = getDoctorOrThrow(doctorId);

        return doctorMapper.toResponse(doctor);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = DOCTORS_CACHE,
            key = "#pageable.pageNumber + '-' + #pageable.pageSize"
    )
    public Page<DoctorResponse> getAllDoctors(Pageable pageable) {

        log.debug("Fetching doctors page={}",
                pageable.getPageNumber());

        return doctorRepository.findAllByDeletedFalse(pageable)
                .map(doctorMapper::toResponse);
    }

    @Override
    @CacheEvict(
            value = {DOCTOR_CACHE,DOCTORS_CACHE},
            allEntries = true
    )
    public DoctorResponse updateDoctor(
            String doctorId,
            UpdateDoctorRequest request) {

        Doctor doctor = getDoctorOrThrow(doctorId);

        log.debug("Updating doctor {}", doctorId);

        if (request.getFullName() != null) {
            doctor.setFullName(request.getFullName());
        }

        if (request.getEmail() != null) {
            doctor.setEmail(request.getEmail());
        }

        if (request.getPhoneNumber() != null) {
            doctor.setPhoneNumber(request.getPhoneNumber());
        }

        if (request.getHospitalName() != null) {
            doctor.setHospitalName(request.getHospitalName());
        }

        if (request.getSpecialtyId() != null) {

            Specialty specialty = specialtyRepository
                    .findById(request.getSpecialtyId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Specialty not found"));

            doctor.setSpecialization(specialty);
        }

        Doctor updatedDoctor = doctorRepository.save(doctor);

        User admin = userProvider.getCurrentUser();

        auditLogService.log(
                DOCTOR_UPDATED,
                DOCTOR,
                updatedDoctor.getDoctorId(),
                "Doctor updated",
                admin.getEmail(),
                admin.getId(),
                admin.getRole().getName().name()
        );

        log.info("Doctor updated {}", doctorId);

        return doctorMapper.toResponse(updatedDoctor);
    }

    @Override
    @CacheEvict(
            value = {DOCTOR_CACHE,DOCTORS_CACHE},
            allEntries = true
    )
    public void deleteDoctor(String doctorId) {

        Doctor doctor = getDoctorOrThrow(doctorId);

        doctor.setDeleted(true);
        doctor.setStatus(DoctorStatus.INACTIVE);
        doctor.setDeletedAt(LocalDateTime.now());

        doctorRepository.save(doctor);

        User admin = userProvider.getCurrentUser();

        auditLogService.log(
                DOCTOR_DELETED,
                DOCTOR,
                doctor.getDoctorId(),
                "Doctor deleted: " + doctor.getFullName(),
                admin.getEmail(),
                admin.getId(),
                admin.getRole().getName().name()
        );

        log.info(
                "Doctor soft deleted doctorId={}",
                doctorId
        );
    }

    @Override
    @CacheEvict(
            value = {DOCTOR_CACHE,DOCTORS_CACHE},
            allEntries = true
    )
    public DoctorResponse activateDoctor(String doctorId) {

        Doctor doctor = getDoctorOrThrow(doctorId);

        doctor.setStatus(DoctorStatus.ACTIVE);

        Doctor saved = doctorRepository.save(doctor);

        User admin = userProvider.getCurrentUser();

        auditLogService.log(
                DOCTOR_ACTIVATED,
                DOCTOR,
                doctor.getDoctorId(),
                "Doctor activated",
                admin.getEmail(),
                admin.getId(),
                admin.getRole().getName().name()
        );

        return doctorMapper.toResponse(saved);
    }

    @Override
    @CacheEvict(
            value = {DOCTOR_CACHE,DOCTORS_CACHE},
            allEntries = true
    )
    public DoctorResponse deactivateDoctor(String doctorId) {

        Doctor doctor = getDoctorOrThrow(doctorId);

        doctor.setStatus(DoctorStatus.INACTIVE);

        Doctor saved = doctorRepository.save(doctor);

        User admin = userProvider.getCurrentUser();

        auditLogService.log(
                DOCTOR_DEACTIVATED,
                DOCTOR,
                doctor.getDoctorId(),
                "Doctor deactivated",
                admin.getEmail(),
                admin.getId(),
                admin.getRole().getName().name()
        );

        return doctorMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DoctorResponse> searchDoctors(
            String keyword,
            Pageable pageable
    ) {

        if (keyword == null || keyword.isBlank()) {
            return doctorRepository.findAllByDeletedFalse(pageable)
                    .map(doctorMapper::toResponse);
        }

        log.debug("Searching doctors keyword={}", keyword);

        return doctorRepository
                .searchDoctors(keyword, pageable)
                .map(doctorMapper::toResponse);
    }

    public Page<DoctorResponse> getDeletedDoctors(Pageable pageable) {
        return doctorRepository.findAllByDeletedTrue(pageable)
                .map(doctorMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DoctorResponse> getDoctorsBySpecialty(
            String specialtyId,
            Pageable pageable
    ) {

        return doctorRepository
                .findBySpecialization_IdAndDeletedFalse(
                        specialtyId,
                        pageable
                )
                .map(doctorMapper::toResponse);
    }

    @Transactional
    @CacheEvict(value = {DOCTOR_CACHE, DOCTORS_CACHE}, allEntries = true)
    public DoctorResponse restoreDoctor(String doctorId) {

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Doctor not found with id: " + doctorId));

        doctor.setDeleted(false);
        doctor.setDeletedAt(null);
        doctor.setStatus(DoctorStatus.ACTIVE);

        return doctorMapper.toResponse(
                doctorRepository.save(doctor)
        );
    }

    private Doctor getDoctorOrThrow(String doctorId) {

        return doctorRepository
                .findByDoctorIdAndDeletedFalse(doctorId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Doctor not found with id: " + doctorId
                        ));
    }


}
