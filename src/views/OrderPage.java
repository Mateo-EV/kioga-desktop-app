package views;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import controllers.OrderController;
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
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import models.Order;
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
import ui.table.TableHeaderAlignment;
import ui.table.TableImage;
import utils.ApiClient;
import utils.GlobalCacheState;
import utils.structure.ArbolBinario;
import utils.structure.Cola;
import views.dialog.CreateOrderForm;
import views.dialog.DeleteOrderForm;
import views.dialog.EditOrderForm;
import views.dialog.ViewPendingOrderForm;

public class OrderPage extends SimpleForm {

    private final LoadingSkeleton loadingSkeleton;
    private static TableRowSorter rowSorter;

    public OrderPage() {
        initComponents();
        setLayout(new MigLayout("wrap,fill,insets 10", "fill", "fill"));
        loadingSkeleton = new LoadingSkeleton();
        init();
    }

    @Override
    public void formRefresh() {
        showLoadingSkeleton();
        table.setRowSorter(null);
        loadData();
        table.setRowSorter(rowSorter);
    }

    private void init() {
        showLoadingSkeleton();

        lbTitle.putClientProperty(
            FlatClientProperties.STYLE,
            "font:+5;"
            + "font:bold +5"
        );

        btnPending.setIcon(new FlatSVGIcon("resources/menu/calendar.svg"));

        txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT,
            "Buscar pedidos");
        txtSearch.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON,
            new FlatSVGIcon("resources/icon/search.svg"));

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
                    rowSorter.setRowFilter(
                        new RowFilter<TableModel, Integer>() {
                        @Override
                        public boolean include(
                            Entry<? extends TableModel, ? extends Integer> entry) {
                            for (int i = 0; i < entry.getValueCount(); i++) {
                                Object value = entry.getValue(i);
                                if (value instanceof TableImage) {
                                    return false;
                                } else if (value != null && value.toString().toLowerCase().contains(
                                    text.toLowerCase())) {
                                    return true;
                                }
                            }
                            return false;
                        }
                    });
                }
            }
        });

        panel.putClientProperty(
            FlatClientProperties.STYLE,
            "arc:25"
        );

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

        table.getColumnModel().getColumn(7).setCellRenderer(
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

    public static void syncOrders() {
        DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
        table.setRowSorter(null);
        tableModel.setRowCount(0);
        table.setRowSorter(rowSorter);
        GlobalCacheState.getOrders().forEach(order -> {
            addOrderToTable(order);
        });
    }

    private static void addOrderToTable(Order order) {
        DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
        tableModel.addRow(new Object[]{
            false,
            order.getCode(),
            order.getAmountFormatted(),
            1,
            order.getStatus().getValue(),
            order.getCreatedAtDate(),
            order.getCreatedAtTime(),
            order
        });
    }

    private void loadData() {
        DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
        OrderController.getInstance().findAll(new ApiClient.onResponse() {
            @Override
            public void onSuccess(ApiClient.ApiResponse response) {
                ArbolBinario<Order> orders = (ArbolBinario<Order>) response.getData();
                tableModel.setRowCount(0);
                orders.forEach((order) -> addOrderToTable(order));
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

    private List<Order> getSelectedData() {
        List<Order> list = new ArrayList();
        for (int i = 0; i < table.getRowCount(); i++) {
            if ((boolean) table.getValueAt(i, 0)) {
                Order data = (Order) table.getValueAt(i, 7);
                list.add(data);
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
        actionButton1 = new ui.components.ActionButton(ActionButton.DESTRUCTIVE);
        actionButton2 = new ui.components.ActionButton();
        actionButton3 = new ui.components.ActionButton(ActionButton.SECONDARY);
        btnPending = new ui.components.ActionButton(ActionButton.SECONDARY);

        setLayout(null);

        scroll.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "SELECT", "CÓDIGO", "IMPORTE", "PRODUCTOS", "ESTADO", "FECHA", "HORA", ""
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, false, false, false, false, false, false
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
            table.getColumnModel().getColumn(1).setMaxWidth(100);
            table.getColumnModel().getColumn(2).setPreferredWidth(50);
            table.getColumnModel().getColumn(3).setPreferredWidth(150);
            table.getColumnModel().getColumn(4).setPreferredWidth(150);
            table.getColumnModel().getColumn(5).setPreferredWidth(150);
            table.getColumnModel().getColumn(7).setPreferredWidth(0);
            table.getColumnModel().getColumn(7).setMaxWidth(0);
        }

        lbTitle.setText("Pedidos");

        actionButton1.setText("Eliminar");
        actionButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                actionButton1ActionPerformed(evt);
            }
        });

        actionButton2.setText("Agregar");
        actionButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                actionButton2ActionPerformed(evt);
            }
        });

        actionButton3.setText("Editar");
        actionButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                actionButton3ActionPerformed(evt);
            }
        });

        btnPending.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPendingActionPerformed(evt);
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
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 354, Short.MAX_VALUE)
                        .addComponent(btnPending, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(actionButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(actionButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(actionButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                        .addComponent(actionButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(actionButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(actionButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnPending, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(scroll, javax.swing.GroupLayout.DEFAULT_SIZE, 572, Short.MAX_VALUE)
                .addGap(10, 10, 10))
        );

        add(panel);
        panel.setBounds(0, 0, 1062, 663);
    }// </editor-fold>//GEN-END:initComponents

    private void actionButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_actionButton2ActionPerformed
        CreateOrderForm dialog = new CreateOrderForm();
        DefaultOption option = new DefaultOption() {
            @Override
            public boolean closeWhenClickOutside() {
                return true;
            }
        };
        GlassPanePopup.showPopup(
            new SimplePopupBorder(
                dialog,
                "Crear Pedido"), option);
    }//GEN-LAST:event_actionButton2ActionPerformed

    private void actionButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_actionButton1ActionPerformed
        List<Order> list = getSelectedData();
        if (list.isEmpty()) {
            Notifications.getInstance().show(Notifications.Type.WARNING,
                "Selecciona al menos un elemento para borrar");
            return;
        }

        DeleteOrderForm dialog = new DeleteOrderForm(list);
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
    }//GEN-LAST:event_actionButton1ActionPerformed

    private void btnPendingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPendingActionPerformed
        Cola<Order> pendingOrders = OrderController.getInstance().getOrdersToDelivery();
        if (pendingOrders.isEmpty()) {
            Notifications.getInstance().show(
                Notifications.Type.INFO,
                "No hay más pedidos pendientes"
            );
            return;
        }

        ViewPendingOrderForm dialog = new ViewPendingOrderForm(pendingOrders);
        GlassPanePopup.showPopup(
            new SimplePopupBorder(
                dialog,
                "Pedidos Pendientes"), new DefaultOption() {
            @Override
            public boolean closeWhenClickOutside() {
                return true;
            }
        });
    }//GEN-LAST:event_btnPendingActionPerformed

    private void actionButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_actionButton3ActionPerformed
        List<Order> list = getSelectedData();
        if (list.isEmpty() || list.size() > 1) {
            Notifications.getInstance().show(Notifications.Type.WARNING,
                "Selecciona 1 elemento para editar");
            return;
        }

        EditOrderForm dialog = new EditOrderForm(list.get(0).getId());
        GlassPanePopup.showPopup(
            new SimplePopupBorder(
                dialog,
                "Editar Pedido"), new DefaultOption() {
            @Override
            public boolean closeWhenClickOutside() {
                return true;
            }
        });
    }//GEN-LAST:event_actionButton3ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private ui.components.ActionButton actionButton1;
    private ui.components.ActionButton actionButton2;
    private ui.components.ActionButton actionButton3;
    private ui.components.ActionButton btnPending;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lbTitle;
    private javax.swing.JPanel panel;
    private javax.swing.JScrollPane scroll;
    public static final javax.swing.JTable table = new javax.swing.JTable();
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
