package Manager;

import Tasks.Status;
import Tasks.Task;
import Tasks.Epic;
import Tasks.Subtask;

import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager{
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();

    private final HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public List<Task> getHistory(){
        return historyManager.getHistory();
    }

    @Override
    public Task taskMaker(String name, String description, Status status) {
        return new Task(name, description, status);
    }

    @Override
    public void taskAdd(int id, Task task) {
        tasks.put(id, task);
    }

    @Override
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

    @Override
    public void taskGetById(int number) {
        if (tasks.containsKey(number)){
        Task task = tasks.get(number);
        historyManager.add(number,task);
            System.out.println(task);

        }else {
            System.out.println("Такой задачи не существует");
        }
    }

    @Override
    public void taskRemove(int number) {
        if (tasks.containsKey(number)) {
            tasks.remove(number);
        }else {
            System.out.println("Такой задачи не существует");
        }
    }

    @Override
    public void taskDeleteAll() {
        tasks.clear();
    }

    @Override
    public void taskUpdate(Task task, int number) {
        tasks.remove(number);
        tasks.put(number, task);
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
        epics.put(id, epic);
    }

    @Override
    public void subtaskAdd(int id, int number, Subtask subtask) {
        if (checkEpicsHashMap(number)) {
            System.out.println("Такого эпика не существует");
        } else {
            epics.get(number).subtasks.put(id, subtask);
        }
    }

    @Override
    public void epicListAllTasks() {
        if ((epics.isEmpty())){
            System.out.println("Эпиков нет");
        }else {
            for (int id : epics.keySet()) {
                Epic epic = epics.get(id);
                System.out.println("Идентификатор " + id);
                System.out.println(epic);
            }
        }
    }

    @Override
    public void subtaskListAllTasks(int number) {
        if (checkEpicsHashMap(number)){
            System.out.println("Такого эпика не существует, подзадач соответственно тоже");
        }else {
            Epic epic = epics.get(number);
            if (!(epic.subtasks.isEmpty())) {
                for (int id : epic.subtasks.keySet()) {
                    Subtask subtask = epic.subtasks.get(id);
                    System.out.println("Идентификатор " + id);
                    System.out.println(subtask);
                }
            }else {
                System.out.println("Такой подзадачи не существует, или эпик не содержит подзадач");
            }
        }
    }

    @Override
    public void epicDeleteAll() {
        epics.clear();
    }

    @Override
    public void subtaskDeleteAll(int number) {
        if (epics.containsKey(number)) {
            Epic epic = epics.get(number);
            epic.subtasks.clear();
        }else {
            System.out.println("Такого эпика не существует");
        }
    }

    @Override
    public void epicGetById(int number) {
        if (epics.containsKey(number)) {
            Epic epic = epics.get(number);
            System.out.println(epic);
            historyManager.add(number,epic);
        }else {
            System.out.println("Такого эпика не существует");
        }

    }

    @Override
    public void subtaskGetById(int number1, int number2) {
        if (epics.containsKey(number1)) {
            Epic epic = epics.get(number1);
            if (epic.subtasks.containsKey(number2)) {
                Subtask subtask = epic.subtasks.get(number2);
                System.out.println(subtask);
                historyManager.add(number2,subtask);
            }else {
                System.out.println("Такой подзадачи не существует");
            }
        }else {
            System.out.println("Такого эпика не существует");
        }
    }

    @Override
    public void epicUpdate(Epic epic, int number) {
        epics.remove(number);
        epics.put(number, epic);
    }

    @Override
    public void subtaskUpdate(Subtask subtask, int epicNumber, int subtaskNumber) {
        if (checkEpicsHashMap(epicNumber)){
            System.out.println("Такого эпика не существует");
        }else {
            Epic epic = epics.get(epicNumber);
            if (epic.subtasks.isEmpty()||!(epic.subtasks.containsKey(subtaskNumber))) {
                System.out.println("Такой подзадачи не существует");
            } else {
                epic.subtasks.remove(subtaskNumber);
                epic.subtasks.put(subtaskNumber, subtask);
            }
        }
    }

    @Override
    public void epicRemove(int number) {
        if (epics.containsKey(number)) {
            epics.remove(number);
        }else {
            System.out.println("Такого эпика не существует");
        }
    }

    @Override
    public void subtaskRemove(int epicNumber, int subtaskNumber) {
        if (epics.containsKey(epicNumber)) {
            Epic epic = epics.get(epicNumber);
            if (epic.subtasks.containsKey(subtaskNumber)){
                epic.subtasks.remove(subtaskNumber);
            }else {
                System.out.println("Такой подзадачи не существует");
            }}else{
            System.out.println("Такого эпика не существует");
        }
    }

    @Override
    public void takeEpicStatus(int number) {
        if(checkEpicsHashMap(number)){
            System.out.println("Такого эпика не существует");
        }else{
            Epic epic = epics.get(number);
            String statusNew = "";
            String statusDone = "";
            String statusInProgress = "";
            if (epic.subtasks.isEmpty()){
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


