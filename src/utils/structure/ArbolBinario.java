package utils.structure;

import java.util.function.Consumer;
import models.Identifiable;

public class ArbolBinario<T extends Identifiable> {

    class ArbolNodo<T extends Identifiable> {

        T data;
        ArbolNodo<T> left;
        ArbolNodo<T> right;

        public ArbolNodo(T data) {
            this.data = data;
            this.left = null;
            this.right = null;
        }
    }

    private ArbolNodo<T> root;

    public ArbolBinario() {
        this.root = null;
    }

    public void insert(T data) {
        root = insertRec(root, data);
    }

    private ArbolNodo<T> insertRec(ArbolNodo<T> root, T data) {
        if (root == null) {
            root = new ArbolNodo(data);
            return root;
        }
        if (data.getId() > root.data.getId()) {
            root.left = insertRec(root.left, data);
        } else if (data.getId() < root.data.getId()) {
            root.right = insertRec(root.right, data);
        }
        return root;
    }

    public T find(int id) {
        return findRec(root, id);
    }

    private T findRec(ArbolNodo<T> root, int id) {
        if (root == null || root.data.getId() == id) {
            return root != null ? root.data : null;
        }
        if (id > root.data.getId()) {
            return findRec(root.left, id);
        } else {
            return findRec(root.right, id);
        }
    }

    public void delete(int id) {
        root = deleteRec(root, id);
    }

    public void update(T newData) {
        updateRec(root, newData);
    }

    private void updateRec(ArbolNodo<T> root, T newData) {
        if (root == null) {
            return;
        }
        if (root.data.getId() == newData.getId()) {
            root.data = newData;
            return;
        }
        if (newData.getId() > root.data.getId()) {
            updateRec(root.left, newData);
        } else {
            updateRec(root.right, newData);
        }
    }

    private ArbolNodo<T> deleteRec(ArbolNodo<T> root, int id) {
        if (root == null) {
            return root;
        }
        if (id > root.data.getId()) {
            root.left = deleteRec(root.left, id);
        } else if (id < root.data.getId()) {
            root.right = deleteRec(root.right, id);
        } else {
            if (root.left == null) {
                return root.right;
            } else if (root.right == null) {
                return root.left;
            }
            root.data = maxValue(root.right);
            root.right = deleteRec(root.right, root.data.getId());
        }
        return root;
    }

    private T maxValue(ArbolNodo<T> root) {
        T maxValue = root.data;
        while (root.left != null) {
            maxValue = root.left.data;
            root = root.left;
        }
        return maxValue;
    }

    public void inorder() {
        inorderRec(root);
    }

    private void inorderRec(ArbolNodo<T> root) {
        if (root != null) {
            inorderRec(root.left);
            System.out.println(root.data.getId());
            inorderRec(root.right);
        }
    }

    public boolean isEmpty() {
        return root == null;
    }

    public void forEach(Consumer<T> action) {
        forEachRec(root, action);
    }

    private void forEachRec(ArbolNodo<T> node, Consumer<T> action) {
        if (node != null) {
            forEachRec(node.left, action);
            action.accept(node.data);
            forEachRec(node.right, action);
        }
    }

    public void forEachReverse(Consumer<T> action) {
        forEachReverseRec(root, action);
    }

    private void forEachReverseRec(ArbolNodo<T> node, Consumer<T> action) {
        if (node != null) {
            forEachReverseRec(node.right, action);
            action.accept(node.data);
            forEachReverseRec(node.left, action);
        }
    }
}
