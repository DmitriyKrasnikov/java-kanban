package Tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;

public class Epic extends Task {
    public HashMap<Integer, Subtask> subtasks;

    public Epic(String name, String description, Status status, HashMap<Integer, Subtask> subtasks) {
        super(name, description, status);
        this.subtasks = subtasks;
    }

    @Override
    public Duration getDuration() {
        return Duration.between(getStartTime(), getEndTime());
    }

    @Override
    public LocalDateTime getStartTime() {
        LocalDateTime localDateTime = super.getStartTime();
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
        LocalDateTime localDateTime = super.getEndTime();
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
