package Tasks;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {

    public Subtask(String name, String description, Status status, Duration duration, LocalDateTime startTime) {
        super(name, description, status, duration, startTime);
    }
}
