package org.codewithzea.doccasetracker.mapper;

import org.codewithzea.doccasetracker.entity.User;
import org.codewithzea.doccasetracker.dto.response.UserResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "role", source = "role.name")
    UserResponseDto toDto(User user);
}
