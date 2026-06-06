package org.codewithzea.doccasetracker.mapper;

import org.codewithzea.doccasetracker.dto.response.DoctorResponse;
import org.codewithzea.doccasetracker.entity.Doctor;
import org.codewithzea.doccasetracker.entity.Specialty;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DoctorMapper {


    @Mapping(target = "specialty", expression = "java(mapSpecialty(doctor.getSpecialization()))")
    DoctorResponse toResponse(Doctor doctor);

    default String mapSpecialty(Specialty specialty) {
        return specialty != null ? specialty.getName() : null;
    }
}
