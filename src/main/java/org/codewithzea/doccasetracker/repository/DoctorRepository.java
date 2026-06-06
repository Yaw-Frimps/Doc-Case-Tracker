package org.codewithzea.doccasetracker.repository;

import org.codewithzea.doccasetracker.entity.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, String> {

    Optional<Doctor> findByDoctorIdAndDeletedFalse(String doctorId);

    Page<Doctor> findAllByDeletedFalse(Pageable pageable);

    Page<Doctor> findBySpecialization_IdAndDeletedFalse(
            String specialtyId,
            Pageable pageable
    );

    @Query("""
            SELECT d
            FROM Doctor d
            WHERE d.deleted = false
            AND (
                    LOWER(d.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(d.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(d.phoneNumber) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(d.hospitalName) LIKE LOWER(CONCAT('%', :keyword, '%'))
            )
            """)
    Page<Doctor> searchDoctors(
            String keyword,
            Pageable pageable
    );

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);
}
