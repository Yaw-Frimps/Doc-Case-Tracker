package org.codewithzea.doccasetracker.repository;

import org.codewithzea.doccasetracker.entity.ApprovalStatus;
import org.codewithzea.doccasetracker.entity.RoleType;
import org.codewithzea.doccasetracker.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);

    Page<User> findAllByApprovalStatus(
            ApprovalStatus approvalStatus,
            Pageable pageable
    );

    long countByRoleName(RoleType roleType);
}
