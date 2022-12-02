package Manager;
import Tasks.Task;

import java.util.HashMap;

public class TaskManager {
    HashMap<Integer, Task> tasks = new HashMap<>();

    public Task taskMaker(String name, String description, String status) {
        return new Task(name, description, status);
    }

    public void taskAdd(int id, Task task) {
        tasks.put(id, task);
    }

    public void taskListAllTasks() {
        if ((tasks.isEmpty())){
            System.out.println("Задач нет");
        }else {
            for (int id : tasks.keySet()) {
                Task task = tasks.get(id);
                System.out.println("Идентификатор " + id);
                System.out.println("Задача: " + task.getName());
                System.out.println("Описание: " + task.getDescription());
                System.out.println("Статус: " + task.getStatus());
            }
        }
    }

    public void taskGetById(int number) {
        if (tasks.containsKey(number)){
        Task task = tasks.get(number);
        System.out.println("Задача: " + task.getName());
        System.out.println("Описание: " + task.getDescription());
        System.out.println("Статус: " + task.getStatus());

        }else {
            System.out.println("Такой задачи не существует");
        }
    }

    public void taskRemove(int number) {
        if (tasks.containsKey(number)) {
            tasks.remove(number);
        }else {
            System.out.println("Такой задачи не существует");
        }
    }

    public void taskDeleteAll() {
        tasks.clear();
    }

    public void taskUpdate(Task task, int number) {
        tasks.remove(number);
        tasks.put(number, task);
    }
}

