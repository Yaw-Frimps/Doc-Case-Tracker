package org.codewithzea.doccasetracker.repository;

import org.codewithzea.doccasetracker.dto.TopDoctorProjection;
import org.codewithzea.doccasetracker.entity.CaseTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface CaseTestRepository extends JpaRepository<CaseTest, String> {

    @Query("""
    SELECT ct.test.testName,
           COUNT(ct)
    FROM CaseTest ct
    WHERE ct.cases.deleted = false
    GROUP BY ct.test.testName
    """)
    List<Object[]> getTestStatistics();

    @Query("""
    SELECT COALESCE(SUM(ct.priceAtTime), 0)
    FROM CaseTest ct
    WHERE ct.cases.deleted = false
    """)
    BigDecimal getTotalRevenue();

    @Query("""
    SELECT MONTH(ct.cases.createdAt),
           COALESCE(SUM(ct.priceAtTime),0)
    FROM CaseTest ct
    WHERE ct.cases.deleted = false
    GROUP BY MONTH(ct.cases.createdAt)
    ORDER BY MONTH(ct.cases.createdAt)
    """)
    List<Object[]> getMonthlyRevenue(Integer year);

    @Query("""
        SELECT COALESCE(SUM(ct.commissionAtTime), 0)
        FROM CaseTest ct
        WHERE ct.cases.deleted = false
    """)
    BigDecimal getTotalExpectedCommission();

    @Query("""
        SELECT COALESCE(SUM(ct.commissionAtTime), 0)
        FROM CaseTest ct
        WHERE ct.cases.deleted = false
        AND YEAR(ct.createdAt) = :year
    """)
    BigDecimal getTotalExpectedCommissionForYear(int year);

    @Query("""
        SELECT MONTH(ct.createdAt),
               COALESCE(SUM(ct.commissionAtTime), 0)
        FROM CaseTest ct
        WHERE ct.cases.deleted = false
        AND YEAR(ct.createdAt) = :year
        GROUP BY MONTH(ct.createdAt)
        ORDER BY MONTH(ct.createdAt)
    """)
    List<Object[]> getMonthlyExpectedCommission(int year);

    @Query("""
    SELECT COUNT(ct)
    FROM CaseTest ct
    WHERE ct.cases.deleted = false
    AND YEAR(ct.createdAt) = :year
    AND MONTH(ct.createdAt) = :month
""")
    Long countTestsForMonth(int year, int month);

    @Query("""
    SELECT COUNT(ct)
    FROM CaseTest ct
    WHERE ct.cases.deleted = false
    AND YEAR(ct.createdAt) = :year
""")
    Long countTestsForYear(int year);


    @Query("""
    SELECT c.doctor.doctorId AS doctorId,
           c.doctor.fullName AS doctorName,
           COUNT(DISTINCT c.id) AS totalCases,
           (
               SELECT COUNT(ct.id)
               FROM CaseTest ct
               WHERE ct.cases.doctor.doctorId = c.doctor.doctorId
           ) AS totalTests
    FROM Cases c
    WHERE c.deleted = false
    GROUP BY c.doctor.doctorId, c.doctor.fullName
    ORDER BY totalTests DESC
""")
    List<TopDoctorProjection> findTopDoctorsWithCasesAndTests(Pageable pageable);
}
