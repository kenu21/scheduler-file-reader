package com.keniu;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.EnumSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
class ActionScheduler {

    @Value("${csv.path}")
    private String csvFilePath;

    @Scheduled(cron = "${scheduler.cron}")
    public void runScheduler() {
        ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of("Africa/Lagos"));
        LocalTime currentTime = zonedDateTime.toLocalTime().truncatedTo(ChronoUnit.MINUTES);
        DayOfWeek currentDay = zonedDateTime.getDayOfWeek();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length != 2) {
                    continue;
                }
                LocalTime scheduledTime = LocalTime.parse(parts[0]);
                int bitmask = Integer.parseInt(parts[1]);
                Set<DayOfWeek> scheduledDays = getDaysFromBitmask(bitmask);
                
                if (scheduledTime.equals(currentTime) && scheduledDays.contains(currentDay)) {
                    executeAction(scheduledTime, currentDay);
                }
            }
        } catch (IOException e) {
            //todo add logs in real project
            e.printStackTrace();
        }
    }

    private Set<DayOfWeek> getDaysFromBitmask(int bitmask) {
        Set<DayOfWeek> days = EnumSet.noneOf(DayOfWeek.class);
        DayOfWeek[] weekDays = DayOfWeek.values();
        for (int i = 0; i < weekDays.length; i++) {
            if ((bitmask & (1 << i)) != 0) {
                days.add(weekDays[i]);
            }
        }
        return days;
    }

    private void executeAction(LocalTime time, DayOfWeek day) {
        //todo implement action in real project
        System.out.printf("Executing action at %s on %s%n", time, day);
    }
}