package Manager;
import Tasks.Epic;
import Tasks.Subtask;

import java.util.HashMap;

public class EpicManager {
    public HashMap<Integer, Epic> epics = new HashMap<>();

    public Epic epicMaker(int id, String name, String description) {
        String status = "NEW";
        HashMap<Integer, Subtask> subtasks = new HashMap<>();
        if (epics.containsKey(id)) {
            subtasks = (epics.get(id)).subtasks;
        }
        return new Epic(name, description, status, subtasks);
    }

    public Subtask subtaskMaker(String name, String description, String status) {
        return new Subtask(name, description, status);
    }

    public void epicAdd(int id, Epic epic) {
        epics.put(id, epic);
    }

    public void subtaskAdd(int id, int number, Subtask subtask) {
        if (epics.isEmpty() || !(epics.containsKey(number))) {
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
                System.out.println("Задача: " + epic.getName());
                System.out.println("Описание: " + epic.getDescription());
                System.out.println("Статус: " + epic.getStatus());
            }
        }
    }

    public void subtaskListAllTasks(int number) {
        if ((epics.isEmpty())||!(epics.containsKey(number))){
            System.out.println("Такого эпика не существует, подзадач соответственно тоже");
        }else {
                Epic epic = epics.get(number);
            if (!(epic.subtasks.isEmpty())) {
                for (int id : epic.subtasks.keySet()) {
                    Subtask subtask = epic.subtasks.get(id);
                    System.out.println("Идентификатор " + id);
                    System.out.println("Задача: " + subtask.getName());
                    System.out.println("Описание: " + subtask.getDescription());
                    System.out.println("Статус: " + subtask.getStatus());
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
            System.out.println("Задача: " + epic.getName());
            System.out.println("Описание: " + epic.getDescription());
            System.out.println("Статус: " + epic.getStatus());
        }else {
            System.out.println("Такого эпика не существует");
        }

    }

    public void subtaskGetById(int number1, int number2) {
        if (epics.containsKey(number1)) {
            Epic epic = epics.get(number1);
            if (epic.subtasks.containsKey(number2)) {
                Subtask subtask = epic.subtasks.get(number2);
                System.out.println("Задача: " + subtask.getName());
                System.out.println("Описание: " + subtask.getDescription());
                System.out.println("Статус: " + subtask.getStatus());
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
        if (epics.isEmpty()||!(epics.containsKey(epicNumber))){
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
        if(epics.isEmpty()||!(epics.containsKey(number))){
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
            if ((subtask.getStatus()).equals("NEW")) {
                statusNew = "NEW";
            } else if ((subtask.getStatus()).equals("DONE")) {
                statusDone = "DONE";
            } else {
                statusInProgress = "IN_PROGRESS";
            }
        }

        if (((statusNew).equals("NEW")) && ((statusDone).equals("")) && ((statusInProgress).equals(""))) {
            epic.setStatus("NEW");
        } else if (((statusNew).equals("")) && ((statusDone).equals("DONE")) && (statusInProgress.equals(""))) {
            epic.setStatus("DONE");
        } else {
            epic.setStatus("IN_PROGRESS");
        }
        }
    }
}
