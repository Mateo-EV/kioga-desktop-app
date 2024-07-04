package views;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import controllers.BrandController;
import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import models.Brand;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.TimingTargetAdapter;
import raven.alerts.MessageAlerts;
import raven.popup.DefaultOption;
import raven.popup.GlassPanePopup;
import raven.popup.component.SimplePopupBorder;
import raven.toast.Notifications;
import ui.components.ActionButton;
import ui.components.LoadingSkeleton;
import ui.components.SimpleForm;
import ui.table.CheckBoxTableHeaderRenderer;
import ui.table.ImageTableRenderer;
import ui.table.TableHeaderAlignment;
import utils.ApiClient;
import utils.GlobalCacheState;
import utils.structure.ArbolBinario;
import views.dialog.CreateBrandForm;
import views.dialog.DeleteBrandForm;
import views.dialog.EditBrandForm;

public class BrandPage extends SimpleForm {

    private final LoadingSkeleton loadingSkeleton;
    private TableRowSorter rowSorter;

    public BrandPage() {
        initComponents();
        setLayout(new MigLayout("wrap,fill,insets 10", "fill", "fill"));
        loadingSkeleton = new LoadingSkeleton();
        init();
    }

    @Override
    public void formRefresh() {
        showLoadingSkeleton();
        loadData();
    }

