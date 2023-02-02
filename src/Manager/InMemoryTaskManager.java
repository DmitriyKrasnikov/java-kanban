package Manager;

import Tasks.Status;
import Tasks.Task;
import Tasks.Epic;
import Tasks.Subtask;

import java.time.Duration;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();

    protected final HistoryManager historyManager = Managers.getDefaultHistory();

    protected final ArrayList<Task> allTasks = new ArrayList<>();
    //Изначально я планировал,что будет лишь TreeSet<Task> priority, но при добавлении подзадач в эпик, меняется
    // startTime в эпике, но так как эпик уже находится во множестве, то заново он не пересортировывается, в итоге
    // отсортировать не получается. Я пробовал удалять эпик при добавлении, но TreeSet не удаляет. Пробовал найти
    // ответы, током ничего не нашёл. Я думаю, что дело компараторе, который я туда передал. Или может стоит
    // переопределить equals или hashcode. В итоге задачи сначала складываются в ArrayList<Task> allTasks, а потом уже
    // сортируются в TreeSet<Task> priority;
    protected TreeSet<Task> priority;

    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        return priority = new TreeSet<>(allTasks);
    }

    //Про пересечения за О(1) я не до конца понял, предлагается создать HashMap где ключи это целый год разбитый на
    // интервалы по 15 минут?
    public boolean isIntersections(Task task) {
        boolean intersections = false;
        for (Task allTask : allTasks) {
            //Если время старта и конца одной задачи раньше времени старта и конца другой и наоборот, то задачи не
            // пересекаются. И учитываются задачи, у которых не задано время, а продолжительность равна нулю
            if (!(task.getStartTime().isBefore(allTask.getStartTime()) && task.getEndTime().isBefore(allTask.getEndTime())
                    || allTask.getStartTime().isBefore(task.getStartTime()) && allTask.getEndTime().isBefore(task.getEndTime())
                    || allTask.getStartTime().equals(task.getStartTime()) && allTask.getDuration().equals(Duration.ofMinutes(0)) &&
                    allTask.getDuration().equals(task.getDuration()))) {
                intersections = true;
            }
        }
        return intersections;
    }

    @Override
    public HashMap<Integer, Task> taskHashMap() {
        return tasks;
    }

    @Override
    public HashMap<Integer, Epic> epicHashMap() {
        return epics;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public Task taskMaker(String name, String description, Status status) {
        return new Task(name, description, status);
    }

    @Override
    public void taskAdd(int id, Task task) {
        if (isIntersections(task)) {
            System.out.println("Задача " + task.getName() + " не может выполняться одновременно с другой.");
        } else {
            tasks.put(id, task);
            allTasks.add(task);
        }
    }

    @Override
    public void taskListAllTasks() {
        if ((tasks.isEmpty())) {
            System.out.println("Задач нет");
        } else {
            for (int id : tasks.keySet()) {
                Task task = tasks.get(id);
                System.out.println("Идентификатор " + id);
                System.out.println(task);
            }
        }
    }

    @Override
    public void taskGetById(int number) {
        if (tasks.containsKey(number)) {
            Task task = tasks.get(number);
            historyManager.add(number, task);
            System.out.println(task);

        } else {
            System.out.println("Такой задачи не существует");
        }
    }

    @Override
    public void taskRemove(int number) {
        if (tasks.containsKey(number)) {
            allTasks.remove(tasks.get(number));
            tasks.remove(number);
            historyManager.remove(number);

        } else {
            System.out.println("Такой задачи не существует");
        }
    }

    @Override
    public void taskDeleteAll() {
        for (Integer id : tasks.keySet()) {
            allTasks.remove(tasks.get(id));
            historyManager.remove(id);
        }
        tasks.clear();
    }

    @Override
    public void taskUpdate(Task task, int number) {
        if (tasks.containsKey(number)) {
            allTasks.remove(tasks.get(number));
            tasks.remove(number);
            tasks.put(number, task);
            allTasks.add(task);
        }
    }

    @Override
    public Epic epicMaker(int id, String name, String description) {
        HashMap<Integer, Subtask> subtasks = new HashMap<>();
        if (epics.containsKey(id)) {
            subtasks = (epics.get(id)).subtasks;
        }
        return new Epic(name, description, Status.NEW, subtasks);
    }

    @Override
    public Subtask subtaskMaker(String name, String description, Status status) {
        return new Subtask(name, description, status);
    }

    @Override
    public void epicAdd(int id, Epic epic) {
        if (isIntersections(epic)) {
            System.out.println("Задача " + epic.getName() + " не может выполняться одновременно с другой.");
        } else {
            epics.put(id, epic);
            allTasks.add(epic);
        }
    }

    @Override
    public void subtaskAdd(int id, int number, Subtask subtask) {
        if (checkEpicsHashMap(number)) {
            System.out.println("Такого эпика не существует");
        } else {
            if (isIntersections(subtask)) {
                System.out.println("Задача " + subtask.getName() + " не может выполняться одновременно с другой.");
            } else {
                epics.get(number).subtasks.put(id, subtask);
                allTasks.add(subtask);
            }
        }
    }

    @Override
    public void epicListAllTasks() {
        if ((epics.isEmpty())) {
            System.out.println("Эпиков нет");
        } else {
            for (int id : epics.keySet()) {
                Epic epic = epics.get(id);
                System.out.println("Идентификатор " + id);
                System.out.println(epic);
            }
        }
    }

    @Override
    public void subtaskListAllTasks(int number) {
        if (checkEpicsHashMap(number)) {
            System.out.println("Такого эпика не существует, подзадач соответственно тоже");
        } else {
            Epic epic = epics.get(number);
            if (!(epic.subtasks.isEmpty())) {
                for (int id : epic.subtasks.keySet()) {
                    Subtask subtask = epic.subtasks.get(id);
                    System.out.println("Идентификатор " + id);
                    System.out.println(subtask);
                }
            }/*else {
                System.out.println("Такой подзадачи не существует, или эпик не содержит подзадач");
            }*/
        }
    }

    @Override
    public void epicDeleteAll() {
        for (Integer id : epics.keySet()) {
            allTasks.remove(epics.get(id));
            historyManager.remove(id);
        }
        epics.clear();
    }

    @Override
    public void subtaskDeleteAll(int number) {
        if (epics.containsKey(number)) {
            Epic epic = epics.get(number);
            for (Integer id : epic.subtasks.keySet()) {
                allTasks.remove(epic.subtasks.get(id));
                historyManager.remove(id);
            }
            epic.subtasks.clear();
        } else {
            System.out.println("Такого эпика не существует");
        }
    }

    @Override
    public void epicGetById(int number) {
        if (epics.containsKey(number)) {
            Epic epic = epics.get(number);
            System.out.println(epic);
            historyManager.add(number, epic);
        } else {
            System.out.println("Такого эпика не существует");
        }

    }

    @Override
    public void subtaskGetById(int epicNumber, int subNumber) {
        if (epics.containsKey(epicNumber)) {
            Epic epic = epics.get(epicNumber);
            if (epic.subtasks.containsKey(subNumber)) {
                Subtask subtask = epic.subtasks.get(subNumber);
                System.out.println(subtask);
                historyManager.add(subNumber, subtask);
            } else {
                System.out.println("Такой подзадачи не существует");
            }
        } else {
            System.out.println("Такого эпика не существует");
        }
    }

    @Override
    public void epicUpdate(Epic epic, int number) {
        if (epics.containsKey(number)) {
            allTasks.remove(epics.get(number));
            epics.remove(number);
            epics.put(number, epic);
            allTasks.add(epic);
        }
    }

    @Override
    public void subtaskUpdate(Subtask subtask, int epicNumber, int subtaskNumber) {
        if (checkEpicsHashMap(epicNumber)) {
            System.out.println("Такого эпика не существует");
        } else {
            Epic epic = epics.get(epicNumber);
            if (epic.subtasks.isEmpty() || !(epic.subtasks.containsKey(subtaskNumber))) {
                System.out.println("Такой подзадачи не существует");
            } else {
                allTasks.remove(epic.subtasks.get(subtaskNumber));
                epic.subtasks.remove(subtaskNumber);
                epic.subtasks.put(subtaskNumber, subtask);
                allTasks.add(subtask);
            }
        }
    }

    @Override
    public void epicRemove(int number) {
        if (epics.containsKey(number)) {
            Epic epic = epics.get(number);
            for (Integer subtaskNumber : epic.subtasks.keySet()) {
                historyManager.remove(subtaskNumber);
            }
            allTasks.remove(epics.get(number));
            historyManager.remove(number);
            epics.remove(number);
        } else {
            System.out.println("Такого эпика не существует");
        }
    }

    @Override
    public void subtaskRemove(int epicNumber, int subtaskNumber) {
        if (epics.containsKey(epicNumber)) {
            Epic epic = epics.get(epicNumber);
            if (epic.subtasks.containsKey(subtaskNumber)) {
                allTasks.remove(epic.subtasks.get(subtaskNumber));
                epic.subtasks.remove(subtaskNumber);
                historyManager.remove(subtaskNumber);
            } else {
                System.out.println("Такой подзадачи не существует");
            }
        } else {
            System.out.println("Такого эпика не существует");
        }
    }

    @Override
    public void takeEpicStatus(int number) {
        if (checkEpicsHashMap(number)) {
            System.out.println("Такого эпика не существует");
        } else {
            Epic epic = epics.get(number);
            String statusNew = "";
            String statusDone = "";
            String statusInProgress = "";
            if (epic.subtasks.isEmpty()) {
                statusNew = "NEW";
            }
            for (Subtask subtask : epic.subtasks.values()) {
                if ((subtask.getStatus()).equals(Status.NEW)) {
                    statusNew = "NEW";
                } else if ((subtask.getStatus()).equals(Status.DONE)) {
                    statusDone = "DONE";
                } else {
                    statusInProgress = "IN_PROGRESS";
                }
            }

            if (((statusNew).equals("NEW")) && ((statusDone).equals("")) && ((statusInProgress).equals(""))) {
                epic.setStatus(Status.NEW);
            } else if (((statusNew).equals("")) && ((statusDone).equals("DONE")) && (statusInProgress.equals(""))) {
                epic.setStatus(Status.DONE);
            } else {
                epic.setStatus(Status.IN_PROGRESS);
            }
        }
    }

    @Override
    public boolean checkEpicsHashMap(int number) {
        return epics.isEmpty() || !(epics.containsKey(number));
    }
}


