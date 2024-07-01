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
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import javax.swing.AbstractButton;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import models.Address;
import models.Customer;
import models.Order;
import models.OrderProduct;
import models.OrderStatus;
import models.Product;
import net.miginfocom.swing.MigLayout;
import raven.popup.GlassPanePopup;
import raven.toast.Notifications;
import ui.components.ActionButton;
import ui.components.LoadingButton;
import ui.components.LoadingSkeleton;
import ui.table.ButtonEditor;
import ui.table.ButtonRenderer;
import ui.table.ImageTableRenderer;
import ui.table.TableHeaderAlignment;
import utils.ApiClient;
import utils.ComboBoxLoader;
import utils.GlobalCacheState;

/**
 *
 * @author intel
 */
public class CreateOrderForm extends javax.swing.JPanel {

    private static final DefaultComboBoxModel<Customer> customersModel = new DefaultComboBoxModel();
    private static final DefaultComboBoxModel<Product> productsModel = new DefaultComboBoxModel();
    static private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(
        new Locale("es", "PE"));

    private final DefaultComboBoxModel<Address> addressesModel = new DefaultComboBoxModel();

    private final Order orderToSave = new Order();

    public CreateOrderForm() {
        initComponents();
        initcbModels();
        initTableModel();
        init();
    }

    static public void syncCustomers() {
        customersModel.removeAllElements();
        for (Customer customer : GlobalCacheState.getCustomers()) {
            customersModel.addElement(customer);
        }
    }

    static public void syncProducts() {
        productsModel.removeAllElements();
        for (Product product : GlobalCacheState.getProducts()) {
            productsModel.addElement(product);
        }
    }

