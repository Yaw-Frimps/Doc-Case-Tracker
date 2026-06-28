package org.codewithzea.doccasetracker.mapper;


import org.codewithzea.doccasetracker.dto.response.CaseResponse;
import org.codewithzea.doccasetracker.entity.Cases;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {CaseTestMapper.class}
)
public interface CaseMapper {

    @Mapping(target = "doctorId", source = "doctor.doctorId")
    @Mapping(target = "doctorName", source = "doctor.fullName")
    @Mapping(target = "tests", source = "caseTests")
    CaseResponse toResponse(Cases cases);
}