    private void init() {
        showLoadingSkeleton();

        lbTitle.putClientProperty(
            FlatClientProperties.STYLE,
            "font:+5;"
            + "font:bold +5"
        );

        txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT,
            "Buscar marcas");
        txtSearch.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON,
            new FlatSVGIcon("resources/icon/search.svg"));

        panel.putClientProperty(
            FlatClientProperties.STYLE,
            "arc:25"
        );

        rowSorter = new TableRowSorter(table.getModel());
        table.setRowSorter(rowSorter);

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                applyFilter();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                applyFilter();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                applyFilter();
            }

            private void applyFilter() {
                String text = txtSearch.getText();
                if (text.trim().length() == 0) {
                    rowSorter.setRowFilter(null);
                } else {
                    rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });

        table.getTableHeader().putClientProperty(
            FlatClientProperties.STYLE,
            "height:30;"
            + "hoverBackground:null;"
            + "pressedBackground:null;"
            + "separatorColor:$TableHeader.background;"
            + "font:bold;"
        );

        table.putClientProperty(
            FlatClientProperties.STYLE,
            "rowHeight:70;"
            + "showHorizontalLines:true;"
            + "intercellSpacing:0,1;"
            + "cellFocusColor:$TableHeader.hoverBackground"
            + "selectionBackground:$TableHeader.hoverBackground"
        );

        table.getColumnModel().getColumn(0).setHeaderRenderer(
            new CheckBoxTableHeaderRenderer(table, 0)
        );

        table.getTableHeader().setDefaultRenderer(
            new TableHeaderAlignment(table));

        table.getColumnModel().getColumn(3).setCellRenderer(
            new ImageTableRenderer(table));

        table.getColumnModel().getColumn(6).setCellRenderer(
            new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table,
                Object value, boolean isSelected, boolean hasFocus, int row,
                int column) {
                return null;
            }
        });

        loadData();
    }

    public static void syncBrands() {
        DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
        tableModel.setRowCount(0);
        GlobalCacheState.getBrands().forEach(a -> {
            addProductToTable(a);
        });
    }

    private static void addProductToTable(Brand brand) {
        DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
        tableModel.addRow(new Object[]{
            false,
            brand.getId(),
            brand.getName(),
            brand.getImage(),
            brand.getCreatedAtDate(),
            brand.getUpdatedAtDate(),
            brand
        });
    }

    private void showTable() {
        scroll.setViewportView(table);
        Color color = table.getForeground();
        table.setForeground(new Color(color.getRed(),
            color.getGreen(),
            color.getBlue(), 0));

        Animator animator = new Animator(750); // Duración de 1 segundo
        animator.addTarget(new TimingTargetAdapter() {
            @Override
            public void timingEvent(float fraction) {
                int alpha = (int) (255 * fraction);
                table.setForeground(
                    new Color(
                        color.getRed(),
                        color.getGreen(),
                        color.getBlue(),
                        alpha
                    )
                );
            }
        });
        animator.start();
    }

    private void showLoadingSkeleton() {
        loadingSkeleton.startLoading();
        scroll.setViewportView(loadingSkeleton);
    }

    private void loadData() {
        DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
        BrandController.getInstance().findAll(new ApiClient.onResponse() {
            @Override
            public void onSuccess(ApiClient.ApiResponse response) {
                ArbolBinario<Brand> brands = (ArbolBinario<Brand>) response.getData();
                tableModel.setRowCount(0);
                brands.forEach((b) -> addProductToTable(b));
                showTable();
                loadingSkeleton.stopLoading();
            }

            @Override
            public void onError(ApiClient.ApiResponse response) {
                MessageAlerts.getInstance().showMessage(
                    "Error",
                    response.getMessage(),
                    MessageAlerts.MessageType.ERROR
                );
                loadingSkeleton.stopLoading();
            }
        });
    }

    private List<Brand> getSelectedData() {
        List<Brand> list = new ArrayList();
        for (int i = 0; i < table.getRowCount(); i++) {
            if ((boolean) table.getValueAt(i, 0)) {
                list.add((Brand) table.getValueAt(i, 6));
            }
        }
        return list;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel = new javax.swing.JPanel();
        scroll = new javax.swing.JScrollPane();
        jSeparator1 = new javax.swing.JSeparator();
        txtSearch = new javax.swing.JTextField();
        lbTitle = new javax.swing.JLabel();
        btnDelete = new ui.components.ActionButton(ActionButton.DESTRUCTIVE);
        btnAdd = new ui.components.ActionButton();
        btnEdit = new ui.components.ActionButton(ActionButton.SECONDARY);

        setLayout(null);

        scroll.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "SELECT", "#", "NOMBRE", "IMAGEN", "CREADO", "ACTUALIZADO", ""
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table.getTableHeader().setReorderingAllowed(false);
        scroll.setViewportView(table);
        if (table.getColumnModel().getColumnCount() > 0) {
            table.getColumnModel().getColumn(0).setMaxWidth(50);
            table.getColumnModel().getColumn(1).setMaxWidth(50);
            table.getColumnModel().getColumn(2).setPreferredWidth(150);
            table.getColumnModel().getColumn(3).setPreferredWidth(100);
            table.getColumnModel().getColumn(4).setPreferredWidth(150);
            table.getColumnModel().getColumn(5).setPreferredWidth(150);
            table.getColumnModel().getColumn(6).setPreferredWidth(0);
            table.getColumnModel().getColumn(6).setMaxWidth(0);
        }

        lbTitle.setText("Marcas");

        btnDelete.setText("Eliminar");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        btnAdd.setText("Agregar");
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        btnEdit.setText("Editar");
        btnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelLayout = new javax.swing.GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scroll)
            .addComponent(jSeparator1)
            .addGroup(panelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelLayout.createSequentialGroup()
                        .addComponent(lbTitle)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(panelLayout.createSequentialGroup()
                        .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 414, Short.MAX_VALUE)
                        .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(lbTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(scroll, javax.swing.GroupLayout.DEFAULT_SIZE, 572, Short.MAX_VALUE)
                .addGap(10, 10, 10))
        );

        add(panel);
        panel.setBounds(0, 0, 1062, 663);
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        CreateBrandForm dialog = new CreateBrandForm();
        DefaultOption option = new DefaultOption() {
            @Override
            public boolean closeWhenClickOutside() {
                return true;
            }
        };
        GlassPanePopup.showPopup(
            new SimplePopupBorder(
                dialog,
                "Crear Marca"), option);
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        List<Brand> list = getSelectedData();
        if (list.isEmpty()) {
            Notifications.getInstance().show(Notifications.Type.WARNING,
                "Selecciona al menos un elemento para borrar");
            return;
        }

        DeleteBrandForm dialog = new DeleteBrandForm(list);
        DefaultOption option = new DefaultOption() {
            @Override
            public boolean closeWhenClickOutside() {
                return true;
            }
        };
        GlassPanePopup.showPopup(
            new SimplePopupBorder(
                dialog,
                "¿Estás seguro?"), option);
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        List<Brand> list = getSelectedData();
        if (list.isEmpty() || list.size() > 1) {
            Notifications.getInstance().show(Notifications.Type.WARNING,
                "Selecciona 1 elemento para editar");
            return;
        }

        EditBrandForm dialog = new EditBrandForm(list.get(0).getId());
        GlassPanePopup.showPopup(
            new SimplePopupBorder(
                dialog,
                "Editar Marca"), new DefaultOption() {
            @Override
            public boolean closeWhenClickOutside() {
                return true;
            }
        });
    }//GEN-LAST:event_btnEditActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private ui.components.ActionButton btnAdd;
    private ui.components.ActionButton btnDelete;
    private ui.components.ActionButton btnEdit;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lbTitle;
    private javax.swing.JPanel panel;
    private javax.swing.JScrollPane scroll;
    public static final javax.swing.JTable table = new javax.swing.JTable();
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
