package org.codewithzea.doccasetracker.service;




import org.codewithzea.doccasetracker.dto.request.CreateTestRequest;
import org.codewithzea.doccasetracker.dto.request.UpdateTestRequest;
import org.codewithzea.doccasetracker.dto.response.TestResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface TestService {

    TestResponse create(CreateTestRequest dto);

    List<TestResponse> getAll();

    TestResponse getById(String id);

    TestResponse update(String id, UpdateTestRequest dto);


    void delete(String id);

    Page<TestResponse> getInactiveTests(Pageable pageable);


    TestResponse activate(String id);

    TestResponse deactivate(String id);
}