import Manager.EpicManager;
import Manager.TaskManager;
import Tasks.Epic;
import Tasks.Subtask;
import Tasks.Task;

import java.util.Scanner;

public class Main {
    static TaskManager manager = new TaskManager();
    static EpicManager epicManager = new EpicManager();
    static Scanner scanner = new Scanner(System.in);

    static int id = 0;
    static int number = 0;
    static String name = "";
    static String description = "";
    static String status = "";

    public static void main(String[] args) {
        //Комментарии к заданию в файле README
        System.out.println("Выберите, что хотите сделать:");
        while (true) {
            System.out.println("1 -Получение списка всех задач.");
            System.out.println("2 -Удаление всех задач.");
            System.out.println("3 - Получение по идентификатору.");
            System.out.println("4 - Создание. Сам объект должен передаваться в качестве параметра.");
            System.out.println("5 - Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.");
            System.out.println("6 - Удаление по идентификатору.");
            System.out.println("Любая другая цифра - выход");
            int command = scanner.nextInt();
            if (command == 1){
                //Получение списка всех задач.
                taskList();
            } else if (command==2) {
                //Удаление всех задач.
                taskAnnihilator();
            } else if (command==3) {
                //Получение по идентификатору.
                getter();
            } else if (command==4) {
                //Создание. Сам объект должен передаваться в качестве параметра.
                maker();
            } else if (command==5) {
                //Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
                update();
            }else if (command==6){
                //Удаление по идентификатору.
                remote();
            }else {
                return;
            }
        }



    }

    static public void data() {
        System.out.println("Введите название задачи");
        name = scanner.nextLine();
        if(name.equals("")){name = scanner.nextLine();}
        System.out.println("Введите описание задачи");
        description = scanner.nextLine();
            System.out.println("Выберите статус задачи\n 1 - NEW\n 2 - IN_PROGRESS\n 3 - DONE");
            int statusNumber = scanner.nextInt();
            switch (statusNumber) {
                case 1:
                    status = "NEW";
                    break;
                case 2:
                    status = "IN_PROGRESS";
                    break;
                case 3:
                    status = "DONE";
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
                    id+=1;
                manager.taskAdd(id,task);
                    break;
                case 2:
                    data();
                    id+=1;
                    Epic epic = epicManager.epicMaker(id,name,description);
                    epicManager.epicAdd(id,epic);
                    epicManager.takeEpicStatus(id);
                    break;
                case 3:
                    System.out.println("Введите идентефикатор эпика");
                    number = scanner.nextInt();
                    if(epicManager.epics.isEmpty()||!(epicManager.epics.containsKey(number))){
                        System.out.println("Такого эпика не существует");
                        break;
                    }
                    data();
                    Subtask subtask = epicManager.subtaskMaker(name,description,status);
                    id+=1;
                    epicManager.subtaskAdd(id,number,subtask);
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
