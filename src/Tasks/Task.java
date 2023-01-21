package Tasks;

public class Task {
    //Я понял. Потому что обновление задачи в списке tasks происходит за счет создания нового объекта, а не изменения
    // полей старого.
    private final String name;
    private final String description;
    private Status status;

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

    @Override
    public String toString(){
        return "Имя: " + getName() + "\nОписание: " + getDescription() + "\nСтатус: " + getStatus();
    }


}
