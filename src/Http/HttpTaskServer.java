package Http;

import Manager.Managers;
import Manager.TaskManager;
import Tasks.Epic;
import Tasks.Status;
import Tasks.Subtask;
import Tasks.Task;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {

    public static final int PORT = 8080;

    private final HttpServer server;
    public final TaskManager taskManager;
    private final Gson gson;

    public HttpTaskServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/task", this::handle);
        taskManager = Managers.getDefault();
        gson = Managers.getGson();
    }

        public void handle(HttpExchange exchange) {
        String[] path = getTaskType(exchange).split(",");
        int id = parsePathId(path[1]);
        int epicIdForSubtask = parsePathId(path[2]);
        try {
            if (id != -1 && epicIdForSubtask != -1) {
                switch (exchange.getRequestMethod()) {
                    case "GET":
                         if (path[0].equals("history")) {
                            sendText(exchange, gson.toJson(taskManager.getHistory()), 200);
                        }else {
                             sendText(exchange, getTaskToString(path[0], id, epicIdForSubtask), 200);
                         }
                         break;
                    case "DELETE":
                        sendText(exchange, choiceDeleteMethod(path[0], id, epicIdForSubtask), 200);
                        break;
                    case "POST":
                        try {
                            sendText(exchange, choicePostMethod(exchange, path[0], id, epicIdForSubtask), 200);
                        } catch (JsonSyntaxException e) {
                            sendText(exchange, "Получен некорректный JSON", 400);
                        }
                }
            } else {
                sendText(exchange, "Введён некорректный Id", 405);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            exchange.close();
        }
    }

    public String getTaskToString(String taskType, int id, int epicIdForSubtask) {
        String consoleOutput;
        PrintStream StartPrintStream = System.out;
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            PrintStream workPrintStream = new PrintStream(byteArrayOutputStream);
            System.setOut(workPrintStream);
            choiceGetMethod(taskType, id, epicIdForSubtask);
            workPrintStream.flush();
            consoleOutput = byteArrayOutputStream.toString();
            System.setOut(StartPrintStream);
            return consoleOutput;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getTaskType(HttpExchange exchange) {
        String parameter = exchange.getRequestURI().getQuery();
        String path = exchange.getRequestURI().getPath().replaceFirst("/task", "")
                .replaceFirst("/", "");
        String taskType = "null";
        String id = "0";
        String epicIdForSubtask = "0";

        if (!path.isBlank()) {
            taskType = path;
        }
        if (parameter != null) {
            String[] ids = parameter.split(",");
            id = ids[0];
            if (ids.length == 2) {
                epicIdForSubtask = ids[1];
            }
        }
        return taskType + "," + id + "," + epicIdForSubtask;
    }

    private int parsePathId(String id) {
        try {
            return Integer.parseInt(id);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void choiceGetMethod(String taskType, int id, int epicIdForSubtask) {
        if (taskType.equals("null")) {
            for (Task task: taskManager.getPrioritizedTasks()) {
                System.out.println(task);
            }
        } else if (id == 0) {
            switch (taskType) {
                case "task":
                    taskManager.taskListAllTasks();
                    break;
                case "epic":
                    taskManager.epicListAllTasks();
                    break;
                case "subtask":
                    taskManager.subtaskListAllTasks(epicIdForSubtask);
                    break;
            }
        } else {
            switch (taskType) {
                case "task":
                    taskManager.taskGetById(id);
                    break;
                case "epic":
                    taskManager.epicGetById(id);
                    break;
                case "subtask":
                    taskManager.subtaskGetById(epicIdForSubtask, id);
                    break;
            }
        }
    }

    private String choiceDeleteMethod(String taskType, int id, int epicIdForSubtask) {
        if (taskType.equals("null")) {
            taskManager.taskDeleteAll();
            for (Integer num : taskManager.epicHashMap().keySet()) {
                taskManager.subtaskDeleteAll(num);
            }
            taskManager.epicDeleteAll();
            return "Все задачи,эпики и подзадачи удалены";
        } else if (id == 0) {
            switch (taskType) {
                case "task":
                    taskManager.taskDeleteAll();
                    return "Все задачи удалены";
                case "epic":
                    taskManager.epicDeleteAll();
                    return "Все эпики удалены";
                case "subtask":
                    taskManager.subtaskDeleteAll(epicIdForSubtask);
                    return "Все подзадачи удалены";
                default:
                    return "0";
            }
        } else {
            switch (taskType) {
                case "task":
                    taskManager.taskRemove(id);
                    return "Задача " + id + " удалена";
                case "epic":
                    taskManager.epicRemove(id);
                    return "Эпик " + id + " удален";
                case "subtask":
                    taskManager.subtaskRemove(epicIdForSubtask, id);
                    return "Подзадача " + id + " удалена";
                default:
                    return "0";
            }
        }
    }

    private String choicePostMethod(HttpExchange exchange, String taskType, int id, int epicIdForSubtask) throws IOException {
        String request = readText(exchange);
        if (id == 0) {
            switch (taskType) {
                case "task":
                    Task task = gson.fromJson(request, Task.class);
                    if (task.getStartTime() == null) {
                        task.setStartTime(LocalDateTime.of(10000, 1, 1, 1, 1));
                    }
                    if (task.getDuration() == null) {
                        task.setDuration(Duration.ofMinutes(0));
                    }
                    if (task.getStatus() == null) {
                        task.setStatus(Status.NEW);
                    }
                    if (task.getId() == 0) {
                        task.setOwnId();
                    }
                    taskManager.taskAdd(task);
                    return "Задача " + task.getName() + " добавлена";
                case "epic":
                    Epic epic = gson.fromJson(request, Epic.class);
                    taskManager.epicAdd(epic);
                    return "Эпик " + epic.getName() + " добавлен";
                case "subtask":
                    Subtask subtask = gson.fromJson(request, Subtask.class);
                    if (subtask.getStartTime() == null) {
                        subtask.setStartTime(LocalDateTime.of(10000, 1, 1, 1, 1));
                    }
                    if (subtask.getDuration() == null) {
                        subtask.setDuration(Duration.ofMinutes(0));
                    }
                    if (subtask.getStatus() == null) {
                        subtask.setStatus(Status.NEW);
                    }
                    if (subtask.getId() == 0) {
                        subtask.setOwnId();
                    }
                    taskManager.subtaskAdd(epicIdForSubtask, subtask);
                    return "Подзадача " + subtask.getName() + " добавлена";
                default:
                    return "Неправильно указан тип задачи";
            }
        } else {
            switch (taskType) {
                case "task":
                    Task task = gson.fromJson(request, Task.class);
                    if (task.getStartTime() == null) {
                        task.setStartTime(LocalDateTime.of(10000, 1, 1, 1, 1));
                    }
                    if (task.getDuration() == null) {
                        task.setDuration(Duration.ofMinutes(0));
                    }
                    if (task.getStatus() == null) {
                        task.setStatus(Status.NEW);
                    }
                    if (task.getId() == 0) {
                        task.setOwnId();
                    }
                    taskManager.taskUpdate(task, id);
                    return "Задача " + task.getName() + " добавлена";
                case "epic":
                    Epic epic = gson.fromJson(request, Epic.class);
                    epic.setEpicId();
                    if (taskManager.epicHashMap().containsKey(id)) {
                        epic.subtasks = (taskManager.epicHashMap().get(id)).subtasks;
                    }
                    epic.setDuration(epic.getDuration());
                    epic.setStartTime(epic.getStartTime());
                    taskManager.epicUpdate(epic, id);
                    return "Эпик " + epic.getName() + " добавлен";
                case "subtask":
                    Subtask subtask = gson.fromJson(request, Subtask.class);
                    if (subtask.getStartTime() == null) {
                        subtask.setStartTime(LocalDateTime.of(10000, 1, 1, 1, 1));
                    }
                    if (subtask.getDuration() == null) {
                        subtask.setDuration(Duration.ofMinutes(0));
                    }
                    if (subtask.getStatus() == null) {
                        subtask.setStatus(Status.NEW);
                    }
                    if (subtask.getId() == 0) {
                        subtask.setOwnId();
                    }
                    taskManager.subtaskUpdate(subtask, epicIdForSubtask, id);
                    return "Подзадача " + subtask.getName() + " добавлена";
                default:
                    return "Неправильно указан тип задачи или id";
            }
        }
    }


    public String readText(HttpExchange httpExchange) throws IOException {
        return new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    public void sendText(HttpExchange httpExchange, String responseString, int responseCode) throws IOException {
        if (responseString.isBlank()) {
            httpExchange.sendResponseHeaders(responseCode, 0);
        } else {
            byte[] resp = responseString.getBytes(StandardCharsets.UTF_8);
            //httpExchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            httpExchange.sendResponseHeaders(responseCode, resp.length);
            httpExchange.getResponseBody().write(resp);
        }
    }

    public void start() {
        System.out.println("Сервер работает на " + PORT + " порту");
        server.start();
    }

    public void stop() {
        server.stop(0);
        System.out.println("Сервер остановили на " + PORT + " порту");
    }
}


