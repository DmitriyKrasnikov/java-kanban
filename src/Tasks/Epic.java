package Tasks;

import java.util.HashMap;

public class Epic extends Task {
    public HashMap<Integer, Subtask> subtasks;

    public Epic(String name, String description, String status,HashMap<Integer, Subtask> subtasks) {
        super(name, description, status);
        this.subtasks = subtasks;
    }
}
