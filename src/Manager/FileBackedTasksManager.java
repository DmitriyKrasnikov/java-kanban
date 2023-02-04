package Manager;

import Tasks.Epic;
import Tasks.Status;
import Tasks.Subtask;
import Tasks.Task;

import java.io.*;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;

public class FileBackedTasksManager extends InMemoryTaskManager {
    File file;

    FileBackedTasksManager(File file) {
        this.file = file;
    }

    static public FileBackedTasksManager loadFromFile(File file) {
        return new FileBackedTasksManager(file);
    }

    //Методы для сохранения в файл

    //save() сохраняет текущее состояние файла. В тз требуется добавить его в каждый модифицирующий метод родителя,
    public void save() {
        //Проходим спискам задач Task и Epic, собираем в одну мапу
        HashMap<Integer, Task> allTasks = new HashMap<>();
        for (Integer key : super.tasks.keySet()) {
            allTasks.put(key, super.tasks.get(key));
        }
        for (Integer key : super.epics.keySet()) {
            allTasks.put(key, super.epics.get(key));
        }

        try (FileWriter fileWriter = new FileWriter(file)) {

            fileWriter.write("id,type,name,status,description,startTime,durationOnMinutes,epic" + System.lineSeparator());

            // Следующие циклы записаны для того, чтобы задачи в файл записывались по порядку. Сначала Task,
            // потом Epic со всеми, включенными в него Subtask.
            for (Integer allTaskKey : allTasks.keySet()) {
                if (allTaskKey < 2000) {

                    fileWriter.write(allTaskKey + "," + TaskEnum.TASK + "," +
                            toString(allTasks.get(allTaskKey)) + System.lineSeparator());
                }
            }
            for (Integer allTaskKey : allTasks.keySet()) {
                if (2000 < allTaskKey) {

                    fileWriter.write(allTaskKey + "," + TaskEnum.EPIC + "," +
                            toString(allTasks.get(allTaskKey)) + System.lineSeparator());

                    Epic epic = (Epic) allTasks.get(allTaskKey);
                    for (Integer subtaskKey : epic.subtasks.keySet()) {

                        fileWriter.write(subtaskKey + "," + TaskEnum.SUBTASK + "," +
                                toString(epic.subtasks.get(subtaskKey)) + "," + allTaskKey + System.lineSeparator());
                    }
                }
            }
            fileWriter.write(System.lineSeparator());
            fileWriter.write(historyToString(super.historyManager, allTasks));
        } catch (IOException e) {
            throw new ManagerSaveException("Как сейчас сделал, больше так не делай", e);
        }

    }

    private String toString(Task task) {
        return task.getName() + "," + task.getStatus() + "," + task.getDescription() + "," + task.getStartTime() +
                "," + task.getDuration().toMinutes();
    }

    //historyToString Преобразовывает историю в строку.
    static private String historyToString(HistoryManager manager, HashMap<Integer, Task> allTasks) {
        List<Task> history = manager.getHistory();
        StringBuilder stringBuilder = new StringBuilder();
        // Прохожу по всем спискам, захожу в эпики, смотрю сабтаски, сверяю с объектами в истории просмотров
        for (Task task : history) {
            for (Integer id : allTasks.keySet()) {
                if (id < 2000) {
                    if (allTasks.get(id).equals(task)) {
                        stringBuilder.append(id).append(",");
                    }
                } else {
                    if (allTasks.get(id).equals(task)) {
                        stringBuilder.append(id).append(",");
                    }
                    Epic epic = (Epic) allTasks.get(id);
                    for (Integer subtaskId : epic.subtasks.keySet()) {
                        if (epic.subtasks.get(subtaskId).equals(task)) {
                            stringBuilder.append(subtaskId).append(",");
                        }
                    }
                }
            }
        }
        //Удаляется последняя запятая
        if (stringBuilder.length() > 0) {
            stringBuilder.setLength(stringBuilder.length() - 1);
        }
        return stringBuilder.toString();
    }


    //Методы, получающие и данные из файла

