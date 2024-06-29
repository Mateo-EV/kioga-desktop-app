package utils;

import utils.structure.Pila;
import java.util.Iterator;
import java.util.Stack;

public class UndoRedo<E> implements Iterable<E> {

    private final Pila<E> stack1;
    private final Pila<E> stack2;

    public UndoRedo() {
        stack1 = new Pila<>();
        stack2 = new Pila<>();
    }

    public void add(E item) {
        stack1.push(item);
        stack2.clear();
    }

    public E undo() {
        if (stack1.size() > 1) {
            stack2.push(stack1.pop());
            return stack1.peek();
        } else {
            return null;
        }
    }

    public E redo() {
        if (!stack2.isEmpty()) {
            E item = stack2.pop();
            stack1.push(item);
            return item;
        } else {
            return null;
        }
    }

    public E getCurrent() {
        if (stack1.isEmpty()) {
            return null;
        } else {
            return stack1.peek();
        }
    }

    public boolean isUndoAble() {
        return stack1.size() > 1;
    }

    public boolean isRedoAble() {
        return !stack2.isEmpty();
    }

    public void clear() {
        stack1.clear();
        stack2.clear();
    }

    public void clearRedo() {
        stack2.clear();
    }

    @Override
    public Iterator<E> iterator() {
        return new MyIterator();
    }

    private class MyIterator implements Iterator<E> {

        private int index = 0;

        @Override
        public boolean hasNext() {
            if (index < stack1.size()) {
                return true;
            } else if (index < stack1.size() + stack2.size()) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        public E next() {
            if (index < stack1.size()) {
                return stack1.elementAt(index++);
            } else {
                return stack2.elementAt((index++) - stack1.size());
            }
        }
    }
}
