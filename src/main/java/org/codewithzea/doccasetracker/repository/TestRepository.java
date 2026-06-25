package org.codewithzea.doccasetracker.repository;

import org.codewithzea.doccasetracker.entity.Test;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TestRepository extends JpaRepository<Test, String> {
    Optional<Test> findById(String s);

    boolean existsByTestName(String testName);
}
