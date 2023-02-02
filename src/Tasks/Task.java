package Tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task implements Comparable<Task> {
    private final String name;
    private final String description;
    private Status status;
    private Duration duration = Duration.ofMinutes(0);
    //В тз указано, что время должно представлять из себя день и продолжительность в минутах, то есть неважно когда
    // задача начинается, отсчет всегда ведется от 00.00. Я, может, неправильно понял, но если я понял правильно, то
    // мне кажется необходимым добавить полную дату со временем.
    private LocalDateTime startTime = LocalDateTime.of(10000, 1, 1, 1, 1);

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

    public void setStatus(Status status) {
        this.status = status;
    }

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
}
