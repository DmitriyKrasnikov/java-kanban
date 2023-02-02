import Manager.*;
import Tasks.Epic;
import Tasks.Status;
import Tasks.Subtask;
import Tasks.Task;

import java.util.List;
import java.util.Scanner;

public class Main {
    private static final TaskManager manager = new InMemoryTaskManager();
    private static final Scanner scanner = new Scanner(System.in);

    private static int taskId = 1000;
    private static int epicId = 2000;
    private static int subTaskId = 3000;
    private static int number = 0;
    private static String name = "";
    private static String description = "";
    private static Status status;

    public static void main(String[] args) {

        System.out.println("Выберите, что хотите сделать:");
        while (true) {
            System.out.println("1 - Получение списка всех задач.");
            System.out.println("2 - Удаление всех задач.");
            System.out.println("3 - Получение по идентификатору.");
            System.out.println("4 - Создание. Сам объект должен передаваться в качестве параметра.");
            System.out.println("5 - Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.");
            System.out.println("6 - Удаление по идентификатору.");
            System.out.println("7 - Получить историю просмотров");
            System.out.println("8 - Выход");
            int command = scanner.nextInt();
            switch (command){
                case 1:
                    //Получение списка всех задач.
                    taskList();
                    break;
                case 2:
                    //Удаление всех задач.
                    taskAnnihilator();
                    break;
                case 3:
                    //Получение по идентификатору.
                    getter();
                    break;
                case 4:
                    //Создание.
                    maker();
                    break;
                case 5:
                    //Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
                    update();
                    break;
                case 6:
                    //Удаление по идентификатору.
                    remote();
                    break;
                case 7:
                    //Получение истории просмотров
                    showHistory();
                    break;
                case 8:
                    return;
                default:
                    System.out.println("Неверная команда");
                    break;
            }
        }
    }
    static private void showHistory(){
        List<Task> history = manager.getHistory();
        for (Task task : history){
            System.out.println(task);
        }
    }
    static private void data() {
        System.out.println("Введите название задачи");
        name = scanner.nextLine();
        if(name.equals("")){name = scanner.nextLine();}
        System.out.println("Введите описание задачи");
        description = scanner.nextLine();
            System.out.println("Выберите статус задачи\n 1 - NEW\n 2 - IN_PROGRESS\n 3 - DONE");
            int statusNumber = scanner.nextInt();
            switch (statusNumber) {
                case 1:
                    status = Status.NEW;
                    break;
                case 2:
                    status = Status.IN_PROGRESS;
                    break;
                case 3:
                    status = Status.DONE;
                    break;
                default:
                    System.out.println("Не мороси");
                    break;
            }
    }

    static private void taskList(){
        while (true) {
            System.out.println("Выберите какой список вы хотите получить\n 1 - Задачи\n 2 - Эпики\n 3 - Подзадачи\n 4 - Выход");
            int taskListNumber = scanner.nextInt();
            if (taskListNumber==4){break;}
            switch (taskListNumber) {
                case 1:
                    manager.taskListAllTasks();
                    break;
                case 2:
                    manager.epicListAllTasks();
                    break;
                case 3:
                    System.out.println("Введите идентификатор эпика");
                    number = scanner.nextInt();
                    manager.subtaskListAllTasks(number);
                    break;
                default:
                    System.out.println("Не мороси");
                    break;
            }
        }
    }

    static private void taskAnnihilator(){
        while (true) {
            System.out.println("Выберите какой список вы хотите удалить\n 1 - Задачи\n 2 - Эпики\n 3 - Подзадачи\n 4 - Выход");
            int taskAnnihilatorNumber = scanner.nextInt();
            if (taskAnnihilatorNumber==4){break;}
            switch (taskAnnihilatorNumber) {
                case 1:
                    manager.taskDeleteAll();
                    System.out.println("Правильно! Лучше полежать на диване)");
                    break;
                case 2:
                    manager.epicDeleteAll();
                    System.out.println("Если проблема решаема,то не стоит о ней беспокоиться.\n " +
                            "Если проблема нерешаема, то не стоит беспокоиться тем более");
                    break;
                case 3:
                    System.out.println("Введите идентификатор эпика");
                    number = scanner.nextInt();
                    manager.subtaskDeleteAll(number);
                    System.out.println("Нет смысла иметь подзадачи, когда эпик сам, как подзадача");
                    manager.takeEpicStatus(number);
                    break;
                default:
                    System.out.println("Не мороси");
                    break;
            }
        }
    }

