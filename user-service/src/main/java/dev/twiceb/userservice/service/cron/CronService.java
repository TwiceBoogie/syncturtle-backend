package dev.twiceb.userservice.service.cron;

import dev.twiceb.common.enums.TimePeriod;
import dev.twiceb.userservice.domain.model.UserStatistic;
import dev.twiceb.userservice.domain.repository.UserRepository;
import dev.twiceb.userservice.domain.repository.UserStatisticRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CronService {

    private final UserRepository userRepository;
    private final UserStatisticRepository userStatisticRepository;


    // every day at midnight
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void calculateDailyRegisteredUserChange() {
        Instant last24Hours = Instant.now().minus(24, ChronoUnit.HOURS);
        userStatisticRepository.save(calculateRegisteredUserChange(last24Hours, TimePeriod.DAILY));
    }

    @Scheduled(cron = "0 0 0 * * 1")
    @Transactional
    public void calculateWeeklyRegisteredUserChange() {
        Instant lastWeek = Instant.now().minus(1, ChronoUnit.WEEKS);
        userStatisticRepository.save(calculateRegisteredUserChange(lastWeek, TimePeriod.WEEKLY));
    }

    @Scheduled(cron = "0 0 0 1 * ?")
    @Transactional
    public void calculateMonthlyRegisteredUserChange() {
        Instant lastMonth = Instant.now().minus(1, ChronoUnit.MONTHS);
        userStatisticRepository.save(calculateRegisteredUserChange(lastMonth, TimePeriod.MONTHLY));
    }

    @Scheduled(cron = "0 0 0 1 1 ?")
    @Transactional
    public void calculateYearlyRegisteredUserChange() {
        Instant lastYear = Instant.now().minus(1, ChronoUnit.YEARS);
        userStatisticRepository.save(calculateRegisteredUserChange(lastYear, TimePeriod.YEARLY));
    }

    private UserStatistic calculateRegisteredUserChange(Instant startTime,
            TimePeriod intervalType) {
        UserStatistic userStatistic = new UserStatistic();
        int registeredUserCount = userRepository.countUsersByTimePeriod(startTime);
        int registeredActiveUserCount = userRepository.countVerifiedUsersByTimePeriod(startTime);
        Optional<UserStatistic> userStatisticFromDb =
                userStatisticRepository.findFirstByIntervalTypeOrderByCreatedAtDesc(intervalType);

        if (userStatisticFromDb.isPresent()) {
            int difference = registeredUserCount - userStatisticFromDb.get().getRegisteredUsers();
            if (difference == 0) {
                userStatistic.setRegisteredUsersChange(0);
            } else {
                userStatistic.setRegisteredUsersChange(
                        ((double) difference / registeredUserCount) * 100);
            }
        }
        userStatistic.setIntervalType(intervalType);
        userStatistic.setRegisteredUsers(registeredUserCount);
        userStatistic.setActiveUserCount(registeredActiveUserCount);

        return userStatistic;
    }
}
