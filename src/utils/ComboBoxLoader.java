package utils;

import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.DefaultComboBoxModel;
import javax.swing.SwingWorker;

public class ComboBoxLoader<T> extends SwingWorker<Void, T> {

    private final DefaultComboBoxModel<T> model;
    private final List<T> items;

    public ComboBoxLoader(DefaultComboBoxModel<T> model, List<T> items) {
        this.model = model;
        this.items = items;
    }

    @Override
    protected Void doInBackground() {
        for (T item : items) {
            publish(item);
        }
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

    public static <T> void loadItems(DefaultComboBoxModel<T> model,
        List<T> items) {
        new ComboBoxLoader<>(model, items).execute();
    }
}
