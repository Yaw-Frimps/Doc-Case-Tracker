package org.codewithzea.doccasetracker.mapper;


import org.codewithzea.doccasetracker.dto.request.CreateTestRequest;
import org.codewithzea.doccasetracker.dto.request.UpdateTestRequest;
import org.codewithzea.doccasetracker.dto.response.TestResponse;
import org.codewithzea.doccasetracker.entity.Test;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface TestMapper {

    Test toEntity(CreateTestRequest dto);

    TestResponse toDTO(Test test);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(UpdateTestRequest dto, @MappingTarget Test test);
}