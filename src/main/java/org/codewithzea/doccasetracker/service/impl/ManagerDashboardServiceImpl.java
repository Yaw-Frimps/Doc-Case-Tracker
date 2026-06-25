package org.codewithzea.doccasetracker.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.codewithzea.doccasetracker.dto.MonthlyCommissionProjection;
import org.codewithzea.doccasetracker.dto.response.*;
import org.codewithzea.doccasetracker.entity.DoctorStatus;
import org.codewithzea.doccasetracker.repository.CaseRepository;
import org.codewithzea.doccasetracker.repository.CommissionRepository;
import org.codewithzea.doccasetracker.repository.DoctorRepository;
import org.codewithzea.doccasetracker.service.ManagerDashboardService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ManagerDashboardServiceImpl implements ManagerDashboardService{

    private final DoctorRepository doctorRepository;
    private final CaseRepository caseRepository;
    private final CommissionRepository commissionRepository;

    @Override
    @Cacheable("managerDashboard")
    public DashboardResponse getDashboard() {
        int year = LocalDate.now().getYear();
        int month = LocalDate.now().getMonthValue();

        Long totalDoctors =
                doctorRepository.countByDeletedFalse();

        Long activeDoctors =
                doctorRepository.countByDeletedFalseAndStatus(
                        DoctorStatus.ACTIVE);

        Long monthlyCases =
                caseRepository.countCasesForMonth(year, month);

        Long yearlyCases =
                caseRepository.countCasesForYear(year);

        BigDecimal revenue =
                caseRepository.getTotalRevenue();

        BigDecimal commission =
                commissionRepository.getTotalCommissionPaid();

        List<Object[]> doctorData =
                caseRepository.findTopReferringDoctors(
                        PageRequest.of(0,1));

        TopDoctorResponse topDoctor = null;

        if (!doctorData.isEmpty()) {

            Object[] row = doctorData.get(0);

            topDoctor = TopDoctorResponse.builder()
                    .doctorId((String) row[0])
                    .doctorName((String) row[1])
                    .totalCases((Long) row[2])
                    .build();
        }

        List<DoctorReferralResponse> referrals =
                caseRepository.getDoctorReferrals()
                        .stream()
                        .map(r ->
                                DoctorReferralResponse.builder()
                                        .doctorId((String) r[0])
                                        .doctorName((String) r[1])
                                        .totalCases((Long) r[2])
                                        .build()
                        )
                        .toList();

        List<TestStatisticsResponse> tests =
                caseRepository.getTestStatistics()
                        .stream()
                        .map(r ->
                                TestStatisticsResponse.builder()
                                        .testName((String) r[0])
                                        .totalCases((Long) r[1])
                                        .build()
                        )
                        .toList();

        Map<Integer, BigDecimal> revenueMap =
                caseRepository.getMonthlyRevenue(year)
                        .stream()
                        .collect(Collectors.toMap(
                                r -> (Integer) r[0],
                                r -> (BigDecimal) r[1]
                        ));

        Map<Integer, BigDecimal> commissionMap =
                commissionRepository.getMonthlyCommission(year)
                        .stream()
                        .collect(Collectors.toMap(
                                MonthlyCommissionProjection::getMonth,
                                MonthlyCommissionProjection::getTotal
                        ));

        List<MonthlyRevenueChartResponse> chart =
                IntStream.rangeClosed(1,12)
                        .mapToObj(m ->
                                MonthlyRevenueChartResponse.builder()
                                        .month(Month.of(m).name())
                                        .revenue(
                                                revenueMap.getOrDefault(
                                                        m,
                                                        BigDecimal.ZERO
                                                ))
                                        .commission(
                                                commissionMap.getOrDefault(
                                                        m,
                                                        BigDecimal.ZERO
                                                ))
                                        .build()
                        )
                        .toList();
        return DashboardResponse.builder()
                .totalDoctors(totalDoctors)
                .activeDoctors(activeDoctors)
                .totalCasesThisMonth(monthlyCases)
                .totalCasesThisYear(yearlyCases)
                .totalRevenue(revenue)
                .totalCommissionPaid(commission)
                .topReferringDoctor(topDoctor)
                .doctorReferrals(referrals)
                .testStatistics(tests)
                .revenueChart(chart)
                .build();
    }
}
