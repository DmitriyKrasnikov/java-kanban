import Http.HttpTaskManager;
import Http.KVServer;
import Manager.FileBackedTasksManager;
import Manager.Managers;
import Tasks.Epic;
import Tasks.Status;
import Tasks.Subtask;
import Tasks.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

public class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {

    KVServer server;
    @BeforeEach
    public void create(){
        try {
            server = new KVServer();
            server.start();
            manager = Managers.getDefault();
        }catch (IOException e){
            System.out.println("Возникла ошибка при запуске сервера");
        }
    }
    @AfterEach
    public void stop(){
    server.stop();
    }
    @Test
    public void shouldSave(){
        Task task = manager.taskMaker("Задача1", "Описание задачи 1", Status.NEW,
                Duration.ofMinutes(30),
                LocalDateTime.of(2023, 1, 9, 12, 30)
        );
        manager.taskAdd(task);

        Epic epic = manager.epicMaker(2001, "Эпик", "Описание эпика ");
        manager.epicAdd(epic);

        Subtask subtask = manager.subtaskMaker("Подзадача1", "Описание подзадачи 1", Status.DONE,
                Duration.ofMinutes(37),
                LocalDateTime.of(2023, 1, 9, 18, 30)
        );
        manager.subtaskAdd(2001,subtask);

        manager.taskGetById(task.getId());
        manager.epicGetById(epic.getId());
        manager.subtaskGetById(subtask.getEpicMasterId(), subtask.getId());

        manager.save();

        Assertions.assertEquals(manager.client.load("tasks"), manager.gson.toJson(manager.taskHashMap().values()),
                "Проблема с сохранением задач");
        Assertions.assertEquals(manager.client.load("epics"), manager.gson.toJson(manager.epicHashMap().values()),
                "Проблема с сохранением эпиков");
        Assertions.assertEquals(manager.client.load("subtasks"), manager.gson.toJson(manager.subtaskHashMap().values()),
                "Проблема с сохранением подзадач");
        Assertions.assertEquals(manager.client.load("history"),
                FileBackedTasksManager.historyToString(manager.historyManager, manager.allTaskForHistoryList()),
                "Проблема с сохранением истории");

        HashMap<Integer,Task> t = new HashMap<>(manager.taskHashMap());
        HashMap<Integer,Task> e = new HashMap<>(manager.epicHashMap());
        HashMap<Integer,Task> s = new HashMap<>(manager.subtaskHashMap());
        List<Task> h = manager.getHistory();

        manager.taskDeleteAll();
        manager.epicDeleteAll();

        manager.recovery();

        Assertions.assertEquals(manager.taskHashMap(),t);
        Assertions.assertEquals(manager.epicHashMap(),e);
        Assertions.assertEquals(manager.subtaskHashMap(),s);
        Assertions.assertEquals(manager.getHistory(),h);
    }
}
