import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class Main {

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static void main(String[] args) {
        HashMap<LocalDateTime, LocalDateTime> busyTimes = new HashMap<>();

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
            BufferedReader br = new BufferedReader(new FileReader("src/calendar.csv"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] array = line.split(",");
                if (array.length != 3) {
                    continue;
                }
                LocalDateTime startTime = LocalDateTime.parse(array[1].trim(), formatter);
                LocalDateTime endTime = LocalDateTime.parse(array[2].trim(), formatter);

                boolean overlap = false;
                for (Map.Entry<LocalDateTime, LocalDateTime> entry : busyTimes.entrySet()) {
                    LocalDateTime busyStart = entry.getKey();
                    LocalDateTime busyEnd = entry.getValue();
                    if (startTime.isBefore(busyEnd) && busyStart.isBefore(endTime)) {
                        overlap = true;
                        busyTimes.remove(busyStart);
                        if (startTime.isBefore(busyEnd)) {
                            busyTimes.put(busyStart, endTime);
                        } else {
                            busyTimes.put(startTime, busyEnd);
                        }
                        break;
                    }
                }
                if (!overlap) busyTimes.put(startTime, endTime);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Duration maxDuration = Duration.ZERO;
        LocalDateTime maxStartTime = LocalDateTime.now();
        LocalDateTime maxEndTime = LocalDateTime.now();
        for (int i = 0; i < 7; i++) {
            LocalDateTime startTime = LocalDate.now().plusDays(i).atTime(8, 0);
            LocalDateTime endTime = LocalDate.now().plusDays(i).atTime(22, 0);
            for (Map.Entry<LocalDateTime, LocalDateTime> entry : busyTimes.entrySet()) {
                LocalDateTime busyStart = entry.getKey();
                LocalDateTime busyEnd = entry.getValue();
                if (startTime.isBefore(busyEnd) && busyStart.isBefore(endTime)) {
                    if (Duration.between(startTime, busyStart).compareTo(Duration.between(busyEnd, endTime)) > 1) {
                        endTime = busyStart;
                    } else {
                        startTime = busyEnd;
                    }
                }
            }
            Duration dayDuration = Duration.between(startTime, endTime);

            if (dayDuration.compareTo(maxDuration) > 0) {
                maxDuration = dayDuration;
                maxStartTime = startTime;
                maxEndTime = endTime;
            }

            System.out.println(startTime + " - " + endTime + " : "  + dayDuration.toHours());
        }

        System.out.println("\nLARGEST BLOCK");
        System.out.println(maxStartTime + " - " + maxEndTime + " : " + maxDuration.toHours());
    }
}
