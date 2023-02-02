import Manager.HistoryManager;
import Manager.InMemoryHistoryManager;
import Tasks.Epic;
import Tasks.Status;
import Tasks.Subtask;
import Tasks.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

public class HistoryManagerTest {

    HistoryManager historyManager;

    @BeforeEach
    public void createHistoryManager() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    public void worksWithEmptyHistoryDuplicationDeletion() {
        //Пустая история задач
        List<Task> checkList = historyManager.getHistory();
        Assertions.assertTrue(checkList.isEmpty());

        Task task = new Task("Task", "TaskDescription", Status.NEW);
        HashMap<Integer, Subtask> subtasks = new HashMap<>();
        Epic epic = new Epic("Epic", "EpicDescription", Status.NEW, subtasks);
        Subtask subtask1 = new Subtask("Subtask1", "Subtask1Description", Status.NEW);
        Subtask subtask2 = new Subtask("Subtask2", "Subtask2Description", Status.NEW);
        epic.subtasks.put(3001, subtask1);
        epic.subtasks.put(3002, subtask2);
        //Дублирование
        historyManager.add(2001, epic);
        historyManager.add(1001, task);
        historyManager.add(2001, epic);
        checkList.add(task);
        checkList.add(epic);
        Assertions.assertEquals(historyManager.getHistory().toString(), checkList.toString());

        historyManager.add(3002, subtask2);
        historyManager.add(3001, subtask1);
        checkList.add(subtask2);
        checkList.add(subtask1);
        //Удаление из начала
        historyManager.remove(1001);
        checkList.remove(task);
        Assertions.assertEquals(historyManager.getHistory().toString(), checkList.toString());
        //Удаление из середины
        historyManager.remove(3002);
        checkList.remove(subtask2);
        Assertions.assertEquals(historyManager.getHistory().toString(), checkList.toString());
        //Удаление из конца
        historyManager.remove(3001);
        checkList.remove(subtask1);
        Assertions.assertEquals(historyManager.getHistory().toString(), checkList.toString());
    }
}
