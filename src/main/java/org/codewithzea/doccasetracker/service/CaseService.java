package org.codewithzea.doccasetracker.service;

import org.codewithzea.doccasetracker.dto.request.CreateCaseRequest;
import org.codewithzea.doccasetracker.dto.request.UpdateCaseRequest;
import org.codewithzea.doccasetracker.dto.response.CaseResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CaseService {

    CaseResponse createCase(CreateCaseRequest request);

    CaseResponse updateCase(
            String caseId,
            UpdateCaseRequest request
    );

    void deleteCase(String caseId);

    CaseResponse getCaseById(String caseId);

    Page<CaseResponse> getAllCases(Pageable pageable);
}
