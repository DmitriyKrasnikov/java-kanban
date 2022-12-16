package Manager;

import Tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{
    private final static List<Task> historyList = new ArrayList<>();
    private static int historyId = 0;

    public void add(Task task){
            changeHistoryList(task);
            changeHistoryId();
    }

    public List<Task> getHistory(){
        return historyList;
    }

    private void changeHistoryId(){
        if(historyId > 9){
            historyId = 0;
        }
    }

    private void changeHistoryList(Task task){
        if ((historyList.isEmpty())||(historyList.size() < 10)){
            historyList.add(task);
        }else {
            historyList.remove(historyId);
            historyList.add(historyId, task);
        }
        historyId+=1;
    }
}
