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

    public FileBackedTasksManager() {
    }

    static public FileBackedTasksManager loadFromFile(File file) {
        return new FileBackedTasksManager(file);
    }

    //Методы для сохранения в файл

    public HashMap<Integer, Task> allTaskForHistoryList() {
        HashMap<Integer, Task> hashMap = new HashMap<>();
        for (Integer key : super.tasks.keySet()) {
            hashMap.put(key, super.tasks.get(key));
        }
        for (Integer key : super.epics.keySet()) {
            hashMap.put(key, super.epics.get(key));
        }
        return hashMap;
    }

    //save() сохраняет текущее состояние файла. В тз требуется добавить его в каждый модифицирующий метод родителя,
    public void save() {
        //Проходим спискам задач Task и Epic, собираем в одну мапу
        HashMap<Integer, Task> allTasks = allTaskForHistoryList();

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
    static public String historyToString(HistoryManager manager, HashMap<Integer, Task> allTasks) {
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
                    super.epics.put(parseInt(taskToPart[0]), (Epic) fromString(taskToPart));
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
    protected void recoveryHistoryList(List<Integer> historyFromString) {
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
    static public List<Integer> historyFromString(String value) {
        List<Integer> id = new ArrayList<>();
        String[] ids = value.split(",");
        for (String s : ids) {
            if (s.isBlank()){continue;}
            id.add(parseInt(s));
        }
        return id;
    }

    @Override
    public void taskAdd(Task task) {
        super.taskAdd(task);
    }

    @Override
    public void taskRemove(int number) {
        super.taskRemove(number);
    }

    @Override
    public void taskDeleteAll() {
        super.taskDeleteAll();
    }

    @Override
    public void taskUpdate(Task task, int number) {
        super.taskUpdate(task, number);
    }

    @Override
    public void epicAdd(Epic epic) {
        super.epicAdd(epic);
    }

    @Override
    public void subtaskAdd(int number, Subtask subtask) {
        super.subtaskAdd(number, subtask);
        super.takeEpicStatus(number);
    }

    @Override
    public void epicDeleteAll() {
        super.epicDeleteAll();
    }

    @Override
    public void subtaskDeleteAll(int number) {
        super.subtaskDeleteAll(number);
    }

    @Override
    public void epicUpdate(Epic epic, int number) {
        super.epicUpdate(epic, number);
        super.takeEpicStatus(number);
    }

    @Override
    public void subtaskUpdate(Subtask subtask, int epicNumber, int subtaskNumber) {
        super.subtaskUpdate(subtask, epicNumber, subtaskNumber);
        super.takeEpicStatus(epicNumber);
    }

    @Override
    public void epicRemove(int number) {
        super.epicRemove(number);
    }

    @Override
    public void subtaskRemove(int epicNumber, int subtaskNumber) {
        super.subtaskRemove(epicNumber, subtaskNumber);
    }

    @Override
    public void taskGetById(int number) {
        super.taskGetById(number);
    }

    @Override
    public void epicGetById(int number) {
        super.epicGetById(number);
    }

    @Override
    public void subtaskGetById(int number1, int number2) {
        super.subtaskGetById(number1, number2);
    }

    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        return super.getPrioritizedTasks();
    }

}