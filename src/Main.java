import Manager.EpicManager;
import Manager.TaskManager;
import Tasks.Epic;
import Tasks.Status;
import Tasks.Subtask;
import Tasks.Task;

import java.util.Scanner;

public class Main {
    public static TaskManager manager = new TaskManager();
    public static EpicManager epicManager = new EpicManager();
    public static Scanner scanner = new Scanner(System.in);

    public static int taskId = 1000;
    public static int epicId = 2000;
    public static int subTaskId = 3000;
    public static int number = 0;
    public static String name = "";
    public static String description = "";
    public static Status status;

    public static void main(String[] args) {
        //На этот раз комментарии в коде.
        //Все замечания устранил, за исключением создания абстрактного класса для TaskManager и EpicManager и
        // лямбда - выражений. 
        System.out.println("Выберите, что хотите сделать:");
        while (true) {
            System.out.println("1 - Получение списка всех задач.");
            System.out.println("2 - Удаление всех задач.");
            System.out.println("3 - Получение по идентификатору.");
            System.out.println("4 - Создание. Сам объект должен передаваться в качестве параметра.");
            System.out.println("5 - Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.");
            System.out.println("6 - Удаление по идентификатору.");
            System.out.println("7 - Выход");
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
                    //Создание. Сам объект должен передаваться в качестве параметра.
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
                    return;
                default:
                    System.out.println("Неверная команда");
                    break;

            }
        }
    }

    public interface changeStatus{

    }

    static public void data() {
        System.out.println("Введите название задачи");
        name = scanner.nextLine();
        if(name.equals("")){name = scanner.nextLine();}
        System.out.println("Введите описание задачи");
        description = scanner.nextLine();
            System.out.println("Выберите статус задачи\n 1 - NEW\n 2 - IN_PROGRESS\n 3 - DONE");
            int statusNumber = scanner.nextInt();
            switch (statusNumber) {//Лямбда - выражения я понял в общих чертах, но как применить их здесь я не придумал.
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

    static public void taskList(){
        while (true) {
            System.out.println("Выберите какой список вы хотите получить\n 1 - Задачи\n 2 - Эпики\n 3 - Подзадачи\n 4 - Выход");
            int taskListNumber = scanner.nextInt();
            if (taskListNumber==4){break;}
            switch (taskListNumber) {
                case 1:
                    manager.taskListAllTasks();
                    break;
                case 2:
                    epicManager.epicListAllTasks();
                    break;
                case 3:
                    System.out.println("Введите идентефикатор эпика");
                    number = scanner.nextInt();
                    epicManager.subtaskListAllTasks(number);
                    break;
                default:
                    System.out.println("Не мороси");
                    break;
            }
        }
    }

    static public void taskAnnihilator(){
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
                    epicManager.epicDeleteAll();
                    System.out.println("Если проблема решаема,то не стоит о ней беспокоиться.\n " +
                            "Если проблема нерешаема, то не стоит беспокоиться тем более");
                    break;
                case 3:
                    System.out.println("Введите идентефикатор эпика");
                    number = scanner.nextInt();
                    epicManager.subtaskDeleteAll(number);
                    System.out.println("Нет смысла иметь подзадачи, когда эпик сам, как подзадача");
                    epicManager.takeEpicStatus(number);
                    break;
                default:
                    System.out.println("Не мороси");
                    break;
            }
        }
    }
    static public void getter(){
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
                    epicManager.epicGetById(idNumber);
                    break;
                case 3:
                    System.out.println("Введите идентефикатор эпика");
                    number = scanner.nextInt();
                    epicManager.subtaskGetById(number, idNumber);
                    break;
                default:
                    System.out.println("Не мороси");
                    break;
            }
        }
    }

    static public void maker(){
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
                    Epic epic = epicManager.epicMaker(epicId,name,description);
                    epicManager.epicAdd(epicId,epic);
                    epicManager.takeEpicStatus(epicId);
                    break;
                case 3:
                    System.out.println("Введите идентефикатор эпика");
                    number = scanner.nextInt();
                    if(epicManager.checkEpicsHashMap(number)){
                        System.out.println("Такого эпика не существует");
                        break;
                    }
                    data();
                    Subtask subtask = epicManager.subtaskMaker(name,description,status);
                    subTaskId+=1;
                    epicManager.subtaskAdd(subTaskId,number,subtask);
                    epicManager.takeEpicStatus(number);
                    break;
                default:
                    System.out.println("Не мороси");
                    break;
            }
        }
    }

    static public void update(){
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
                    Epic epic = epicManager.epicMaker(idNumber,name,description);
                    epicManager.epicUpdate(epic,idNumber);
                    epicManager.takeEpicStatus(idNumber);
                    break;
                case 3:
                    System.out.println("Введите идентефикатор эпика");
                    number = scanner.nextInt();
                    data();
                    Subtask subtask = epicManager.subtaskMaker(name,description,status);
                    epicManager.subtaskUpdate(subtask, number, idNumber);
                    epicManager.takeEpicStatus(number);
                    break;
                default:
                    System.out.println("Не мороси");
                    break;
            }
        }
    }

    static public void remote(){
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
                    epicManager.epicRemove(idNumber);
                    break;
                case 3:
                    System.out.println("Введите идентефикатор эпика");
                    number = scanner.nextInt();
                    epicManager.subtaskRemove(number, idNumber);
                    epicManager.takeEpicStatus(number);
                    break;
                default:
                    System.out.println("Не мороси");
                    break;
            }
        }
    }


}
