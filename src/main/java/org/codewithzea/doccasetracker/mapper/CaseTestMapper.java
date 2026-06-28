package org.codewithzea.doccasetracker.mapper;

import org.codewithzea.doccasetracker.dto.response.CaseTestResponse;
import org.codewithzea.doccasetracker.entity.CaseTest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CaseTestMapper {

    @Mapping(source = "test.id", target = "testId")
    @Mapping(source = "testNameAtTime", target = "testName")
    @Mapping(source = "priceAtTime", target = "price")
    @Mapping(source = "commissionAtTime", target = "commission")
    CaseTestResponse toResponse(CaseTest caseTest);
}
