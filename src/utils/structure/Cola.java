package utils.structure;

public class Cola<T> {
    private Nodo<T> cabecera;
    private Nodo<T> cola;

    public void enqueue(T data) {
        Nodo<T> nuevoNodo = new Nodo(data);
        if (cola != null)
            cola.next = nuevoNodo;
        cola = nuevoNodo;
        if (cabecera == null) {
            cabecera = nuevoNodo;
        }
    }
    
    public T front() {
        if(isEmpty()) return null;
        return cabecera.data;
    }

    public T dequeue() {
        if (cabecera == null) return null;
        T data = cabecera.data;
        cabecera = cabecera.next;
        if (cabecera == null)
            cola = null;
        return data;
    }

    public boolean isEmpty() {
        return cabecera == null;
    }
}
