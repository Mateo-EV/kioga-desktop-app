package ui.table;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import java.awt.Component;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import net.miginfocom.swing.MigLayout;
import ui.components.ActionButton;

public class ButtonRenderer extends JPanel implements TableCellRenderer {

    private final JButton incrementButton = new ActionButton("+");
    private final JButton decrementButton = new ActionButton("-");
    private final JButton deleteButton = new ActionButton(
        new FlatSVGIcon("resources/icon/delete.svg"),
        ActionButton.DESTRUCTIVE
    );
    private final TableCellRenderer oldCellRenderer;

    public ButtonRenderer(JTable table) {
        oldCellRenderer = table.getDefaultRenderer(Object.class);
        setLayout(new MigLayout("fill, gap 10", "center", "center"));
        add(incrementButton);
        add(decrementButton);
        add(deleteButton);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus, int row, int column) {
        Component com = oldCellRenderer.getTableCellRendererComponent(table,
            value, isSelected, hasFocus, row, column);
        setBackground(com.getBackground());
        return this;
    }
}
