package org.codewithzea.doccasetracker.repository;

import org.codewithzea.doccasetracker.entity.Role;
import org.codewithzea.doccasetracker.entity.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(RoleType name);
}
