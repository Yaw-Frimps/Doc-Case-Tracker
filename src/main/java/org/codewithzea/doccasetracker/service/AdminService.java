package org.codewithzea.doccasetracker.service;

import org.codewithzea.doccasetracker.dto.response.ApprovalStatusResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminService {
    ApprovalStatusResponse approveUser(String id);
    ApprovalStatusResponse rejectUser(String id);
    Page<ApprovalStatusResponse> getAllUsers(Pageable pageable);
    Page<ApprovalStatusResponse> getPendingUsers(Pageable pageable);
    Page<ApprovalStatusResponse> getApprovedUsers(Pageable pageable);
    void deleteUser(String id);
    ApprovalStatusResponse getUserById(String id);
}
