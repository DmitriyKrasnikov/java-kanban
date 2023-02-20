package Http;

import Manager.FileBackedTasksManager;
import Manager.Managers;
import Tasks.Epic;
import Tasks.Subtask;
import Tasks.Task;
import com.google.gson.*;

public class HttpTaskManager extends FileBackedTasksManager {
    private final static String[] keys = {"tasks","epics","subtasks","history"};
    public final KVTaskClient client;

    public final Gson gson;

    public HttpTaskManager(String path) {
        super();
        client = new KVTaskClient(path);
        gson = Managers.getGson();
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

}
