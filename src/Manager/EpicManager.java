package Manager;
import Tasks.Epic;
import Tasks.Status;
import Tasks.Subtask;

import java.util.HashMap;
/*Я не стал создавать абстрактный класс, для того, чтобы наследовать от него TaskManager и EpicManager. Потому что в
классе EpicManager определены практически одинаковые методы для объектов Epic и SubTask. Поэтому пришлось бы
переопределять один метод два раза, и тогда непонятно на какой метод ссылаться. Я решил попробовать сделать через
вложенный класс, но тогда нет доступа к нестатичной таблице epics. Поэтому оставил как есть. Думаю, конкретно в этом
случае это роли не играет.*/
public class EpicManager {
    private HashMap<Integer, Epic> epics = new HashMap<>();
    //Для начала поставил public, чтобы не путаться. Потом просто забыл поменять. Здесь подойдет модификатор private.
    // Так как, работа с таблицей ведется только с помощью методов класса EpicManager.

    public Epic epicMaker(int id, String name, String description) {
        HashMap<Integer, Subtask> subtasks = new HashMap<>();
        if (epics.containsKey(id)) {
            subtasks = (epics.get(id)).subtasks;
        }
        return new Epic(name, description, Status.NEW, subtasks);
    }

    public Subtask subtaskMaker(String name, String description, Status status) {
        return new Subtask(name, description, status);
    }

    public void epicAdd(int id, Epic epic) {
        epics.put(id, epic);
    }

    public void subtaskAdd(int id, int number, Subtask subtask) {
        if (checkEpicsHashMap(number)) {
            System.out.println("Такого эпика не существует");
        } else {
            epics.get(number).subtasks.put(id, subtask);
        }
    }

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

    public void epicDeleteAll() {
        epics.clear();
    }

    public void subtaskDeleteAll(int number) {
        if (epics.containsKey(number)) {
            Epic epic = epics.get(number);
            epic.subtasks.clear();
        }else {
            System.out.println("Такого эпика не существует");
        }
    }

    public void epicGetById(int number) {
        if (epics.containsKey(number)) {
            Epic epic = epics.get(number);
            System.out.println(epic);
        }else {
            System.out.println("Такого эпика не существует");
        }

    }

    public void subtaskGetById(int number1, int number2) {
        if (epics.containsKey(number1)) {
            Epic epic = epics.get(number1);
            if (epic.subtasks.containsKey(number2)) {
                Subtask subtask = epic.subtasks.get(number2);
                System.out.println(subtask);
            }else {
                System.out.println("Такой подзадачи не существует");
            }
        }else {
            System.out.println("Такого эпика не существует");
        }
    }

    public void epicUpdate(Epic epic, int number) {
        epics.remove(number);
        epics.put(number, epic);
    }

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

    public void epicRemove(int number) {
        if (epics.containsKey(number)) {
            epics.remove(number);
        }else {
            System.out.println("Такого эпика не существует");
        }
    }

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

    public boolean checkEpicsHashMap(int number) {
        return epics.isEmpty() || !(epics.containsKey(number));
    }
}
