package dev.twiceb.userservice.service.cron;

import dev.twiceb.common.enums.TimePeriod;
import dev.twiceb.userservice.model.UserStatistic;
import dev.twiceb.userservice.repository.UserRepository;
import dev.twiceb.userservice.repository.UserStatisticRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
        LocalDateTime last24Hours = LocalDateTime.now().minusHours(24);
        userStatisticRepository.save(calculateRegisteredUserChange(last24Hours, TimePeriod.DAILY));
    }

    @Scheduled(cron = "0 0 0 * * 1")
    @Transactional
    public void calculateWeeklyRegisteredUserChange() {
        LocalDateTime lastWeek = LocalDateTime.now().minusWeeks(1);
        userStatisticRepository.save(calculateRegisteredUserChange(lastWeek, TimePeriod.WEEKLY));
    }

    @Scheduled(cron = "0 0 0 1 * ?")
    @Transactional
    public void calculateMonthlyRegisteredUserChange() {
        LocalDateTime lastMonth = LocalDateTime.now().minusMonths(1);
        userStatisticRepository.save(calculateRegisteredUserChange(lastMonth, TimePeriod.MONTHLY));
    }

    @Scheduled(cron = "0 0 0 1 1 ?")
    @Transactional
    public void calculateYearlyRegisteredUserChange() {
        LocalDateTime lastYear = LocalDateTime.now().minusYears(1);
        userStatisticRepository.save(calculateRegisteredUserChange(lastYear, TimePeriod.YEARLY));
    }

    private UserStatistic calculateRegisteredUserChange(LocalDateTime startTime, TimePeriod intervalType) {
        UserStatistic userStatistic = new UserStatistic();
        int registeredUserCount = userRepository.countUsersByTimePeriod(startTime);
        int registeredActiveUserCount = userRepository.countVerifiedUsersByTimePeriod(startTime);
        Optional<UserStatistic> userStatisticFromDb = userStatisticRepository.findFirstByIntervalTypeOrderByCreatedDateDesc(
                intervalType
        );

        if (userStatisticFromDb.isPresent()) {
            int difference = registeredUserCount - userStatisticFromDb.get().getRegisteredUsers();
            if (difference == 0) {
                userStatistic.setRegisteredUsersChange(0);
            } else {
                userStatistic.setRegisteredUsersChange(((double) difference / registeredUserCount) * 100);
            }
        }
        userStatistic.setIntervalType(intervalType);
        userStatistic.setRegisteredUsers(registeredUserCount);
        userStatistic.setActiveUserCount(registeredActiveUserCount);

        return userStatistic;
    }
}
