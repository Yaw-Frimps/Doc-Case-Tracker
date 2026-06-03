package org.codewithzea.doccasetracker.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardAnalyticsResponse {
    private long totalUsers;
    private long totalWorkers;
    private long totalManagers;
}
