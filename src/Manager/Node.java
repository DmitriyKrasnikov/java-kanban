package Manager;

public class Node<Task> {
    protected Task data;
    protected Node<Task> next;
    protected Node<Task> prev;

    protected Node(Node<Task> prev, Task data, Node<Task> next) {
        this.data = data;
        this.next = next;
        this.prev = prev;
    }
}
