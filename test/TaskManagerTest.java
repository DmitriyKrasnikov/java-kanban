import Manager.TaskManager;
import Tasks.Epic;
import Tasks.Status;
import Tasks.Subtask;
import Tasks.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

abstract class TaskManagerTest<T extends TaskManager> {

    T manager;

    @Test
    public void checkPrioritySet (){
        Task task = manager.taskMaker("Задача1", "Описание задачи 1", Status.NEW,
                Duration.ofMinutes(66), LocalDateTime.of(2023, 1, 14, 11, 1));
        manager.taskAddWithId(1001,task);
        Epic epic = manager.epicMaker(2001, "Эпик", "Описание эпика ");
        manager.epicAddWhithId(2001,epic);
        Subtask subtask = manager.subtaskMaker("Подзадача1", "Описание подзадачи 1", Status.DONE,
                Duration.ofMinutes(17), LocalDateTime.of(2023, 1, 15, 11, 1));
        manager.subtaskAddWithId(3001,2001, subtask);
        ArrayList<Task> taskArrayList = new ArrayList<>();
        taskArrayList.add(task);
        taskArrayList.add(subtask);
        taskArrayList.add(epic);

        Assertions.assertEquals(manager.getPrioritizedTasks().toString(),taskArrayList.toString());
    }

    @Test
    public void addNewTask() {
        //Проверка создания и добавления задачи. Методы taskMaker и taskAdd
        Task task = manager.taskMaker("Test addNewTask", "Test addNewTask description", Status.NEW,
                Duration.ofMinutes(17), LocalDateTime.of(2023, 1, 15, 11, 1));
        Assertions.assertNotNull(task, "Задача не создана");

        manager.taskAddWithId(1, task);
        HashMap<Integer, Task> tasks = manager.taskHashMap();
        Assertions.assertNotNull(tasks.get(1), "Задача не найдена");
        Assertions.assertEquals(tasks.get(1), task, "Задачи не совпадают");

        //Проверка получения задачи по Id. Так как метод taskGetById void, то я решил проверить,
        // что он выводит просмотром записи в истории просмотров.
        manager.taskGetById(1);
        //Получение задачи по несуществующему id
        manager.taskGetById(5);
        final List<Task> history = manager.getHistory();
        Assertions.assertEquals(tasks.get(1), history.get(0), "Задачи не совпадают");
        Assertions.assertFalse(history.contains(tasks.get(5)), "Получена несуществующая задача");

        //Проверка обновления задачи метод taskUpdate
        Task taskUpdate = manager.taskMaker("task update", "Test addNewTask description", Status.NEW,
                Duration.ofMinutes(36), LocalDateTime.of(2023, 1, 17, 10, 1));
        manager.taskUpdate(taskUpdate, 1);
        //Обновление с несуществующим id
        manager.taskUpdate(taskUpdate, 5);
        tasks = manager.taskHashMap();
        Assertions.assertEquals(tasks.get(1), taskUpdate, "Задача не обновилась");
        Assertions.assertEquals(tasks.size(), 1, "Обновление прошло не правильно");
        Assertions.assertNull(tasks.get(5), "Задача добавлена, но не обновлена");

        //Удаление одной задачи
        manager.taskAddWithId(2, task);
        manager.taskRemove(1);
        tasks = manager.taskHashMap();
        Assertions.assertNull(tasks.get(1), "Задача не удалилась");
        Assertions.assertEquals(tasks.size(), 1, "Задача не удалилась");

        //Удаление всех задач
        manager.taskDeleteAll();
        tasks = manager.taskHashMap();
        Assertions.assertEquals(tasks.size(), 0, "Остались неудаленные задачи");
    }

    //Решение нашёл в интернете. Как работает примерно понимаю.
    @Test
    public void taskListAllTasks() {
        Task task1 = manager.taskMaker("Test1", "Test1", Status.NEW,
                Duration.ofMinutes(28), LocalDateTime.of(2023, 1, 4, 14, 1));
        manager.taskAddWithId(1, task1);
        Task task2 = manager.taskMaker("Test2", "Test2", Status.NEW,
                Duration.ofMinutes(13), LocalDateTime.of(2023, 1, 10, 11, 1));
        manager.taskAddWithId(2, task2);

        String consoleOutput = null;
        PrintStream originalOut = System.out;
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(500);
            PrintStream capture = new PrintStream(outputStream);
            System.setOut(capture);
            manager.taskListAllTasks();
            capture.flush();
            consoleOutput = outputStream.toString();
            System.setOut(originalOut);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Assertions.assertEquals(consoleOutput, "Идентификатор " + 1 + "\r\n" + task1.toString() + "\r\n" +
                "Идентификатор " + 2 + "\r\n" + task2.toString() + "\r\n");
    }

