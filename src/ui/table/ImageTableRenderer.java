/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ui.table;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author intel
 */
public class ImageTableRenderer implements TableCellRenderer {

    private final TableCellRenderer oldCellRenderer;

    public ImageTableRenderer(JTable table) {
        oldCellRenderer = table.getDefaultRenderer(Object.class);
    }

    @Override
    public Component getTableCellRendererComponent(
        JTable table,
        Object value,
        boolean isSelected,
        boolean hasFocus,
        int row,
        int column
    ) {
        Component com = oldCellRenderer.getTableCellRendererComponent(table,
            value, isSelected, hasFocus, row, column);
        TableImage cell = new TableImage((String) value);
        cell.setBackground(com.getBackground());
        return cell;
    }
}
