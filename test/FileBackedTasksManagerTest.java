import Manager.FileBackedTasksManager;
import Tasks.Epic;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.HashMap;

public class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    static File file = new File("test\\TaskTest.csv");

    @BeforeEach
    public void create() {
        manager = FileBackedTasksManager.loadFromFile(file);
    }

    @Test
    public void recoveryAndSaveEmptyTaskListAndHistoryList() {
        Assertions.assertTrue(manager.taskHashMap().isEmpty(), "Список сохраняемых задач не пуст");
        Assertions.assertTrue(manager.epicHashMap().isEmpty(), "Список сохраняемых эпиков не пуст");
        Assertions.assertTrue(manager.getHistory().isEmpty(), "Сохраняемая история просмотров не пуста");
        manager.save();
        manager = FileBackedTasksManager.loadFromFile(file);
        manager.recovery();
        Assertions.assertTrue(manager.taskHashMap().isEmpty(), "Список восстановленных задач не пуст");
        Assertions.assertTrue(manager.epicHashMap().isEmpty(), "Список восстановленных задач не пуст");
        Assertions.assertTrue(manager.getHistory().isEmpty(), "Восстановленная история просмотров не пустая");

    }

    @Test
    public void recoveryAndSaveEpicWithoutSubtask() {
        Epic epic = manager.epicMaker(2001, "TestEpic", "TestEpicDescription");
        manager.epicAddWhithId(2001, epic);
        Assertions.assertEquals(manager.epicHashMap().size(), 1);
        Assertions.assertEquals(manager.epicHashMap().get(2001), epic);
        HashMap<Integer, Epic> epics = manager.epicHashMap();

        manager = FileBackedTasksManager.loadFromFile(file);
        manager.recovery();

    }
}
