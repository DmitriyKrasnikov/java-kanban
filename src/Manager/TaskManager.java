package Manager;
import Tasks.Status;
import Tasks.Task;

import java.util.HashMap;

public class TaskManager {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    // Ответ такой же как и в EpicManager.

    public Task taskMaker(String name, String description, Status status) {
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
                System.out.println(task);
            }
        }
    }

    public void taskGetById(int number) {
        if (tasks.containsKey(number)){
        Task task = tasks.get(number);
            System.out.println(task);

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

