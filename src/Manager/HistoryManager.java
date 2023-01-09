package Manager;

import Tasks.Task;

import java.util.List;

public interface HistoryManager {
     void add(int id,Task task);
     void remove(int id);
     List<Task> getHistory();
}