    //Общий восстанавливающий метод
    public void recovery() {
        List<String> fileToLine = new ArrayList<>();
        String historyListTurn = null;
        String emptyStr = null;

        //Чтение построчно
        try {
            InputStreamReader fr = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(fr);

            while (reader.ready()) {
                fileToLine.add(reader.readLine());
            }
            for (String s : fileToLine) {
                if (s.isBlank()) {
                    if (fileToLine.size() > fileToLine.indexOf(s) + 1) {
                        historyListTurn = fileToLine.get(fileToLine.indexOf(s) + 1);
                    }
                    emptyStr = fileToLine.get(fileToLine.indexOf(s));
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Как сейчас сделал, больше так не делай", e);
        }
        //Удаление последней строки с историей, пустой разделяющей строки и первой
        fileToLine.remove(emptyStr);
        fileToLine.remove(historyListTurn);
        fileToLine.remove(0);

        recoveryTasks(fileToLine);
        if (historyListTurn != null) {
            recoveryHistoryList(historyFromString(historyListTurn));
        }
    }

    //Восстанавливает задачи из построчно считанного списка. Вызывается в методе recovery.
    private void recoveryTasks(List<String> fileToLine) {
        for (String taskLine : fileToLine) {
            String[] taskToPart = taskLine.split(",");
            switch (taskToPart[1]) {
                case "TASK":
                    super.tasks.put(parseInt(taskToPart[0]), fromString(taskToPart));
                    break;
                case "EPIC":
                    super.epics.put(parseInt(taskToPart[0]),(Epic) fromString(taskToPart));
                    break;
                case "SUBTASK":
                    Epic epic = super.epics.get(parseInt(taskToPart[7]));
                    epic.subtasks.put(parseInt(taskToPart[0]), (Subtask) fromString(taskToPart));
                    break;
            }
        }
    }

    //Возвращает задачу из строки. Вызывается в методе recoveryTasks.
    private Task fromString(String[] taskToPart) {
        switch (taskToPart[1]) {
            case "TASK":
                return super.taskMaker(taskToPart[2], taskToPart[4], statusFromString(taskToPart[3]),
                        Duration.ofMinutes(parseLong(taskToPart[6])), LocalDateTime.parse(taskToPart[5]));
            case "EPIC":
                return super.epicMaker(parseInt(taskToPart[0]), taskToPart[2], taskToPart[4]);
            case "SUBTASK":
                return super.subtaskMaker(taskToPart[2], taskToPart[4], statusFromString(taskToPart[3]),
                        Duration.ofMinutes(parseLong(taskToPart[6])), LocalDateTime.parse(taskToPart[5]));
            default:
                return null;
        }
    }

    //Возвращает статус из строки. Вызывается в методе Task fromString(String[] taskToPart)
    private Status statusFromString(String statusString) {
        switch (statusString) {
            case "NEW":
                return Status.NEW;
            case "IN_PROGRESS":
                return Status.IN_PROGRESS;
            case "DONE":
                return Status.DONE;
            default:
                return null;
        }
    }

    //Восстанавливает историю просмотров. Вызывается в методе recovery
    private void recoveryHistoryList(List<Integer> historyFromString) {
        for (Integer id : historyFromString) {
            if (super.tasks.get(id) != null) {
                super.historyManager.add(id, super.tasks.get(id));
            } else if (super.epics.get(id) != null) {
                super.historyManager.add(id, super.epics.get(id));
            } else {
                boolean elementNull = true;
                for (Integer epicId : super.epics.keySet()) {
                    Epic epic = super.epics.get(epicId);
                    if (epic.subtasks.get(id) != null) {
                        super.historyManager.add(id, epic.subtasks.get(id));
                        elementNull = false;
                    }
                }
                if (elementNull) {
                    System.out.println("Не в этот раз, мазафакер");
                }
            }
        }
    }

    //Преобразовывает строку в список целых чисел. Вызывается в методе recovery
    // в качестве аргумента метода recoveryHistoryList
    static private List<Integer> historyFromString(String value) {
        List<Integer> id = new ArrayList<>();
        String[] ids = value.split(",");
        for (String s : ids) {
            id.add(parseInt(s));
        }
        return id;
    }

    @Override
    public void taskAdd(int id, Task task) {
        super.taskAdd(id, task);
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
    public void epicAdd(int id, Epic epic) {
        super.epicAdd(id, epic);
        save();
    }

    @Override
    public void subtaskAdd(int id, int number, Subtask subtask) {
        super.subtaskAdd(id, number, subtask);
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

    public static void main(String[] args) {
        String filePath = new File("src\\Task.csv").getAbsolutePath();
        File file1 = new File(filePath);

        FileBackedTasksManager fileBackedTasksManager = loadFromFile(file1);

        LocalDateTime localDateTime1 = LocalDateTime.of(2023, 1, 1, 12, 30);
        LocalDateTime localDateTime2 = LocalDateTime.of(2023, 1, 2, 12, 30);
        LocalDateTime localDateTime3 = LocalDateTime.of(2023, 1, 3, 12, 30);
        LocalDateTime localDateTime4 = LocalDateTime.of(2023, 1, 4, 12, 30);
        LocalDateTime localDateTime5 = LocalDateTime.of(2023, 1, 5, 12, 30);
        LocalDateTime localDateTime6 = LocalDateTime.of(2023, 1, 6, 12, 30);
        LocalDateTime localDateTime7 = LocalDateTime.of(2023, 1, 7, 12, 30);
        LocalDateTime localDateTime8 = LocalDateTime.of(2023, 1, 8, 12, 30);
        LocalDateTime localDateTime9 = LocalDateTime.of(2023, 1, 9, 12, 30);

        Duration duration1 = Duration.ofMinutes(30);
        Duration duration2 = Duration.ofMinutes(31);
        Duration duration3 = Duration.ofMinutes(32);
        Duration duration4 = Duration.ofMinutes(33);
        Duration duration5 = Duration.ofMinutes(34);
        Duration duration6 = Duration.ofMinutes(35);
        Duration duration7 = Duration.ofMinutes(36);
        Duration duration8 = Duration.ofMinutes(37);
        Duration duration9 = Duration.ofMinutes(38);

        Task task = fileBackedTasksManager.taskMaker("Задача1", "Описание задачи 1", Status.NEW,
                duration1,localDateTime1);
        Task task1 = fileBackedTasksManager.taskMaker("Задача2", "Описание задачи 2", Status.NEW,
                duration2,localDateTime2);
        Task task2 = fileBackedTasksManager.taskMaker("Задача10", "Описание задачи 10", Status.NEW,
                duration3, localDateTime3);

        fileBackedTasksManager.taskAdd(1001, task);
        fileBackedTasksManager.taskAdd(1002, task1);
        fileBackedTasksManager.taskAdd(1003, task2);

        Epic epic = fileBackedTasksManager.epicMaker(2001, "Эпик", "Описание эпика ");
        Epic epic1 = fileBackedTasksManager.epicMaker(2002, "Эпик1", "Описание эпика 1");
        Epic epic2 = fileBackedTasksManager.epicMaker(2003, "Эпик2", "Описание эпика 2");

        fileBackedTasksManager.epicAdd(2001, epic);
        fileBackedTasksManager.epicAdd(2002, epic1);
        fileBackedTasksManager.epicAdd(2003, epic2);

        Subtask subtask = fileBackedTasksManager.subtaskMaker("Подзадача1", "Описание подзадачи 1", Status.DONE,
                duration4, localDateTime1);
        Subtask subtask1 = fileBackedTasksManager.subtaskMaker("Подзадача2", "Описание подзадачи 2", Status.IN_PROGRESS,
                duration5, localDateTime5);
        Subtask subtask2 = fileBackedTasksManager.subtaskMaker("Подзадача3", "Описание подзадачи 3", Status.NEW,
                duration6,localDateTime6);
        Subtask subtask3 = fileBackedTasksManager.subtaskMaker("Подзадача4", "Описание подзадачи 4", Status.DONE,
                duration7,localDateTime7);
        Subtask subtask4 = fileBackedTasksManager.subtaskMaker("Подзадача5", "Описание подзадачи 5", Status.DONE,
                duration8, localDateTime8);
        Subtask subtask5 = fileBackedTasksManager.subtaskMaker("Подзадача6", "Описание подзадачи 6", Status.NEW,
                duration9, localDateTime9);

        fileBackedTasksManager.subtaskAdd(3001, 2001, subtask);
        fileBackedTasksManager.subtaskAdd(3002, 2001, subtask1);
        fileBackedTasksManager.subtaskAdd(3003, 2001, subtask2);
        fileBackedTasksManager.subtaskAdd(3004, 2002, subtask3);
        fileBackedTasksManager.subtaskAdd(3005, 2002, subtask4);
        fileBackedTasksManager.subtaskAdd(3006, 2002, subtask5);

        for (Task priorityList : fileBackedTasksManager.getPrioritizedTasks()) {
            System.out.println(priorityList);
        }
        System.out.println();

        System.out.println("Вывод информации, которая записалась в файл, чтобы сравнить ее с полученной ранее.");
        FileBackedTasksManager recoveryFileBackedTasksManager = new FileBackedTasksManager(file1);
        recoveryFileBackedTasksManager.recovery();

        recoveryFileBackedTasksManager.taskListAllTasks();
        System.out.println();
        recoveryFileBackedTasksManager.epicListAllTasks();
        System.out.println();
        for (Integer id : recoveryFileBackedTasksManager.epics.keySet()) {
            recoveryFileBackedTasksManager.subtaskListAllTasks(id);
        }
        System.out.println();
        List<Task> historyList = recoveryFileBackedTasksManager.getHistory();
        for (Task historyListTask : historyList) {
            System.out.println(historyListTask);
        }
    }
}