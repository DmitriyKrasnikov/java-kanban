package Manager;

import Tasks.Epic;
import Tasks.Status;
import Tasks.Subtask;
import Tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

public interface TaskManager {

    TreeSet<Task> getPrioritizedTasks();

    HashMap<Integer, Task> taskHashMap();

    HashMap<Integer, Epic> epicHashMap();

    HashMap<Integer, Subtask> subtaskHashMap();

    List<Task> getHistory();

    Task taskMaker(String name, String description, Status status, Duration duration, LocalDateTime startTime);

    void taskAdd(Task task);

    void taskAddWithId(int id, Task task);

    void taskListAllTasks();

    void taskGetById(int number);

    void taskRemove(int number);

    void taskDeleteAll();

    void taskUpdate(Task task, int number);

    Epic epicMaker(int id, String name, String description);

    Subtask subtaskMaker(String name, String description, Status status, Duration duration, LocalDateTime startTime);

    void epicAdd(Epic epic);

    void epicAddWhithId(int id, Epic epic);

    void subtaskAdd(int number, Subtask subtask);

    void subtaskAddWithId(int id, int number, Subtask subtask);

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
