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

    //Согласно тз продолжительность эпика - это сумма продолжительности всех его сабтасков. Предположим, эпик содержит
    // две подзадачи начинающиеся 4 и 5 числа, каждая продолжительностью по 30 минут. То исходя из логики тз, эпик
    // начнется 4 числа и закончится через час. В то время как одна подзадача еще не началась. Я сделал продолжительностью
    // эпика разницу между началом первой задачи и концом последней.
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
