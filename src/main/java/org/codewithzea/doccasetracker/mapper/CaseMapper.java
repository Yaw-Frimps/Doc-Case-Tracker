package org.codewithzea.doccasetracker.mapper;


import org.codewithzea.doccasetracker.dto.response.CaseResponse;
import org.codewithzea.doccasetracker.entity.Cases;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface CaseMapper {

    @Mapping(target = "doctorId", source = "doctor.doctorId")
    @Mapping(target = "doctorName", source = "doctor.fullName")

    @Mapping(source = "test.id", target = "testId")
    @Mapping(source = "test.testName", target = "testName")
    CaseResponse toResponse(Cases cases);
}
