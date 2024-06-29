package utils.structure;

public class Nodo<T> {
    T data;
    Nodo<T> next;

    Nodo(T data) {
        this.data = data;
    }
}
