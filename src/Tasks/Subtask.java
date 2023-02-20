package Tasks;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {

    private static int generalId = 3000;
    private final int subId = generalId + 1;
    private int epicMasterId = 0;

    public Subtask(String name, String description, Status status, Duration duration, LocalDateTime startTime) {
        super(name, description, status, duration, startTime);
    }

    public static void setGeneralId(int generalId) {
        Subtask.generalId = generalId;
    }

    public int getId() {
        return subId;
    }
    public static void setId() {
        Subtask.generalId = Subtask.generalId + 1;
    }

    public int getEpicMasterId() {
        return epicMasterId;
    }

    public void setEpicMasterId(int epicMasterId) {
        this.epicMasterId = epicMasterId;
    }
}
