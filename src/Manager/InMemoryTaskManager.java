package Manager;

import Tasks.Status;
import Tasks.Task;
import Tasks.Epic;
import Tasks.Subtask;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    //1.Мапа с подзадачами лежит у меня в эпике, для каждого своя
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected TreeSet<Task> priority;
    //1. Вместо TreeMap я решил воспользоваться HashMap, потому что в TreeMap скорость доступа к элементам O(log(n)),
    // а в HashMap О(1). Я разобрался в подсказке в тз. Ниже опишу как.
    private final HashMap<LocalDateTime, Boolean> busyTime = new HashMap<>();
    protected final ArrayList<Task> allTasks = new ArrayList<>();

    //Интервал задачи разбивается на подынтервалы по 15 минут и начало старта каждого подынтервала записывается как ключ
    // в HashMap busyTime, со значением false. При добавлении новой задачи, сначала проверяется лежит ли под таким ключом
    // значение в методе isTimeFree, затем, если время свободно, то оно резервируется в методе reserveTime. В итоге
    // вместо прежнего перебора всего списка за О(n), мы получаем несколько значений за 0(1).
    public boolean isTimeFree(Task task){
        boolean isTimeFree = true;
        int period = (int)Math.ceil(task.getDuration().toMinutes()/15);
         for (int i = 0; i <= period; i++){
            if(busyTime.get(task.getStartTime().plusMinutes(i * 15L)) != null){
                isTimeFree = false;
            }
        }
        return isTimeFree;
    }

    public void reserveTime(Task task){
        int period = (int)Math.ceil(task.getDuration().toMinutes()/15);
        for (int i = 0; i <= period; i++){
            busyTime.put(task.getStartTime().plusMinutes(i * 15L),false);
        }
    }

    public void freeUpTime(Task task){
        int period = (int)Math.ceil(task.getDuration().toMinutes()/15);
        for (int i = 0; i <= period; i++){
            busyTime.remove(task.getStartTime().plusMinutes(i * 15L));
        }
    }

    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        return priority = new TreeSet<>(allTasks);
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
    public Task taskMaker(String name, String description, Status status, Duration duration, LocalDateTime startTime) {
        return new Task(name, description, status, duration, startTime);
    }

    @Override
    public void taskAdd(int id, Task task) {
        if(task.getStartTime().equals(LocalDateTime.of(10000, 1, 1, 1, 1))){
            tasks.put(id, task);
            allTasks.add(task);
        }else {
            if (isTimeFree(task)) {
                reserveTime(task);
                tasks.put(id, task);
                allTasks.add(task);
            } else {
                System.out.println("Задача " + task.getName() + " не может выполняться одновременно с другой.");
            }
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
            freeUpTime(tasks.get(number));
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
            freeUpTime(tasks.get(id));
            allTasks.remove(tasks.get(id));
            historyManager.remove(id);
        }
        tasks.clear();
    }

    @Override
    public void taskUpdate(Task task, int number) {
        if (tasks.containsKey(number)) {
            freeUpTime(task);
            if (task.getStartTime().equals(LocalDateTime.of(10000, 1, 1, 1, 1))){
                allTasks.remove(tasks.get(number));
                tasks.remove(number);
                tasks.put(number, task);
                allTasks.add(task);
            }else {
                if(isTimeFree(task)){
                    freeUpTime(tasks.get(number));
                    allTasks.remove(tasks.get(number));
                    tasks.remove(number);
                    tasks.put(number, task);
                    allTasks.add(task);
                    reserveTime(task);
            }else {
                    System.out.println("Задача " + task.getName() + " не может выполняться одновременно с другой.");
                    reserveTime(task);
                }
            }
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
    public Subtask subtaskMaker(String name, String description, Status status, Duration duration, LocalDateTime startTime) {
        return new Subtask(name, description, status, duration, startTime);
    }

    @Override
    public void epicAdd(int id, Epic epic) {
            epics.put(id, epic);
            allTasks.add(epic);
    }

    @Override
    public void subtaskAdd(int id, int number, Subtask subtask) {
        if (checkEpicsHashMap(number)) {
            System.out.println("Такого эпика не существует");
        } else {
            if(subtask.getStartTime().equals(LocalDateTime.of(10000, 1, 1, 1, 1))){
                epics.get(number).subtasks.put(id, subtask);
                allTasks.add(subtask);
            }else {
                if (isTimeFree(subtask)) {
                    reserveTime(subtask);
                    epics.get(number).subtasks.put(id, subtask);
                    allTasks.add(subtask);
                } else {
                    System.out.println("Задача " + subtask.getName() + " не может выполняться одновременно с другой.");
                }
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
            }
        }
    }

    @Override
    public void epicDeleteAll() {
        for (Integer id : epics.keySet()) {
            for (Integer subId : epics.get(id).subtasks.keySet()){
                freeUpTime(epics.get(id).subtasks.get(subId));
                allTasks.remove(epics.get(id).subtasks.get(subId));
                historyManager.remove(subId);
            }
            freeUpTime(epics.get(id));
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
                freeUpTime(epic.subtasks.get(id));
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
                freeUpTime(epic.subtasks.get(subtaskNumber));
                if (subtask.getStartTime().equals(LocalDateTime.of(10000, 1, 1, 1, 1))){
                    allTasks.remove(epic.subtasks.get(subtaskNumber));
                    epic.subtasks.remove(subtaskNumber);
                    epic.subtasks.put(subtaskNumber, subtask);
                    allTasks.add(subtask);
                }else {
                    if (isTimeFree(subtask)) {
                        reserveTime(subtask);
                        allTasks.remove(epic.subtasks.get(subtaskNumber));
                        epic.subtasks.remove(subtaskNumber);
                        epic.subtasks.put(subtaskNumber, subtask);
                        allTasks.add(subtask);
                    } else {
                        System.out.println("Задача " + subtask.getName() + " не может выполняться одновременно с другой.");
                        reserveTime(epic.subtasks.get(subtaskNumber));
                    }
                }
            }
        }
    }

    @Override
    public void epicRemove(int number) {
        if (epics.containsKey(number)) {
            Epic epic = epics.get(number);
            for (Integer subtaskNumber : epic.subtasks.keySet()) {
                historyManager.remove(subtaskNumber);
                allTasks.remove(epic.subtasks.get(subtaskNumber));
                freeUpTime(epic.subtasks.get(subtaskNumber));
            }
            freeUpTime(epics.get(number));
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
                freeUpTime(epic.subtasks.get(subtaskNumber));
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


