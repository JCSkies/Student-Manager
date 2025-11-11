package cs151.application.services;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class Logger {
    private final Path dir = Paths.get("localData");
    private final Path file = dir.resolve("log.txt");
    private static final Logger instance = new Logger();

    private Logger() {
        try {
            Files.createDirectories(dir);
            if (Files.notExists(file)) {
                Files.createFile(file);
            } else {
                Files.writeString(file, "", StandardCharsets.UTF_8,
                        StandardOpenOption.TRUNCATE_EXISTING);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static Logger getInstance() {
        return instance;
    }

    public void log(Exception e) {
        ZonedDateTime now = ZonedDateTime.now();
        String timeStamp = now.format(DateTimeFormatter.ofPattern("<yyyy-MM-dd HH:mm:ss z>"));

        try {
            Files.writeString(file, timeStamp + Arrays.toString(e.getStackTrace()) + "\n", StandardCharsets.UTF_8,
                    StandardOpenOption.APPEND);
        } catch (Exception error) {
            System.out.println(error.getMessage());
        }
    }

    public void log(String msg) {
        ZonedDateTime now = ZonedDateTime.now();
        String timeStamp = now.format(DateTimeFormatter.ofPattern("<yyyy-MM-dd HH:mm:ss z>"));

        try {
            Files.writeString(file, timeStamp + msg + "\n", StandardCharsets.UTF_8,
                    StandardOpenOption.APPEND);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
}