    static private void getter(){
        while (true) {
            System.out.println("Выберите из какого списка вы хотите получить\n 1 - Задачи\n 2 - Эпики\n 3 - Подзадачи\n 4 - Выход");
            int getterNumber = scanner.nextInt();
            if (getterNumber==4){break;}
            System.out.println("Введите идентификатор задачи");
            int idNumber = scanner.nextInt();
            switch (getterNumber) {
                case 1:
                    manager.taskGetById(idNumber);
                    break;
                case 2:
                    manager.epicGetById(idNumber);
                    break;
                case 3:
                    System.out.println("Введите идентификатор эпика");
                    number = scanner.nextInt();
                    manager.subtaskGetById(number, idNumber);
                    break;
                default:
                    System.out.println("Не мороси");
                    break;
            }
        }
    }

    static private void maker(){
        while (true) {
            System.out.println("Выберите что вы хотите добавить\n 1 - Задачи\n 2 - Эпики\n 3 - Подзадачи\n 4 - Выход");
            int makerNumber = scanner.nextInt();
            if (makerNumber==4){break;}
            switch (makerNumber) {
                case 1:
                    data();
                Task task = manager.taskMaker(name,description,status);
                    taskId+=1;
                    manager.taskAdd(taskId,task);
                    break;
                case 2:
                    data();
                    epicId+=1;
                    Epic epic = manager.epicMaker(epicId,name,description);
                    manager.epicAdd(epicId,epic);
                    manager.takeEpicStatus(epicId);
                    break;
                case 3:
                    manager.epicListAllTasks();
                    System.out.println("Введите идентификатор эпика");
                    number = scanner.nextInt();
                    if(manager.checkEpicsHashMap(number)){
                        System.out.println("Такого эпика не существует");
                        break;
                    }
                    data();
                    Subtask subtask = manager.subtaskMaker(name,description,status);
                    subTaskId+=1;
                    manager.subtaskAdd(subTaskId,number,subtask);
                    manager.takeEpicStatus(number);
                    break;
                default:
                    System.out.println("Не мороси");
                    break;
            }
        }
    }

    static private void update(){
        while (true) {
            System.out.println("Выберите что вы хотите обновить\n 1 - Задачи\n 2 - Эпики\n 3 - Подзадачи\n 4 - Выход");
            int makerNumber = scanner.nextInt();
            if (makerNumber==4){break;}
            System.out.println("Введите идентификатор задачи");
            int idNumber = scanner.nextInt();
            switch (makerNumber) {
                case 1:
                    data();
                    Task task = manager.taskMaker(name,description,status);
                    manager.taskUpdate(task, idNumber);
                    break;
                case 2:
                    data();
                    Epic epic = manager.epicMaker(idNumber,name,description);
                    manager.epicUpdate(epic,idNumber);
                    manager.takeEpicStatus(idNumber);
                    break;
                case 3:
                    System.out.println("Введите идентификатор эпика");
                    number = scanner.nextInt();
                    data();
                    Subtask subtask = manager.subtaskMaker(name,description,status);
                    manager.subtaskUpdate(subtask, number, idNumber);
                    manager.takeEpicStatus(number);
                    break;
                default:
                    System.out.println("Не мороси");
                    break;
            }
        }
    }

    static private void remote(){
        while (true) {
            System.out.println("Выберите что вы хотите удалить\n 1 - Задачи\n 2 - Эпики\n 3 - Подзадачи\n 4 - Выход");
            int makerNumber = scanner.nextInt();
            if (makerNumber==4){break;}
            System.out.println("Введите идентификатор задачи");
            int idNumber = scanner.nextInt();
            switch (makerNumber) {
                case 1:
                    manager.taskRemove(idNumber);
                    break;
                case 2:
                    manager.epicRemove(idNumber);
                    break;
                case 3:
                    System.out.println("Введите идентификатор эпика");
                    number = scanner.nextInt();
                    manager.subtaskRemove(number, idNumber);
                    manager.takeEpicStatus(number);
                    break;
                default:
                    System.out.println("Не мороси");
                    break;
            }
        }
    }
}
