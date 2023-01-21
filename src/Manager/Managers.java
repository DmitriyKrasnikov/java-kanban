package Manager;

public class Managers {

    public static TaskManager getDefault(){
        return new InMemoryTaskManager();
    }
    //Оставил старую реализацию, потому что не знаю, какие в будущем будут изменения в задании. Поэтому, если в тз
    // не прописано, что конкретно это необходимо изменить, то лучше это не трогать)))

    public static HistoryManager getDefaultHistory(){
        return new InMemoryHistoryManager();
    }

}
