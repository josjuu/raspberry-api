package classes;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author josmu
 */
public class Time {

    public static final String startTime = "15:02:00";
    public static final String endTime = "15:03:30";

    public static String getCurrentTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        return dtf.format(now);
    }

    public static String getTimeStamp() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime now = LocalDateTime.now();

        return dtf.format(now);
    }

    public static boolean isTimeBetweenTimes(LocalTime currentTime) {
        return (currentTime.isAfter(LocalTime.parse(startTime)) && currentTime.isBefore(LocalTime.parse(endTime)));
    }
}