    private void initcbModels() {
        cbCustomers.setModel(customersModel);
        cbProducts.setModel(productsModel);
        cbAddresses.setModel(addressesModel);

        cbAddresses.setRenderer(
            (list, value, index, isSelected, cellHasFocus) -> {
                DefaultListCellRenderer renderer = new DefaultListCellRenderer();
                JLabel label = (JLabel) renderer.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    return label;
                }

                label.setText(value.getFormattedDirection());
                return label;
            });
        cbCustomers.setRenderer(
            (list, value, index, isSelected, cellHasFocus) -> {
                DefaultListCellRenderer renderer = new DefaultListCellRenderer();
                JLabel label = (JLabel) renderer.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    return label;
                }

                label.setText(value.getName() + " <" + value.getEmail() + ">");
                return label;
            });
        cbProducts.setRenderer(
            (list, value, index, isSelected, cellHasFocus) -> {
                DefaultListCellRenderer renderer = new DefaultListCellRenderer();
                JLabel label = (JLabel) renderer.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    return label;
                }
                label.setText(value.getName());
                return label;
            });

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

        DefaultTableModel tableModel = (DefaultTableModel) table.getModel();

        table.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer(
            table));
        table.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor(
            new ButtonEditor.Actions() {
            @Override
            public void decrement(int row) {
                int value = Integer.parseInt((String) tableModel.getValueAt(row,
                    3));

                if (value == 1) {
                    return;
                }
                int newValue = value + 1;

                orderToSave.getDetails().get(row).setQuantity(newValue);
                tableModel.setValueAt(String.valueOf(newValue), row, 3);

                updateTotalField();
            }

            @Override
            public void increment(int row) {
                int value = Integer.parseInt((String) tableModel.getValueAt(row,
                    3));

                if (value == 10) {
                    return;
                }
                int newValue = value + 1;

                orderToSave.getDetails().get(row).setQuantity(newValue);
                tableModel.setValueAt(String.valueOf(newValue), row, 3);

                updateTotalField();
            }

            @Override
            public void delete(int row) {
                orderToSave.getDetails().remove(row);
                tableModel.removeRow(row);

                updateTotalField();
            }

        }, table));
    }

    private void init() {

        JPanel loadingPanel = new JPanel();
        loadingPanel.setLayout(
            new MigLayout(
                "wrap,fill,insets 20 50 20 50,gap 20",
                "fill",
                "fill"
            )
        );
        LoadingSkeleton skeletons[] = {
            new LoadingSkeleton(),
            new LoadingSkeleton(),
            new LoadingSkeleton(),
            new LoadingSkeleton(),
            new LoadingSkeleton(),};
        for (LoadingSkeleton skeleton : skeletons) {
            skeleton.startLoading();
            loadingPanel.add(skeleton);
        }

        add(loadingPanel, "form-loading");
        CardLayout ly = (CardLayout) getLayout();
        JPanel parent = this;
        ly.show(parent, "form-loading");

        if (customersModel.getSize() == 0 || productsModel.getSize() == 0) {
            OrderController.getInstance().getDataToSave(
                new ApiClient.onResponse() {
                @Override
                public void onSuccess(ApiClient.ApiResponse apiResponse) {
                    System.out.println(apiResponse.getData());
                    Object responses[] = (Object[]) apiResponse.getData();
                    List<Customer> customers = (List<Customer>) responses[0];
                    List<Product> products = (List<Product>) responses[1];

                    ComboBoxLoader.loadItems(customersModel, customers);
                    ComboBoxLoader.loadItems(productsModel, products);

                    ly.show(parent, "form-loaded");
                    for (LoadingSkeleton skeleton : skeletons) {
                        skeleton.stopLoading();
                    }
                    loadingPanel.removeAll();
                }

                @Override
                public void onError(ApiClient.ApiResponse apiResponse) {
                    GlassPanePopup.closePopupLast();
                    Notifications.getInstance().show(
                        Notifications.Type.ERROR,
                        apiResponse.getMessage()
                    );
                }
            });
        } else {
            ly.show(parent, "form-loaded");
            for (LoadingSkeleton skeleton : skeletons) {
                skeleton.stopLoading();
            }
            loadingPanel.removeAll();
        }

        LoadingButton saveBtn = new LoadingButton("Guardar");
        ActionButton cancelBtn = new ActionButton("Cancelar",
            ActionButton.SECONDARY);
        FooterPanel.add(saveBtn, BorderLayout.EAST);
        FooterPanel.add(cancelBtn, BorderLayout.WEST);

        cancelBtn.addActionListener((e) -> {
            GlassPanePopup.closePopupLast();
        });
        saveBtn.addActionListener((e) -> {
            saveBtn.startLoading();
            orderToSave.setCustomer((Customer) customersModel.getSelectedItem());
            orderToSave.setStatus(OrderStatus.fromString(
                (String) cbStatus.getModel().getSelectedItem()));
            orderToSave.setAddress((Address) addressesModel.getSelectedItem());
            orderToSave.setIsDelivery(isDelivery());
            orderToSave.setNotes(txtNotes.getText());
            OrderController.getInstance().save(orderToSave,
                new ApiClient.onResponse() {
                @Override
                public void onSuccess(ApiClient.ApiResponse apiResponse) {
                    GlassPanePopup.closePopupLast();
                    Notifications.getInstance().show(
                        Notifications.Type.SUCCESS,
                        "Pedido agregado correctamente"
                    );

                    saveBtn.stopLoading();
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
            = orderToSave.getDetails().stream()
                .map(detail
                    -> detail.getQuantity()
                * detail.getProduct().getPriceDiscounted())
                .reduce(0.0, Double::sum);

        txtSubtotal.setEnabled(true);
//        txtSubtotal.setText(String.valueOf(subTotal));
        txtSubtotal.setValue(subTotal);
        txtSubtotal.setEnabled(false);

        txtTotal.setEnabled(true);
        txtTotal.setValue(subTotal + (isDelivery() ? 5 : 0));
        txtTotal.setEnabled(false);
    }

    public void addDetail(Product product) {
        DefaultTableModel tableModel = (DefaultTableModel) table.getModel();

        if (orderToSave.getDetails().stream().anyMatch(
            (detail)
            -> detail.getProduct() == product
        )) {
            Notifications.getInstance().show(Notifications.Type.WARNING,
                "Este producto ya ha sido agregado");
            return;
        }

        OrderProduct detail = new OrderProduct();
        detail.setProduct(product);
        detail.setQuantity(1);

        orderToSave.getDetails().add(detail);

        tableModel.addRow(new Object[]{
            product.getName(),
            product.getImage(),
            product.getPriceDiscountedFormatted(),
            "1",
            null
        });

        updateTotalField();
    }

    public boolean isDelivery() {
        for (Enumeration<AbstractButton> buttons = btnGisDelivery.getElements(); buttons.hasMoreElements();) {
            AbstractButton button = buttons.nextElement();
            if (button.isSelected()) {
                if (button.getText().equals("Envío a domicilio")) {
                    return true;
                }
            }
        }
        return false;
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
        cbCustomers = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtNotes = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        FooterPanel = new javax.swing.JPanel();
        rbIsDelivery = new javax.swing.JRadioButton();
        rbNotDelivery = new javax.swing.JRadioButton();
        jLabel5 = new javax.swing.JLabel();
        cbAddresses = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        cbProducts = new javax.swing.JComboBox<>();
        detailPanel = new javax.swing.JPanel();
        scroll = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        btnaddprod = new ui.components.ActionButton();
        jLabel4 = new javax.swing.JLabel();
        lbSubtotal = new javax.swing.JLabel();
        lbEnvio = new javax.swing.JLabel();
        lbTotal = new javax.swing.JLabel();
        txtEnvio = new javax.swing.JFormattedTextField(currencyFormat);
        txtSubtotal = new javax.swing.JFormattedTextField(currencyFormat);
        txtTotal = new javax.swing.JFormattedTextField(currencyFormat);
        lbSubtotal1 = new javax.swing.JLabel();
        cbStatus = new javax.swing.JComboBox<>();

        setLayout(new java.awt.CardLayout());

        cbCustomers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbCustomersActionPerformed(evt);
            }
        });

        txtNotes.setColumns(20);
        txtNotes.setRows(5);
        jScrollPane1.setViewportView(txtNotes);

        jLabel1.setText("Cliente");

        FooterPanel.setLayout(new java.awt.BorderLayout());

        btnGisDelivery.add(rbIsDelivery);
        rbIsDelivery.setSelected(true);
        rbIsDelivery.setText("Envío a domicilio");
        rbIsDelivery.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbIsDeliveryActionPerformed(evt);
            }
        });

        btnGisDelivery.add(rbNotDelivery);
        rbNotDelivery.setText("Retiro en tienda");
        rbNotDelivery.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbNotDeliveryActionPerformed(evt);
            }
        });

        jLabel5.setText("Dirección");

        cbAddresses.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbAddressesActionPerformed(evt);
            }
        });

        jLabel6.setText("Productos");

        cbProducts.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbProductsActionPerformed(evt);
            }
        });

        scroll.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "NOMBRE", "IMAGEN", "PRECIO", "CANTIDAD", ""
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table.getTableHeader().setReorderingAllowed(false);
        scroll.setViewportView(table);
        if (table.getColumnModel().getColumnCount() > 0) {
            table.getColumnModel().getColumn(4).setResizable(false);
        }

        javax.swing.GroupLayout detailPanelLayout = new javax.swing.GroupLayout(detailPanel);
        detailPanel.setLayout(detailPanelLayout);
        detailPanelLayout.setHorizontalGroup(
            detailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scroll)
        );
        detailPanelLayout.setVerticalGroup(
            detailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scroll, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        btnaddprod.setText("+");
        btnaddprod.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnaddprodActionPerformed(evt);
            }
        });

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

        cbStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Pendiente", "En Espera", "Enviado", "Entregado", "Cancelado" }));

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
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lbSubtotal1)
                                    .addGap(39, 39, 39)
                                    .addComponent(cbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, PanelContainerLayout.createSequentialGroup()
                            .addComponent(rbIsDelivery)
                            .addGap(18, 18, 18)
                            .addComponent(rbNotDelivery))
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(detailPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(FooterPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(PanelContainerLayout.createSequentialGroup()
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(cbProducts, javax.swing.GroupLayout.PREFERRED_SIZE, 431, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btnaddprod, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, PanelContainerLayout.createSequentialGroup()
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(cbCustomers, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(PanelContainerLayout.createSequentialGroup()
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(cbAddresses, javax.swing.GroupLayout.PREFERRED_SIZE, 465, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(50, 50, 50))
        );
        PanelContainerLayout.setVerticalGroup(
            PanelContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelContainerLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(PanelContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbCustomers, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7)
                .addGroup(PanelContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rbIsDelivery)
                    .addComponent(rbNotDelivery))
                .addGap(7, 7, 7)
                .addGroup(PanelContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(PanelContainerLayout.createSequentialGroup()
                        .addGroup(PanelContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cbAddresses, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(7, 7, 7)
                        .addGroup(PanelContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cbProducts, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(btnaddprod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7)
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
                    .addComponent(cbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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

    private void cbProductsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbProductsActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbProductsActionPerformed

    private void cbAddressesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbAddressesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbAddressesActionPerformed

    private void rbNotDeliveryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbNotDeliveryActionPerformed
        Customer customer = (Customer) customersModel.getSelectedItem();
        addressesModel.removeAllElements();
        List<Address> addressesFiltered
            = customer.getAddresses().stream()
                .filter(
                    (address) -> address.getDepartment() == null
                )
                .toList();
        ComboBoxLoader.loadItems(addressesModel, addressesFiltered);

        lbEnvio.setVisible(false);
        lbSubtotal.setVisible(false);
        txtEnvio.setVisible(false);
        txtSubtotal.setVisible(false);

        updateTotalField();
    }//GEN-LAST:event_rbNotDeliveryActionPerformed

    private void rbIsDeliveryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbIsDeliveryActionPerformed
        Customer customer = (Customer) customersModel.getSelectedItem();
        addressesModel.removeAllElements();
        List<Address> addressesFiltered
            = customer.getAddresses().stream()
                .filter(
                    (address) -> address.getDepartment() != null
                )
                .toList();
        ComboBoxLoader.loadItems(addressesModel, addressesFiltered);

        lbEnvio.setVisible(true);
        lbSubtotal.setVisible(true);
        txtEnvio.setVisible(true);
        txtSubtotal.setVisible(true);

        updateTotalField();
    }//GEN-LAST:event_rbIsDeliveryActionPerformed

    private void cbCustomersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbCustomersActionPerformed
        Customer customer = (Customer) customersModel.getSelectedItem();
        addressesModel.removeAllElements();

        boolean isDelivery = isDelivery();
        List<Address> addressesFiltered
            = customer.getAddresses().stream()
                .filter(
                    (address)
                    -> isDelivery
                        ? address.getDepartment() != null
                        : address.getDepartment() == null
                )
                .toList();

        ComboBoxLoader.loadItems(addressesModel, addressesFiltered);
    }//GEN-LAST:event_cbCustomersActionPerformed

    private void btnaddprodActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnaddprodActionPerformed
        addDetail((Product) productsModel.getSelectedItem());
    }//GEN-LAST:event_btnaddprodActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel FooterPanel;
    private javax.swing.JPanel PanelContainer;
    private javax.swing.ButtonGroup btnGisDelivery;
    private ui.components.ActionButton btnaddprod;
    private javax.swing.JComboBox<Address> cbAddresses;
    private javax.swing.JComboBox<Customer> cbCustomers;
    private javax.swing.JComboBox<Product> cbProducts;
    private javax.swing.JComboBox<String> cbStatus;
    private javax.swing.JPanel detailPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbEnvio;
    private javax.swing.JLabel lbSubtotal;
    private javax.swing.JLabel lbSubtotal1;
    private javax.swing.JLabel lbTotal;
    private javax.swing.JRadioButton rbIsDelivery;
    private javax.swing.JRadioButton rbNotDelivery;
    private javax.swing.JScrollPane scroll;
    private javax.swing.JTable table;
    private javax.swing.JFormattedTextField txtEnvio;
    private javax.swing.JTextArea txtNotes;
    private javax.swing.JFormattedTextField txtSubtotal;
    private javax.swing.JFormattedTextField txtTotal;
    // End of variables declaration//GEN-END:variables
}
