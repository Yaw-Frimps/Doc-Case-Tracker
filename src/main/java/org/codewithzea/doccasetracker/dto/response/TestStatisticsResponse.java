package org.codewithzea.doccasetracker.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TestStatisticsResponse {

    private String testName;

    private Long totalCases;
}
