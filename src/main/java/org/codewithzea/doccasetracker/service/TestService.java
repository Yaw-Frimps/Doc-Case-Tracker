package org.codewithzea.doccasetracker.service;




import org.codewithzea.doccasetracker.dto.request.CreateTestRequest;
import org.codewithzea.doccasetracker.dto.request.UpdateTestRequest;
import org.codewithzea.doccasetracker.dto.response.TestResponse;
import org.springframework.data.domain.Page;

import java.awt.print.Pageable;
import java.util.List;

public interface TestService {

    TestResponse create(CreateTestRequest dto);

    List<TestResponse> getAll();

    TestResponse getById(String id);

    TestResponse update(String id, UpdateTestRequest dto);

    void delete(String id);

    TestResponse activate(String id);

    TestResponse deactivate(String id);
}