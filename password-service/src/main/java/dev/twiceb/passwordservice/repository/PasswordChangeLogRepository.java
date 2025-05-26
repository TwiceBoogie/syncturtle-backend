package dev.twiceb.passwordservice.repository;

import dev.twiceb.passwordservice.model.PasswordChangeLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordChangeLogRepository extends JpaRepository<PasswordChangeLog, Long> {

    // @Query("SELECT AVG(count_result) AS updates_count " +
    // "FROM (" +
    // " SELECT COUNT(pcl) AS count_result" +
    // " FROM PasswordChangeLog pcl" +
    // " WHERE pcl.id = :policyId" +
    // " AND change_date >= :start_date" +
    // " GROUP BY pcl.id" +
    // ") subquery")
    // @Query("SELECT pcl FROM PasswordChangeLog pcl")
}
