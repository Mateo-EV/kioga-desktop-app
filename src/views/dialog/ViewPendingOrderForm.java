/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package views.dialog;

import com.formdev.flatlaf.FlatClientProperties;
import controllers.OrderController;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import models.Order;
import models.OrderProduct;
import models.OrderStatus;
import net.miginfocom.swing.MigLayout;
import raven.popup.DefaultOption;
import raven.popup.GlassPanePopup;
import raven.popup.component.SimplePopupBorder;
import raven.toast.Notifications;
import ui.components.ActionButton;
import ui.components.LoadingButton;
import ui.components.LoadingSkeleton;
import ui.table.ImageTableRenderer;
import ui.table.TableHeaderAlignment;
import utils.ApiClient;
import utils.structure.Cola;

/**
 *
 * @author intel
 */
public class ViewPendingOrderForm extends javax.swing.JPanel {

    static private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(
        new Locale("es", "PE"));
    private final JPanel loadingPanel = new JPanel();

    private final LoadingSkeleton skeletons[] = {
        new LoadingSkeleton(),
        new LoadingSkeleton(),
        new LoadingSkeleton(),
        new LoadingSkeleton(),
        new LoadingSkeleton(),};

    private Order actualOrderToSave;

    private final Cola<Order> pendientOrders;

    private final LoadingButton saveBtn = new LoadingButton("Siguiente");
    private final ActionButton cancelBtn = new ActionButton("Dejar Pendiente",
        ActionButton.SECONDARY);

    public ViewPendingOrderForm(Cola<Order> pendientOrders) {
        initComponents();
        initTableModel();
        this.pendientOrders = pendientOrders;
        init();
    }

    private void initTableModel() {
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
            + "cellFocusColor:#282828;"
            + "selectionBackground:#282828"
        );

