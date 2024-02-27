package dev.twiceb.userservice.repository;

import dev.twiceb.common.enums.TimePeriod;
import dev.twiceb.userservice.model.UserStatistic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserStatisticRepository extends JpaRepository<UserStatistic, Long> {
    Optional<UserStatistic> findFirstByIntervalTypeOrderByCreatedDateDesc(TimePeriod intervalType);
}
