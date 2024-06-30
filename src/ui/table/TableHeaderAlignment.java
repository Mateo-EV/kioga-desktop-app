package ui.table;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

public class TableHeaderAlignment implements TableCellRenderer {

    private final TableCellRenderer oldHeaderRenderer;
    private final TableCellRenderer oldCellRenderer;
    public int position = 1;

    public TableHeaderAlignment(JTable table) {
        this.oldHeaderRenderer = table.getTableHeader().getDefaultRenderer();
        this.oldCellRenderer = table.getDefaultRenderer(Object.class);
        table.setDefaultRenderer(Object.class,
            (JTable jtable, Object o, boolean bln, boolean bln1, int row, int column) -> {
                JLabel label = (JLabel) oldCellRenderer.getTableCellRendererComponent(
                    jtable, o, bln, bln1, row, column);
                label.setHorizontalAlignment(getAlignment(column));
                return label;
            });
    }

    public TableHeaderAlignment(JTable table, int position) {
        this(table);
        this.position = position;
    }

    @Override
    public Component getTableCellRendererComponent(JTable jtable, Object o,
        boolean bln, boolean bln1, int row, int column) {
        JLabel label = (JLabel) oldHeaderRenderer.getTableCellRendererComponent(
            jtable, o, bln, bln1, row, column);
        label.setHorizontalAlignment(getAlignment(column));
        return label;
    }

    protected int getAlignment(int column) {
        if (column == position) {
            return SwingConstants.CENTER;
        }
        return SwingConstants.LEADING;
    }
}