        table.getTableHeader().setDefaultRenderer(
            new TableHeaderAlignment(table, -1));
        table.getColumnModel().getColumn(1).setCellRenderer(
            new ImageTableRenderer(table));
    }

    private void showLoadingState() {
        CardLayout ly = (CardLayout) getLayout();
        for (LoadingSkeleton skeleton : skeletons) {
            skeleton.startLoading();
        }
        ly.show(this, "form-loading");
    }

    private void stopLoadingState() {
        CardLayout ly = (CardLayout) getLayout();
        ly.show(this, "form-loaded");
        for (LoadingSkeleton skeleton : skeletons) {
            skeleton.stopLoading();
        }
    }

    private void initLayoutForLoading() {
        loadingPanel.setLayout(
            new MigLayout(
                "wrap,fill,insets 20 50 20 50,gap 20",
                "fill",
                "fill"
            )
        );

        for (LoadingSkeleton skeleton : skeletons) {
            loadingPanel.add(skeleton);
        }

        add(loadingPanel, "form-loading");
    }

    private void loadNewOrder() {
        OrderController.getInstance().findById(actualOrderToSave.getId(),
            new ApiClient.onResponse() {
            @Override
            public void onSuccess(ApiClient.ApiResponse apiResponse) {
                stopLoadingState();
                Order order = (Order) apiResponse.getData();
                actualOrderToSave = order;

                txtCodigo.setText(order.getCode());
                txtCodigo.setEnabled(false);

                txtCliente.setText(
                    order.getCustomer().getName() + "<" + order.getCustomer().getEmail() + ">");
                txtCliente.setEnabled(false);

                if (order.getIsDelivery()) {
                    rbIsDelivery.setSelected(true);
                    lbEnvio.setVisible(true);
                    lbSubtotal.setVisible(true);
                    txtEnvio.setVisible(true);
                    txtSubtotal.setVisible(true);

                    saveBtn.setText("Enviar a cliente");
                } else {
                    rbNotDelivery.setSelected(true);
                    lbEnvio.setVisible(false);
                    lbSubtotal.setVisible(false);
                    txtEnvio.setVisible(false);
                    txtSubtotal.setVisible(false);

                    saveBtn.setText("Poner en espera");
                }

                txtNotes.setText(order.getNotes());
                txtNotes.setEnabled(false);

                ((DefaultTableModel) table.getModel()).setRowCount(0);
                for (OrderProduct detail : order.getDetails()) {
                    addDetail(detail);
                }

                updateTotalField();

            }

            @Override
            public void onError(ApiClient.ApiResponse apiResponse) {

                stopLoadingState();
                GlassPanePopup.closePopupLast();
                Notifications.getInstance().show(Notifications.Type.ERROR,
                    apiResponse.getMessage());
            }
        });
    }

    private void resetFields() {

        txtCodigo.setText("");
        txtCodigo.setEnabled(true);

        txtCliente.setText("");
        txtCliente.setEnabled(true);

        txtNotes.setText("");
        txtNotes.setEnabled(true);

        txtNotes.setEnabled(false);

        ((DefaultTableModel) table.getModel()).setRowCount(0);
    }

    private void init() {
        actualOrderToSave = pendientOrders.dequeue();

        initLayoutForLoading();

        showLoadingState();

        loadNewOrder();

        FooterPanel.add(saveBtn, BorderLayout.EAST);
        FooterPanel.add(cancelBtn, BorderLayout.WEST);

        cancelBtn.addActionListener((e) -> {
            actualOrderToSave = pendientOrders.dequeue();

            if (actualOrderToSave == null) {
                GlassPanePopup.closePopupLast();
                Notifications.getInstance().show(
                    Notifications.Type.INFO,
                    "No hay más pedidos pendientes"
                );
                return;
            }
            resetFields();
            showLoadingState();
            loadNewOrder();
        });
        saveBtn.addActionListener((e) -> {
            saveBtn.startLoading();
            if (actualOrderToSave.getIsDelivery()) {
                actualOrderToSave.setStatus(OrderStatus.ENVIADO);
            } else {
                actualOrderToSave.setStatus(OrderStatus.WAITING);
            }

            OrderController.getInstance().updateStatus(actualOrderToSave,
                new ApiClient.onResponse() {
                @Override
                public void onSuccess(ApiClient.ApiResponse apiResponse) {
                    Notifications.getInstance().show(
                        Notifications.Type.SUCCESS,
                        "Pedido "
                        + (actualOrderToSave.getIsDelivery()
                        ? "enviado correctamente"
                        : "en espera correctamente")
                    );

                    saveBtn.stopLoading();
                    actualOrderToSave = pendientOrders.dequeue();
                    if (actualOrderToSave == null) {
                        GlassPanePopup.closePopupLast();
                        Notifications.getInstance().show(
                            Notifications.Type.INFO,
                            "No hay más pedidos pendientes"
                        );
                        return;
                    }
                    resetFields();
                    showLoadingState();
                    loadNewOrder();
                }

                @Override
                public void onError(ApiClient.ApiResponse apiResponse) {
                    Notifications.getInstance().show(Notifications.Type.ERROR,
                        apiResponse.getMessage());

                    saveBtn.stopLoading();
                }
            });
        });
    }

    public void updateTotalField() {
        double subTotal
            = actualOrderToSave.getDetails().stream()
                .map(detail
                    -> detail.getQuantity()
                * detail.getUnitAmount())
                .reduce(0.0, Double::sum);

        txtSubtotal.setEnabled(true);
        txtSubtotal.setValue(subTotal);
        txtSubtotal.setEnabled(false);

        txtTotal.setEnabled(true);
        txtTotal.setValue(actualOrderToSave.getAmount());
        txtTotal.setEnabled(false);
    }

    public void addDetail(OrderProduct detail) {
        DefaultTableModel tableModel = (DefaultTableModel) table.getModel();

        tableModel.addRow(new Object[]{
            detail.getProduct().getName(),
            detail.getProduct().getImage(),
            detail.getUnitAmountFormatted(),
            String.valueOf(detail.getQuantity()),});
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnGisDelivery = new javax.swing.ButtonGroup();
        PanelContainer = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtNotes = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        FooterPanel = new javax.swing.JPanel();
        rbIsDelivery = new javax.swing.JRadioButton();
        rbNotDelivery = new javax.swing.JRadioButton();
        detailPanel = new javax.swing.JPanel();
        scroll = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        lbSubtotal = new javax.swing.JLabel();
        lbEnvio = new javax.swing.JLabel();
        lbTotal = new javax.swing.JLabel();
        txtEnvio = new javax.swing.JFormattedTextField(currencyFormat);
        txtSubtotal = new javax.swing.JFormattedTextField(currencyFormat);
        txtTotal = new javax.swing.JFormattedTextField(currencyFormat);
        lbSubtotal1 = new javax.swing.JLabel();
        txtStatus = new javax.swing.JTextField();
        txtCliente = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtCodigo = new javax.swing.JTextField();
        actionButton1 = new ui.components.ActionButton();

        setLayout(new java.awt.CardLayout());

        txtNotes.setEditable(false);
        txtNotes.setColumns(20);
        txtNotes.setRows(5);
        jScrollPane1.setViewportView(txtNotes);

        jLabel1.setText("Cliente");

        FooterPanel.setLayout(new java.awt.BorderLayout());

        btnGisDelivery.add(rbIsDelivery);
        rbIsDelivery.setText("Envío a domicilio");
        rbIsDelivery.setEnabled(false);

        btnGisDelivery.add(rbNotDelivery);
        rbNotDelivery.setText("Retiro en tienda");
        rbNotDelivery.setEnabled(false);

        scroll.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "NOMBRE", "IMAGEN", "PRECIO", "CANTIDAD"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table.getTableHeader().setReorderingAllowed(false);
        scroll.setViewportView(table);

        javax.swing.GroupLayout detailPanelLayout = new javax.swing.GroupLayout(detailPanel);
        detailPanel.setLayout(detailPanelLayout);
        detailPanelLayout.setHorizontalGroup(
            detailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(detailPanelLayout.createSequentialGroup()
                .addComponent(scroll, javax.swing.GroupLayout.PREFERRED_SIZE, 566, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        detailPanelLayout.setVerticalGroup(
            detailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(detailPanelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(scroll, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel4.setText("Notas");

        lbSubtotal.setText("Subtotal");

        lbEnvio.setText("Envio");

        lbTotal.setText("Total");

        txtEnvio.setEditable(false);
        txtEnvio.setEnabled(false);
        txtEnvio.setValue(5);

        txtSubtotal.setEditable(false);
        txtSubtotal.setEnabled(false);
        txtSubtotal.setValue(0);

        txtTotal.setEditable(false);
        txtTotal.setEnabled(false);
        txtTotal.setValue(5);

        lbSubtotal1.setText("Estado");

        txtStatus.setEditable(false);
        txtStatus.setText("Pendiente");
        txtStatus.setEnabled(false);

        txtCliente.setEditable(false);

        jLabel2.setText("Código");

        txtCodigo.setEditable(false);

        actionButton1.setText("Ver Dirección");
        actionButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                actionButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PanelContainerLayout = new javax.swing.GroupLayout(PanelContainer);
        PanelContainer.setLayout(PanelContainerLayout);
        PanelContainerLayout.setHorizontalGroup(
            PanelContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelContainerLayout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addGroup(PanelContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(PanelContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(PanelContainerLayout.createSequentialGroup()
                            .addGroup(PanelContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(lbSubtotal)
                                .addComponent(lbTotal)
                                .addComponent(lbEnvio))
                            .addGap(63, 63, 63)
                            .addGroup(PanelContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(txtEnvio, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtSubtotal, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(PanelContainerLayout.createSequentialGroup()
                                    .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 111, Short.MAX_VALUE)
                                    .addComponent(lbSubtotal1)
                                    .addGap(65, 65, 65)
                                    .addComponent(txtStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, PanelContainerLayout.createSequentialGroup()
                            .addComponent(rbIsDelivery)
                            .addGap(18, 18, 18)
                            .addComponent(rbNotDelivery)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(actionButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(FooterPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, PanelContainerLayout.createSequentialGroup()
                            .addGroup(PanelContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE)
                                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(PanelContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(txtCliente)
                                .addComponent(txtCodigo))))
                    .addComponent(detailPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(50, 50, 50))
        );
        PanelContainerLayout.setVerticalGroup(
            PanelContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelContainerLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(PanelContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(PanelContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rbIsDelivery)
                    .addComponent(rbNotDelivery)
                    .addComponent(actionButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addComponent(detailPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addGroup(PanelContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSubtotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbSubtotal))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbEnvio)
                    .addComponent(txtEnvio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbTotal)
                    .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbSubtotal1)
                    .addComponent(txtStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(FooterPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10))
        );

        add(PanelContainer, "form-loaded");
    }// </editor-fold>//GEN-END:initComponents

    private void actionButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_actionButton1ActionPerformed
        DefaultOption option = new DefaultOption() {
            @Override
            public boolean closeWhenClickOutside() {
                return true;
            }
        };
        CreateVolatilAddress dialog = new CreateVolatilAddress(
            actualOrderToSave.getIsDelivery(),
            actualOrderToSave.getAddress());
        GlassPanePopup.showPopup(
            new SimplePopupBorder(
                dialog,
                "Ver dirección"), option
        );
    }//GEN-LAST:event_actionButton1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel FooterPanel;
    private javax.swing.JPanel PanelContainer;
    private ui.components.ActionButton actionButton1;
    private javax.swing.ButtonGroup btnGisDelivery;
    private javax.swing.JPanel detailPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbEnvio;
    private javax.swing.JLabel lbSubtotal;
    private javax.swing.JLabel lbSubtotal1;
    private javax.swing.JLabel lbTotal;
    private javax.swing.JRadioButton rbIsDelivery;
    private javax.swing.JRadioButton rbNotDelivery;
    private javax.swing.JScrollPane scroll;
    private javax.swing.JTable table;
    private javax.swing.JTextField txtCliente;
    private javax.swing.JTextField txtCodigo;
    private javax.swing.JFormattedTextField txtEnvio;
    private javax.swing.JTextArea txtNotes;
    private javax.swing.JTextField txtStatus;
    private javax.swing.JFormattedTextField txtSubtotal;
    private javax.swing.JFormattedTextField txtTotal;
    // End of variables declaration//GEN-END:variables
}
