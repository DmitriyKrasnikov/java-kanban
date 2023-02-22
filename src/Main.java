import Http.HttpTaskServer;
import Http.KVServer;
import Tasks.Epic;
import Tasks.Status;
import Tasks.Subtask;
import Tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;

class Main{
    public static void main(String[] args) {
        KVServer server;
        HttpTaskServer server1;
        try {
            server = new KVServer();
            server.start();
            server1 = new HttpTaskServer();
            server1.start();


            Task task = server1.taskManager.taskMaker("Задача1", "Описание задачи 1", Status.DONE,
                    Duration.ofMinutes(27),
                    LocalDateTime.of(2023, 1, 14, 18, 30)
            );
            server1.taskManager.taskAdd(task);


            Epic epic = server1.taskManager.epicMaker(2001, "Эпик", "Описание эпика ");
            server1.taskManager.epicAdd(epic);

            Subtask subtask = server1.taskManager.subtaskMaker("Подзадача1", "Описание подзадачи 1", Status.DONE,
                    Duration.ofMinutes(37),
                    LocalDateTime.of(2023, 1, 9, 18, 30)
            );
            server1.taskManager.subtaskAdd(2001,subtask);


            server1.taskManager.epicGetById(epic.getId());
            server1.taskManager.subtaskGetById(subtask.getEpicMasterId(), subtask.getId());


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
