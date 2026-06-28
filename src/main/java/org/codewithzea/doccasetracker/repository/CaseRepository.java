package org.codewithzea.doccasetracker.repository;



import org.codewithzea.doccasetracker.entity.Cases;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CaseRepository extends JpaRepository<Cases, String> {

    Page<Cases> findAllByDeletedFalse(Pageable pageable);

    Optional<Cases> findByIdAndDeletedFalse(String id);

    @Query("""
    SELECT COUNT(c)
    FROM Cases c
    WHERE c.deleted = false
    AND YEAR(c.createdAt) = :year
    AND MONTH(c.createdAt) = :month
    """)
    Long countCasesForMonth(Integer year, Integer month);

    @Query("""
    SELECT COUNT(c)
    FROM Cases c
    WHERE c.deleted = false
    AND YEAR(c.createdAt) = :year
    """)
    Long countCasesForYear(Integer year);

    @Query("""
    SELECT c.doctor.doctorId,
           c.doctor.fullName,
           COUNT(c)
    FROM Cases c
    WHERE c.deleted = false
    GROUP BY c.doctor.doctorId,
             c.doctor.fullName
    ORDER BY COUNT(c) DESC
    """)
    List<Object[]> findTopReferringDoctors(Pageable pageable);

    @Query("""
    SELECT c.doctor.doctorId,
           c.doctor.fullName,
           COUNT(c)
    FROM Cases c
    WHERE c.deleted = false
    GROUP BY c.doctor.doctorId,
             c.doctor.fullName
    ORDER BY COUNT(c) DESC
    """)
    List<Object[]> getDoctorReferrals();

}