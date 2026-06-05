package org.codewithzea.doccasetracker.mapper;

import org.codewithzea.doccasetracker.dto.response.ApprovalStatusResponse;
import org.codewithzea.doccasetracker.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface ApprovalStatusMapper {

    @Mapping(target = "role", expression = "java(user.getRole().getName().name())")
    @Mapping(target = "approvalStatus", expression = "java(user.getApprovalStatus().name())")
    ApprovalStatusResponse toResponse(User user);
}
