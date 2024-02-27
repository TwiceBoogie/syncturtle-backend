package dev.twiceb.userservice.repository;

import dev.twiceb.userservice.model.PasswordResetOtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface PasswordResetOtpRepository extends JpaRepository<PasswordResetOtp, Long> {

    @Query("SELECT CASE WHEN :currentTime < pso.expirationTime THEN true ELSE false END " +
            "FROM PasswordResetOtp pso " +
            "WHERE pso.hashedOtp = :hashedOtp")
    boolean isPasswordResetOtpExpired(@Param("currentTime") LocalDateTime currentTime, @Param("hashedOtp") String hashedOtp);

    @Query("SELECT CASE WHEN COUNT(pso) > 0 THEN true ELSE false END " +
            "FROM PasswordResetOtp pso " +
            "WHERE pso.hashedOtp = :hashedOtp")
    boolean isPasswordResetOtpExist(@Param("hashedOtp") String hashedOtp);

    @Query("SELECT pso FROM PasswordResetOtp pso WHERE pso.hashedOtp = :hashedOtp")
    PasswordResetOtp findByHashedOtp(@Param("hashedOtp") String hashedOtp);
}
