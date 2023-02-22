package Tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task implements Comparable<Task> {
    private final String name;
    private final String description;
    private Status status;
    private static int generalId = 1000;
    private int ownId = generalId + 1;
    private Duration duration = Duration.ofMinutes(0);
    private LocalDateTime startTime = LocalDateTime.of(10000, 1, 1, 1, 1);



    public Task(String name, String description, Status status, Duration duration, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }

    // Этот конструктор сделал для эпиков, так как у них время расчетное
    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }
    public static void setGeneralId(int generalId) {
        Task.generalId = generalId;
    }
    public void setOwnId(){
        ownId = generalId+1;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
    public int getId() { return ownId;}
    public static void setId() { Task.generalId = Task.generalId + 1;}

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setStartTime(LocalDateTime localDateTime) {
        this.startTime = localDateTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return startTime.plusMinutes(duration.toMinutes());
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
        String startTime;
        String endTime;
        if (getStartTime().toString().
                equals(LocalDateTime.of(10000, 1, 1, 1, 1).toString())) {
            startTime = "Время старта не задано";
            endTime = "Невозможно рассчитать время окончания";
        } else {
            startTime = getStartTime().format(formatter);
            endTime = getEndTime().format(formatter);
        }
        return "Имя: " + getName() + "\nОписание: " + getDescription() + "\nСтатус: " + getStatus()
                + "\nДата старта: " + startTime + "\nДата окончания: " + endTime;
    }

    @Override
    public int compareTo(Task o) {

        if (o.getStartTime().isBefore(this.getStartTime())) {
            return 1;
        } else if (this.getStartTime().isBefore(o.getStartTime()) || !(this.equals(o))) {
            return -1;
        } else
            return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return name.equals(task.name) && description.equals(task.description) && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, status);
    }
}
