import Http.HttpTaskServer;
import Http.KVServer;
import Manager.Managers;
import Tasks.Epic;
import Tasks.Status;
import Tasks.Subtask;
import Tasks.Task;
import com.google.gson.Gson;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServerTest {
    String path = "http://localhost:" + HttpTaskServer.PORT + "/task";

    int anyTaskId;
    int epicIdForSubtask;
    HttpTaskServer server;
    HttpClient client;
    Gson gson = Managers.getGson();
    KVServer kvServer;

    public String getRequestParameter(int anyTaskId, int epicIdForSubtask){
        if (epicIdForSubtask == 0){
            return "?" + anyTaskId;
        }else {
            return "?" + anyTaskId+ "," + epicIdForSubtask;
        }
    }

    @BeforeEach
    public void start() {
        try {
            kvServer = new KVServer();
            kvServer.start();
            server = new HttpTaskServer();
            server.start();
            client = HttpClient.newHttpClient();
        } catch (IOException e) {
            System.out.println("Возникла ошибка при запуске сервера");
        }
    }

    @AfterEach
    public void stop() {
        server.stop();
        kvServer.stop();
    }

    public void fillTaskManager() {
        Task task = server.taskManager.taskMaker("Задача1", "Описание задачи 1", Status.NEW,
                Duration.ofMinutes(30),
                LocalDateTime.of(2023, 1, 9, 12, 30)
        );
        server.taskManager.taskAdd(task);

        Epic epic = server.taskManager.epicMaker(2001, "Эпик", "Описание эпика ");
        server.taskManager.epicAdd(epic);

        Subtask subtask = server.taskManager.subtaskMaker("Подзадача1", "Описание подзадачи 1", Status.DONE,
                Duration.ofMinutes(37),
                LocalDateTime.of(2023, 1, 9, 18, 30)
        );
        server.taskManager.subtaskAdd(2001, subtask);
    }

    public String getAllTaskOfTaskType(String type, String parameter) {
        URI url = URI.create(path + type + parameter);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            return response.body();
        } catch (IOException | InterruptedException e) {
            System.out.println("Произошла ошибка при получении ответа от сервера в методе getAllTaskOfTaskType");
            return null;
        }
    }

    public void deleteAllTaskOfTaskType(String type, String parameter) {
        URI url = URI.create(path + type + parameter);
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch (IOException | InterruptedException e) {
            System.out.println("Произошла ошибка при получении ответа от сервера в методе deleteAllTaskOfTaskType");
        }
    }

    public void putAllTaskOfTaskType(String type, String parameter, String json) {
        URI url = URI.create(path + type + parameter);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            System.out.println("Произошла ошибка при получении ответа от сервера в методе putAllTaskOfTaskType");
            e.printStackTrace();
        }
    }

    @Test
    public void checkGetMethods() {
        fillTaskManager();
        Assertions.assertEquals(getAllTaskOfTaskType("", ""),
                server.getTaskToString("null", 0, 0));

        Assertions.assertEquals(getAllTaskOfTaskType("/task", ""),
                server.getTaskToString("task", 0, 0));
        Assertions.assertEquals(getAllTaskOfTaskType("/epic", ""),
                server.getTaskToString("epic", 0, 0));
        anyTaskId = 0;
        epicIdForSubtask = 2001;
        Assertions.assertEquals(getAllTaskOfTaskType("/subtask", getRequestParameter(anyTaskId,epicIdForSubtask)),
                server.getTaskToString("subtask", anyTaskId, epicIdForSubtask));

        anyTaskId = 1001;
        Assertions.assertEquals(getAllTaskOfTaskType("/task", getRequestParameter(anyTaskId,epicIdForSubtask)),
                server.getTaskToString("task", anyTaskId, 0));
        anyTaskId = 2001;
        Assertions.assertEquals(getAllTaskOfTaskType("/epic", getRequestParameter(anyTaskId,epicIdForSubtask)),
                server.getTaskToString("epic", anyTaskId, 0));
        anyTaskId = 3001;
        epicIdForSubtask = 2001;
        Assertions.assertEquals(getAllTaskOfTaskType("/subtask", getRequestParameter(anyTaskId,epicIdForSubtask)),
                server.getTaskToString("subtask", anyTaskId, epicIdForSubtask));

    }

    @Test
    public void checkDeleteMethods() {
        Task.setGeneralId(1000);
        Epic.setGeneralId(2000);
        Subtask.setGeneralId(3000);
        fillTaskManager();
        anyTaskId = 3001;
        epicIdForSubtask = 2001;
        deleteAllTaskOfTaskType("/subtask", getRequestParameter(anyTaskId,epicIdForSubtask));
        Assertions.assertFalse(server.taskManager.epicHashMap().get(epicIdForSubtask).subtasks.containsKey(anyTaskId));

        anyTaskId = 2001;
        deleteAllTaskOfTaskType("/epic", getRequestParameter(anyTaskId,epicIdForSubtask));
        Assertions.assertFalse(server.taskManager.epicHashMap().containsKey(anyTaskId));

        anyTaskId = 1001;
        deleteAllTaskOfTaskType("/task", getRequestParameter(anyTaskId,epicIdForSubtask));
        Assertions.assertFalse(server.taskManager.taskHashMap().containsKey(anyTaskId));

        fillTaskManager();
        anyTaskId = 0;
        deleteAllTaskOfTaskType("/subtask", getRequestParameter(anyTaskId,epicIdForSubtask));
        Assertions.assertTrue(server.taskManager.epicHashMap().get(epicIdForSubtask+1).subtasks.isEmpty());
        deleteAllTaskOfTaskType("/epic", "");
        Assertions.assertTrue(server.taskManager.epicHashMap().isEmpty());
        deleteAllTaskOfTaskType("/task", "");
        Assertions.assertTrue(server.taskManager.taskHashMap().isEmpty());

        fillTaskManager();
        deleteAllTaskOfTaskType("", "");
        Assertions.assertTrue(server.taskManager.taskHashMap().isEmpty() &&
                server.taskManager.epicHashMap().isEmpty());
    }

    @Test
    public void checkPostMethod() {
        Task task = server.taskManager.taskMaker("Задача1", "Описание задачи 1", Status.NEW,
                Duration.ofMinutes(30),
                LocalDateTime.of(2023, 1, 9, 12, 30)
        );

        Epic epic = server.taskManager.epicMaker(2001, "Эпик", "Описание эпика ");

        Subtask subtask = server.taskManager.subtaskMaker("Подзадача1", "Описание подзадачи 1", Status.DONE,
                Duration.ofMinutes(37),
                LocalDateTime.of(2023, 1, 9, 18, 30)
        );

        String jsonTask = gson.toJson(task);
        putAllTaskOfTaskType("/task","",jsonTask);
        Assertions.assertEquals(server.taskManager.taskHashMap().get(task.getId()),task);

        String jsonEpic = gson.toJson(epic);
        putAllTaskOfTaskType("/epic","",jsonEpic);
        Assertions.assertEquals(server.taskManager.epicHashMap().get(epic.getId()),epic);

        anyTaskId = 0;
        epicIdForSubtask = 2001;
        String jsonSubtask = gson.toJson(subtask);
        putAllTaskOfTaskType("/subtask",getRequestParameter(anyTaskId,epicIdForSubtask),jsonSubtask);
        Assertions.assertEquals(server.taskManager.epicHashMap().get(epicIdForSubtask).subtasks.get(subtask.getId()),
                subtask);

        Task taskUp = server.taskManager.taskMaker("Задача обновление", "Описание задачи 1", Status.NEW,
                Duration.ofMinutes(30),
                LocalDateTime.of(2023, 1, 9, 12, 30)
        );

        Epic epicUp = server.taskManager.epicMaker(2001, "Эпик обновление", "Описание эпика ");

        Subtask subtaskUp = server.taskManager.subtaskMaker("Подзадача1 обновление", "Описание подзадачи 1",
                Status.DONE,
                Duration.ofMinutes(37),
                LocalDateTime.of(2023, 1, 9, 18, 30)
        );

        epicUp.setStatus(Status.DONE);
        anyTaskId = task.getId();
        String jsonTaskUp = gson.toJson(taskUp);
        putAllTaskOfTaskType("/task",getRequestParameter(anyTaskId,epicIdForSubtask),jsonTaskUp);
        Assertions.assertEquals(server.taskManager.taskHashMap().get(task.getId()),taskUp);

        anyTaskId = epic.getId();
        String jsonEpicUp = gson.toJson(epicUp);
        putAllTaskOfTaskType("/epic",getRequestParameter(anyTaskId,epicIdForSubtask),jsonEpicUp);
        Assertions.assertEquals(server.taskManager.epicHashMap().get(epic.getId()),epicUp);

        anyTaskId = subtask.getId();
        epicIdForSubtask = 2001;
        String jsonSubtaskUp = gson.toJson(subtaskUp);
        putAllTaskOfTaskType("/subtask",getRequestParameter(anyTaskId,epicIdForSubtask),jsonSubtaskUp);
        Assertions.assertEquals(server.taskManager.epicHashMap().get(epicIdForSubtask).subtasks.get(subtask.getId()),
                subtaskUp);
    }
}
