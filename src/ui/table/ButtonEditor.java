package ui.table;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import net.miginfocom.swing.MigLayout;
import ui.components.ActionButton;

/**
 *
 * @author intel
 */
public class ButtonEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {

    public interface Actions {

        void increment(int currentRow);

        void decrement(int currentRow);

        void delete(int currentRow);
    }

    private final Actions callback;
    private final JPanel panel = new JPanel();
    private final JButton incrementButton = new ActionButton("+");
    private final JButton decrementButton = new ActionButton("-");
    private final JButton deleteButton = new ActionButton(
        new FlatSVGIcon("resources/icon/delete.svg"),
        ActionButton.DESTRUCTIVE
    );

    private int currentRow;

    private final TableCellRenderer oldCellRenderer;

    public ButtonEditor(Actions callback, JTable table) {
        this.callback = callback;
        oldCellRenderer = table.getDefaultRenderer(Object.class);
        panel.setLayout(new MigLayout("fill, gap 10", "center", "center"));
        panel.add(incrementButton);
        panel.add(decrementButton);
        panel.add(deleteButton);
        incrementButton.addActionListener(this);
        decrementButton.addActionListener(this);
        deleteButton.addActionListener(this);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
        boolean isSelected, int row, int column) {
        currentRow = row;

        Component com = oldCellRenderer.getTableCellRendererComponent(table,
            value, isSelected, true, row, column);

        panel.setBackground(com.getBackground());
        return panel;
    }

    @Override
    public Object getCellEditorValue() {
        return null;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == incrementButton) {
            callback.increment(currentRow);
        } else if (e.getSource() == decrementButton) {
            callback.decrement(currentRow);
        } else if (e.getSource() == deleteButton) {
            fireEditingStopped();  // Stop editing before deleting the row
            callback.delete(currentRow);
        } else {
            fireEditingStopped();
        }
    }
}
