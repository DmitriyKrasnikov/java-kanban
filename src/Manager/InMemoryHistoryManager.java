package Manager;

import Tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{
    private final List<Task> historyList = new ArrayList<>();
    @Override
    public void add(Task task){
        if (task!= null) {
            historyList.add(task);
            if (historyList.size() > 10) {
                historyList.remove(0);
            }
        }
    }
    @Override
    public List<Task> getHistory(){
        return historyList;
    }

}

/*1.	ArrayList выбрал для реализации, просто потому что пользовался им, и он сюда подходит.
2.	Static в 9 и 10 строчке был для того, чтобы в случае создания нескольких объектов данного класса,
намеренно или по ошибке, сам список оставался прежним. Если будет несколько объектов, то и списки будут разные.
Но если понадобится, чтобы было несколько списков, то модификаторы будут не нужны. Так что всё зависит от задачи.
3.	То, как используется метод add(Task task) исключает возможность передачи в аргумент task = null, так как он
вызывается в методе GetById(int number) класса InMemoryTaskManager в том случае, если список задач не пустой.
Но проверку я добавил.
4.	Метод добавления задач в список переписал, спасибо.*/

