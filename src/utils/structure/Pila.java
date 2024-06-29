package utils.structure;

public class Pila<T> {
    private Nodo<T> top;
    private int size = 0;

    public void push(T data) {
        Nodo<T> nuevoNodo = new Nodo(data);
        nuevoNodo.next = top;
        top = nuevoNodo;
        size++;
    }

    public T pop() {
        if (top == null) 
            return null;
        T data = top.data;
        top = top.next;
        size--;
        return data;
    }
    
    public T peek() {
        if (top == null) return null;
        
        return top.data;
    }
    
    public void clear(){
        size = 0;
        top = null;
    }

    public boolean isEmpty() {
        return top == null;
    }
    
    public int size(){
        return size;
    }
    
    public T elementAt(int index){
         if (index < 0 || index >= size)
            return null;
        
        int desdeElInicio = size - 1 - index;
        Nodo<T> actual = top;
        
        for (int i = 0; i < desdeElInicio; i++) {
            actual = actual.next;
        }

        return actual.data;
    }
}
