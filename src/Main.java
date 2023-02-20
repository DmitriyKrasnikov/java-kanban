import Http.HttpTaskManager;
import Http.KVServer;
import Manager.Managers;
import Tasks.Epic;
import Tasks.Status;
import Tasks.Subtask;
import Tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;

//Спринт с самым непонятным ТЗ. И сам по себе не простой.
class Main{
    public static void main(String[] args) {
        KVServer server;
        try {
            server = new KVServer();
            server.start();
            HttpTaskManager httpTaskManager = Managers.getDefault();

            Task task = httpTaskManager.taskMaker("Задача1", "Описание задачи 1", Status.NEW,
                    Duration.ofMinutes(30),
                    LocalDateTime.of(2023, 1, 9, 12, 30)
            );
            httpTaskManager.taskAdd(task);

            Epic epic = httpTaskManager.epicMaker(2001, "Эпик", "Описание эпика ");
            httpTaskManager.epicAdd(epic);

            Subtask subtask = httpTaskManager.subtaskMaker("Подзадача1", "Описание подзадачи 1", Status.DONE,
                    Duration.ofMinutes(37),
                    LocalDateTime.of(2023, 1, 9, 18, 30)
            );
            httpTaskManager.subtaskAdd(2001,subtask);

            httpTaskManager.taskGetById(task.getId());
            httpTaskManager.epicGetById(epic.getId());
            httpTaskManager.subtaskGetById(subtask.getEpicMasterId(), subtask.getId());

            httpTaskManager.save();

            System.out.println();
            System.out.println("Печать всех задач");
            System.out.println(httpTaskManager.client.load("tasks"));
            System.out.println("Печать всех эпиков");
            System.out.println();
            System.out.println(httpTaskManager.client.load("epics"));
            System.out.println();
            System.out.println("Печать всех подзадач");
            System.out.println(httpTaskManager.client.load("subtasks"));
            System.out.println();
            System.out.println(httpTaskManager.client.load("history"));
            System.out.println("Загруженный менеджер");
            System.out.println(httpTaskManager);

            server.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
