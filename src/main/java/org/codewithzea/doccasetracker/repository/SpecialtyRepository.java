package org.codewithzea.doccasetracker.repository;

import org.codewithzea.doccasetracker.entity.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpecialtyRepository extends JpaRepository<Specialty, String> {

    Optional<Specialty> findByNameIgnoreCase(String name);
}