    @Test
    void addNewEpic() {
        //Проверка создания и добавления задачи. Методы epicMaker и epicAdd
        Epic epic = manager.epicMaker(1, "Test addNewEpic", "Test addNewEpic description");
        Assertions.assertNotNull(epic, "Эпик не создан");

        manager.epicAddWhithId(1, epic);
        HashMap<Integer, Epic> epics = manager.epicHashMap();
        Assertions.assertNotNull(epics.get(1), "Эпик не найден");
        Assertions.assertEquals(epics.get(1), epic, "Эпики не совпадают");

        //Проверка получения задачи по Id.
        manager.epicGetById(1);
        //Получение задачи по несуществующему id
        manager.epicGetById(5);
        final List<Task> history = manager.getHistory();
        Assertions.assertEquals(epics.get(1), history.get(0), "Эпики не совпадают");
        Assertions.assertFalse(history.contains(epics.get(5)), "Получен несуществующий Эпик");

        //Проверка обновления задачи метод EpicUpdate
        Epic epicUpdate = manager.epicMaker(1, "epic update", "Test addNewEpic description");
        manager.epicUpdate(epicUpdate, 1);
        //Обновление с несуществующим id
        manager.epicUpdate(epicUpdate, 5);
        epics = manager.epicHashMap();
        Assertions.assertEquals(epics.get(1), epicUpdate, "Эпик не обновился");
        Assertions.assertEquals(epics.size(), 1, "Обновление прошло не правильно");
        Assertions.assertNull(epics.get(5), "Эпик добавлен, но не обновлен");

        //Удаление одной задачи
        manager.epicAddWhithId(2, epic);
        manager.epicRemove(1);
        epics = manager.epicHashMap();
        Assertions.assertNull(epics.get(1), "Эпик не удалился");
        Assertions.assertEquals(epics.size(), 1, "Эпик не удалился");

        //Удаление всех задач
        manager.epicDeleteAll();
        epics = manager.epicHashMap();
        Assertions.assertEquals(epics.size(), 0, "Остались неудаленные эпики");
    }

    @Test
    public void EpicListAllTasks() {
        Epic epic1 = manager.epicMaker(1, "Test1", "Test1");
        manager.epicAddWhithId(1, epic1);
        Epic epic2 = manager.epicMaker(2, "Test2", "Test2");
        manager.epicAddWhithId(2, epic2);

        String consoleOutput = null;
        PrintStream originalOut = System.out;
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(500);
            PrintStream capture = new PrintStream(outputStream);
            System.setOut(capture);
            manager.epicListAllTasks();
            capture.flush();
            consoleOutput = outputStream.toString();
            System.setOut(originalOut);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Assertions.assertEquals(consoleOutput, "Идентификатор " + 1 + "\r\n" + epic1.toString() + "\r\n" +
                "Идентификатор " + 2 + "\r\n" + epic2.toString() + "\r\n");
    }

    @Test
    void addNewSubtask() {
        //Проверка создания и добавления задачи. Методы subtaskMaker и subtaskAdd.
        //Создается эпик, в котором будут лежать сабтаски
        Epic epic1 = manager.epicMaker(10, "Test1", "Test1");
        manager.epicAddWhithId(10, epic1);
        Subtask subtask = manager.subtaskMaker("Test addNewSubtask", "Test addNewSubtask description", Status.NEW,
                Duration.ofMinutes(93), LocalDateTime.of(2023, 1, 10, 11, 1));
        Assertions.assertNotNull(subtask, "Задача не создана");

        manager.subtaskAddWithId(1, 10, subtask);
        Assertions.assertNotNull(epic1.subtasks.get(1), "Задача не найдена");
        Assertions.assertEquals(epic1.subtasks.get(1), subtask, "Задачи не совпадают");

        //Проверка получения задачи по Id.
        manager.subtaskGetById(10, 1);
        //Получение задачи по несуществующему id
        manager.subtaskGetById(10, 5);
        List<Task> history = manager.getHistory();
        Assertions.assertEquals(epic1.subtasks.get(1), history.get(0), "Задачи не совпадают");
        Assertions.assertFalse(history.contains(epic1.subtasks.get(5)), "Получена несуществующая задача");

        //Проверка обновления задачи метод subtaskUpdate
        Subtask subtaskUpdate = manager.subtaskMaker("Subtask update", "Test addNewSubtask description", Status.NEW,
                Duration.ofMinutes(333), LocalDateTime.of(2023, 1, 19, 1, 1));
        manager.subtaskUpdate(subtaskUpdate, 10, 1);
        //Обновление с несуществующим id
        manager.subtaskUpdate(subtaskUpdate, 10, 5);
        Assertions.assertEquals(epic1.subtasks.get(1), subtaskUpdate, "Задача не обновилась");
        Assertions.assertEquals(epic1.subtasks.size(), 1, "Обновление прошло не правильно");
        Assertions.assertNull(epic1.subtasks.get(5), "Задача добавлена, но не обновлена");

        //Удаление одной задачи
        manager.subtaskAddWithId(2, 10, subtask);
        manager.subtaskRemove(10, 1);
        Assertions.assertNull(epic1.subtasks.get(1), "Задача не удалилась");
        Assertions.assertEquals(epic1.subtasks.size(), 1, "Задача не удалилась");

        //Удаление всех задач
        manager.subtaskDeleteAll(10);
        Assertions.assertEquals(epic1.subtasks.size(), 0, "Остались неудаленные задачи");
    }

