package org.codewithzea.doccasetracker.repository;

import org.codewithzea.doccasetracker.entity.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpVerificationRepository extends JpaRepository<OtpVerification, String> {
    Optional<OtpVerification> findByEmailAndOtp(String email, String otp);
    Optional<OtpVerification> findFirstByEmailOrderByCreatedAtDesc(String email);
}
