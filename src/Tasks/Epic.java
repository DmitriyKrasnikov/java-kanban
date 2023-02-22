package Tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;

public class Epic extends Task {
    public HashMap<Integer, Subtask> subtasks;

    private static int generalId = 2000;
    private int epicId = generalId + 1;

    public Epic(String name, String description, Status status, HashMap<Integer, Subtask> subtasks) {
        super(name, description, status);
        this.subtasks = subtasks;
    }

    public static void setGeneralId(int generalId) {
        Epic.generalId = generalId;
    }

    public int getId() { return epicId;}
    public void setEpicId() { epicId = generalId+1;}

    public static void setId() {
        Epic.generalId = Epic.generalId + 1;
    }

    @Override
    public Duration getDuration() {
        return Duration.between(getStartTime(), getEndTime());
    }

    @Override
    public LocalDateTime getStartTime() {
        LocalDateTime localDateTime = LocalDateTime.of(10000, 1, 1, 1, 1);
        for (Integer id : subtasks.keySet()) {
            if (localDateTime == super.getStartTime()) {
                localDateTime = subtasks.get(id).getStartTime();
            } else {
                if (subtasks.get(id).getStartTime().isBefore(localDateTime)) {
                    localDateTime = subtasks.get(id).getStartTime();
                }
            }
        }
        return localDateTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        LocalDateTime localDateTime = LocalDateTime.of(1000, 1, 1, 1, 1);
        for (Integer id : subtasks.keySet()) {
            if (localDateTime == super.getStartTime()) {
                localDateTime = subtasks.get(id).getEndTime();
            } else {
                if (subtasks.get(id).getEndTime().isAfter(localDateTime)) {
                    localDateTime = subtasks.get(id).getEndTime();
                }
            }
        }
        return localDateTime;
    }
}
