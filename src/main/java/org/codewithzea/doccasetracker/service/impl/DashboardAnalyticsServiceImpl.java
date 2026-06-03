package org.codewithzea.doccasetracker.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.codewithzea.doccasetracker.dto.response.DashboardAnalyticsResponse;
import org.codewithzea.doccasetracker.entity.RoleType;
import org.codewithzea.doccasetracker.repository.UserRepository;
import org.codewithzea.doccasetracker.service.DashboardService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardAnalyticsServiceImpl implements DashboardService {

    private final UserRepository userRepository;

    @Override
    @Cacheable(value = "dashboardAnalytics")
    public DashboardAnalyticsResponse getDashboardAnalytics() {

        log.debug("Fetching dashboard analytics");

        long totalUsers = userRepository.count();

        long totalWorkers = userRepository.countByRoleName(RoleType.ROLE_WORKER);

        long totalManagers = userRepository.countByRoleName(RoleType.ROLE_MANAGER);


        log.info(
                "Dashboard analytics generated: users={}, workers={}, managers={}",
                totalUsers,
                totalWorkers,
                totalManagers
        );


        return DashboardAnalyticsResponse.builder()
                .totalUsers(totalUsers)
                .totalWorkers(totalWorkers)
                .totalManagers(totalManagers)
                .build();
    }
}
