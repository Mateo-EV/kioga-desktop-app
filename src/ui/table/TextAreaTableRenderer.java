package ui.table;

import java.awt.Component;
import java.awt.Insets;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.TableCellRenderer;

public class TextAreaTableRenderer extends JTextArea implements TableCellRenderer {

    private final TableCellRenderer oldCellRenderer;

    public TextAreaTableRenderer(JTable table) {
        oldCellRenderer = table.getDefaultRenderer(Object.class);
        setLineWrap(true);
        setWrapStyleWord(true);
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus,
        int row, int column) {
        Component com = oldCellRenderer.getTableCellRendererComponent(table,
            value, isSelected, hasFocus, row, column);
        setText((value == null) ? "" : value.toString());
        setBackground(com.getBackground());

        // Calcular la altura requerida para el texto
        int textHeight = getPreferredSize().height;
        int rowHeight = table.getRowHeight(row);
        int verticalMargin = (rowHeight - textHeight) / 2;

        // Ajustar el margen vertical para centrar el texto
        setMargin(new Insets(verticalMargin, 5, verticalMargin, 5));
        return this;
    }
}
