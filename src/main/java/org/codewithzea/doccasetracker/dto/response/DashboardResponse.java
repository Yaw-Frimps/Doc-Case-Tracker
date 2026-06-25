package org.codewithzea.doccasetracker.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class DashboardResponse {

    private Long totalDoctors;
    private Long activeDoctors;

    private Long totalCasesThisMonth;
    private Long totalCasesThisYear;

    private Long totalTestsThisMonth;
    private Long totalTestsThisYear;

    private BigDecimal totalRevenue;
    private BigDecimal totalCommissionPaid;

    private TopDoctorResponse topReferringDoctor;

    private List<MonthlyRevenueChartResponse> revenueChart;

    private List<TestStatisticsResponse> testStatistics;

    private List<DoctorReferralResponse> doctorReferrals;
}
