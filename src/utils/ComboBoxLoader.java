package utils;

import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.DefaultComboBoxModel;
import javax.swing.SwingWorker;
import models.Identifiable;
import utils.structure.ArbolBinario;

public class ComboBoxLoader<T extends Identifiable> extends SwingWorker<Void, T> {

    private final DefaultComboBoxModel<T> model;
    private final ArbolBinario<T> tree;

    public ComboBoxLoader(DefaultComboBoxModel<T> model, ArbolBinario<T> tree) {
        this.model = model;
        this.tree = tree;
    }

    @Override
    protected Void doInBackground() {
        tree.forEach(this::publish);
        return null;
    }

    @Override
    protected void process(List<T> chunks) {
        for (T item : chunks) {
            model.addElement(item);
        }
    }

    @Override
    protected void done() {
        try {
            get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public static <T extends Identifiable> void loadItems(
        DefaultComboBoxModel<T> model, ArbolBinario<T> tree) {
        new ComboBoxLoader<>(model, tree).execute();
    }
}
