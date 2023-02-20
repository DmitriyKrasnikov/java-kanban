package Manager;

import Tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private Node<Task> head;
    private Node<Task> tail;
    private final HashMap<Integer, Node<Task>> historyList = new HashMap<>();


    @Override
    public void add(int id, Task task) {
        if (task != null) {
            remove(id);
            linkLast(id, task);
        }
    }

    @Override
    public void remove(int id) {
        removeNode(historyList.get(id));
        historyList.remove(id);
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private void linkLast(int id, Task element) {
        final Node<Task> oldTail = tail;
        final Node<Task> newNode = new Node<>(oldTail, element, null);
        tail = newNode;
        historyList.put(id, tail);
        if (oldTail == null) head = newNode;
        else oldTail.next = newNode;
    }

    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node<Task> node = head;
        while (node != null) {
            tasks.add(node.data);
            node = node.next;
        }
        return tasks;
    }

    private void removeNode(Node<Task> curNode) {
        if (curNode != null) {
            Node<Task> prev = curNode.prev;
            Node<Task> next = curNode.next;
            curNode.data = null;

            if (head == curNode && tail == curNode) {
                head = null;
                tail = null;
            } else if (head == curNode) {
                head = next;
                head.prev = null;
            } else if (tail == curNode) {
                tail = prev;
                tail.next = null;
            } else {
                prev.next = next;
                next.prev = prev;
            }
        }
    }
}


