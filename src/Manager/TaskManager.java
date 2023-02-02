package Manager;

import Tasks.Epic;
import Tasks.Status;
import Tasks.Subtask;
import Tasks.Task;

import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

public interface TaskManager {

    TreeSet<Task> getPrioritizedTasks();

    HashMap<Integer, Task> taskHashMap();

    HashMap<Integer, Epic> epicHashMap();

    List<Task> getHistory();

    Task taskMaker(String name, String description, Status status);

    void taskAdd(int id, Task task);

    void taskListAllTasks();

    void taskGetById(int number);

    void taskRemove(int number);

    void taskDeleteAll();

    void taskUpdate(Task task, int number);

    Epic epicMaker(int id, String name, String description);

    Subtask subtaskMaker(String name, String description, Status status);

    void epicAdd(int id, Epic epic);

    void subtaskAdd(int id, int number, Subtask subtask);

    void epicListAllTasks();

    void subtaskListAllTasks(int number);

    void epicDeleteAll();

    void subtaskDeleteAll(int number);

    void epicGetById(int number);

    void subtaskGetById(int number1, int number2);

    void epicUpdate(Epic epic, int number);

    void subtaskUpdate(Subtask subtask, int epicNumber, int subtaskNumber);

    void epicRemove(int number);

    void subtaskRemove(int epicNumber, int subtaskNumber);

    void takeEpicStatus(int number);

    boolean checkEpicsHashMap(int number);
}
