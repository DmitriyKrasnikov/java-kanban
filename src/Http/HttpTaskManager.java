package Http;

import Manager.FileBackedTasksManager;
import Manager.Managers;
import Tasks.Epic;
import Tasks.Subtask;
import Tasks.Task;
import com.google.gson.*;

import java.util.TreeSet;

public class HttpTaskManager extends FileBackedTasksManager {
    private final static String[] keys = {"tasks","epics","subtasks","history"};
    public final KVTaskClient client;

    public final Gson gson;

    public HttpTaskManager(String path) {
        super();
        client = new KVTaskClient(path);
        gson = Managers.getGson();
        recovery();
    }

    public void recovery(){
        for (String key : keys) {
            if (key.equals("history")) {
                super.recoveryHistoryList(historyFromString(client.load(key)));
            } else {
                JsonElement jsonElement = JsonParser.parseString(client.load(key));
                if (!jsonElement.isJsonNull()) {
                    JsonArray jsonArray = jsonElement.getAsJsonArray();
                    switch (key) {
                        case "tasks":
                            for (JsonElement jsonE : jsonArray) {
                                Task task = gson.fromJson(jsonE, Task.class);
                                super.taskAdd(task);
                            }
                            break;
                        case "epics":
                            for (JsonElement jsonE : jsonArray) {
                                Epic epic = gson.fromJson(jsonE, Epic.class);
                                super.epicAdd(epic);
                            }
                            break;
                        case "subtasks":
                            for (JsonElement jsonE : jsonArray) {
                                Subtask subtask = gson.fromJson(jsonE, Subtask.class);
                                super.subtaskAdd(subtask.getEpicMasterId(), subtask);
                            }
                            break;
                    }
                }
            }
        }
    }
    @Override
    public void save() {
        client.put(keys[0], gson.toJson(tasks.values()));
        client.put(keys[1], gson.toJson(epics.values()));
        client.put(keys[2], gson.toJson(allSubtask.values()));
        client.put(keys[3], FileBackedTasksManager.historyToString(super.historyManager, allTaskForHistoryList()));
    }

    @Override
    public void taskAdd(Task task) {
        super.taskAdd(task);
        save();
    }

    @Override
    public void taskRemove(int number) {
        super.taskRemove(number);
        save();
    }

    @Override
    public void taskDeleteAll() {
        super.taskDeleteAll();
        save();
    }

    @Override
    public void taskUpdate(Task task, int number) {
        super.taskUpdate(task, number);
        save();
    }

    @Override
    public void epicAdd(Epic epic) {
        super.epicAdd(epic);
        save();
    }

    @Override
    public void subtaskAdd(int number, Subtask subtask) {
        super.subtaskAdd(number, subtask);
        super.takeEpicStatus(number);
        save();
    }

    @Override
    public void epicDeleteAll() {
        super.epicDeleteAll();
        save();
    }

    @Override
    public void subtaskDeleteAll(int number) {
        super.subtaskDeleteAll(number);
        save();
    }

    @Override
    public void epicUpdate(Epic epic, int number) {
        super.epicUpdate(epic, number);
        save();
    }

    @Override
    public void subtaskUpdate(Subtask subtask, int epicNumber, int subtaskNumber) {
        super.subtaskUpdate(subtask, epicNumber, subtaskNumber);
        super.takeEpicStatus(epicNumber);
        save();
    }

    @Override
    public void epicRemove(int number) {
        super.epicRemove(number);
        save();
    }

    @Override
    public void subtaskRemove(int epicNumber, int subtaskNumber) {
        super.subtaskRemove(epicNumber, subtaskNumber);
        save();
    }

    @Override
    public void taskGetById(int number) {
        super.taskGetById(number);
        save();
    }

    @Override
    public void epicGetById(int number) {
        super.epicGetById(number);
        save();
    }

    @Override
    public void subtaskGetById(int number1, int number2) {
        super.subtaskGetById(number1, number2);
        save();
    }

    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        return super.getPrioritizedTasks();
    }

}
