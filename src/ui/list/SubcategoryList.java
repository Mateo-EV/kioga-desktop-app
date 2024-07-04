package ui.list;

import java.awt.Component;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class SubcategoryList extends JList<SubcategoryListItem> {

    public SubcategoryList() {
        setCellRenderer(new CustomListCellRenderer());
    }

    private class CustomListCellRenderer implements ListCellRenderer<SubcategoryListItem> {

        @Override
        public Component getListCellRendererComponent(
            JList<? extends SubcategoryListItem> list, SubcategoryListItem value,
            int index, boolean isSelected, boolean cellHasFocus) {
            value.setJlist(list);
            value.setIndex(index);
            return value;
        }
    }

}
