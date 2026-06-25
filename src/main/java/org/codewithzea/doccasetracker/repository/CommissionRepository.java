package org.codewithzea.doccasetracker.repository;

import org.codewithzea.doccasetracker.dto.MonthlyCommissionProjection;
import org.codewithzea.doccasetracker.entity.Commission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface CommissionRepository extends JpaRepository<Commission, String> {

    @Query("""
    SELECT COALESCE(SUM(c.amount),0)
    FROM Commission c
    WHERE c.paid = true
    """)
    BigDecimal getTotalCommissionPaid();

    @Query(value = """
    SELECT EXTRACT(MONTH FROM c.paid_at) AS month,
           COALESCE(SUM(c.amount), 0) AS total
    FROM commissions c
    WHERE c.paid = true
      AND EXTRACT(YEAR FROM c.paid_at) = :year
    GROUP BY EXTRACT(MONTH FROM c.paid_at)
    ORDER BY EXTRACT(MONTH FROM c.paid_at)
""", nativeQuery = true)
    List<MonthlyCommissionProjection> getMonthlyCommission(@Param("year") int year);
}