    @Test
    public void SubtaskListAllTasks() {
        Epic epic1 = manager.epicMaker(10, "Epic", "Epic");
        manager.epicAddWhithId(10, epic1);
        Subtask subtask1 = manager.subtaskMaker("Test1", "Test1", Status.NEW,
                Duration.ofMinutes(45), LocalDateTime.of(2023, 1, 11, 1, 1));
        manager.subtaskAddWithId(1, 10, subtask1);
        Subtask subtask2 = manager.subtaskMaker("Test2", "Test2", Status.NEW,
                Duration.ofMinutes(27), LocalDateTime.of(2023, 1, 1, 1, 1));
        manager.subtaskAddWithId(2, 10, subtask2);

        String consoleOutput = null;
        PrintStream originalOut = System.out;
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(500);
            PrintStream capture = new PrintStream(outputStream);
            System.setOut(capture);
            manager.subtaskListAllTasks(10);
            capture.flush();
            consoleOutput = outputStream.toString();
            System.setOut(originalOut);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Assertions.assertEquals(consoleOutput, "Идентификатор " + 1 + "\r\n" + subtask1.toString() + "\r\n" +
                "Идентификатор " + 2 + "\r\n" + subtask2.toString() + "\r\n");
    }

    @Test
    public void checkingForChangesEpicStatus() {
        Epic epic = manager.epicMaker(10, "Epic", "Epic");
        manager.epicAddWhithId(10, epic);
        Subtask subtask1 = manager.subtaskMaker("Test1", "Test1", Status.NEW,
                Duration.ofMinutes(34), LocalDateTime.of(2023, 1, 11, 11, 1));
        Subtask subtask2 = manager.subtaskMaker("Test2", "Test2", Status.NEW,
                Duration.ofMinutes(25), LocalDateTime.of(2023, 1, 12, 14, 1));
        Subtask subtask3 = manager.subtaskMaker("Test3", "Test3", Status.NEW,
                Duration.ofMinutes(45), LocalDateTime.of(2023, 1, 17, 13, 1));

        //Пустой список подзадач.
        Assertions.assertEquals(epic.getStatus(), Status.NEW, "Статус у эпика без подзадач рассчитывается неверно");

        //Все подзадачи со статусом NEW
        manager.subtaskAddWithId(1, 10, subtask1);
        manager.subtaskAddWithId(2, 10, subtask2);
        manager.subtaskAddWithId(3, 10, subtask3);
        manager.takeEpicStatus(10);
        Assertions.assertEquals(epic.getStatus(), Status.NEW, "Статус у эпика с подзадачами со статусом NEW" +
                " рассчитывается неверно");

        //Все подзадачи со статусом DONE
        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.DONE);
        subtask3.setStatus(Status.DONE);
        manager.takeEpicStatus(10);
        Assertions.assertEquals(epic.getStatus(), Status.DONE, "Статус у эпика с подзадачами со статусом DONE" +
                " рассчитывается неверно");

        //Подзадачи со статусами NEW и DONE
        subtask1.setStatus(Status.NEW);
        manager.takeEpicStatus(10);
        Assertions.assertEquals(epic.getStatus(), Status.IN_PROGRESS, "Статус у эпика с подзадачами со статусом " +
                "DONE and NEW рассчитывается неверно");

        //Подзадачи со статусом IN_PROGRESS
        subtask2.setStatus(Status.IN_PROGRESS);
        manager.takeEpicStatus(10);
        Assertions.assertEquals(epic.getStatus(), Status.IN_PROGRESS, "Статус у эпика с подзадачами со статусом " +
                "NEW,DONE and IN_PROGRESS рассчитывается неверно");
    }

    @Test
    public void checkingHistoryList() {
        Epic epic = manager.epicMaker(2001, "Epic", "Epic");
        manager.epicAddWhithId(2001, epic);
        Subtask subtask1 = manager.subtaskMaker("Subtask1", "Subtask1", Status.NEW,
                Duration.ofMinutes(35), LocalDateTime.of(2023, 1, 7, 1, 1));
        Subtask subtask2 = manager.subtaskMaker("Subtask2", "Subtask2", Status.NEW,
                Duration.ofMinutes(25), LocalDateTime.of(2023, 1, 17, 11, 1));
        manager.subtaskAddWithId(3001, 2001, subtask1);
        manager.subtaskAddWithId(3002, 2001, subtask2);
        Task task = manager.taskMaker("Task", "Task", Status.NEW,
                Duration.ofMinutes(0), LocalDateTime.of(2023, 11, 1, 10, 1));
        manager.taskAddWithId(1001, task);

        manager.subtaskGetById(2001, 3001);
        manager.taskGetById(1001);
        manager.subtaskGetById(2001, 3002);
        manager.epicGetById(2001);

        List<Task> checkList = List.of(subtask1, task, subtask2, epic);
        List<Task> historyList = manager.getHistory();

        Assertions.assertEquals(checkList.toString(), historyList.toString());
    }
}
