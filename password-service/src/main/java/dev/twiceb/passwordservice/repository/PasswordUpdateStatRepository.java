package dev.twiceb.passwordservice.repository;

import dev.twiceb.common.enums.TimePeriod;
import dev.twiceb.passwordservice.model.PasswordUpdateStat;
import dev.twiceb.passwordservice.repository.projection.PasswordUpdateStatProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PasswordUpdateStatRepository extends JpaRepository<PasswordUpdateStat, Long> {

    @Query(value = "SELECT insert_update_stats(:timePeriod)", nativeQuery = true)
    void callInsertUpdateStats(@Param("timePeriod")TimePeriod timePeriod);

    @Query("SELECT pus FROM PasswordUpdateStat pus WHERE pus.intervalType = :timePeriod")
    Page<PasswordUpdateStatProjection> findAllByTimePeriod(@Param("timePeriod") TimePeriod timePeriod, Pageable pageable);
}
