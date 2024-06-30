/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package views.dialog;

import com.formdev.flatlaf.FlatClientProperties;
import controllers.OrderController;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.util.Enumeration;
import java.util.List;
import javax.swing.AbstractButton;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import models.Address;
import models.Customer;
import models.Product;
import net.miginfocom.swing.MigLayout;
import raven.popup.GlassPanePopup;
import raven.toast.Notifications;
import ui.components.ActionButton;
import ui.components.LoadingButton;
import ui.components.LoadingSkeleton;
import ui.table.ImageTableRenderer;
import ui.table.TableHeaderAlignment;
import utils.ApiClient;
import utils.ComboBoxLoader;

/**
 *
 * @author intel
 */
public class CreateOrderForm extends javax.swing.JPanel {

    static public DefaultComboBoxModel<Customer> customersModel = new DefaultComboBoxModel();
    static public DefaultComboBoxModel<Product> productsModel = new DefaultComboBoxModel();

    private final DefaultComboBoxModel<Address> addressesModel = new DefaultComboBoxModel();

    public CreateOrderForm() {
        initComponents();
        initcbModels();
        initTableModel();
        init();
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
            + "cellFocusColor:$TableHeader.hoverBackground"
            + "selectionBackground:$TableHeader.hoverBackground"
        );

        table.getTableHeader().setDefaultRenderer(
            new TableHeaderAlignment(table, 0));

        table.getColumnModel().getColumn(2).setCellRenderer(
            new ImageTableRenderer(table));
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
            OrderController.instance.getDataToSave(new ApiClient.onResponse() {
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
    }

    public void addDetail(Product product) {
        DefaultTableModel tableModel = (DefaultTableModel) table.getModel();

        tableModel.addRow(new Object[]{
            product.getId(),
            product.getName(),
            product.getImage(),
            product.getPriceDiscountedFormatted()
        });
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
        jTextArea1 = new javax.swing.JTextArea();
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
        jLabel4 = new javax.swing.JLabel();
        btnaddprod = new ui.components.ActionButton();

        setLayout(new java.awt.CardLayout());

        cbCustomers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbCustomersActionPerformed(evt);
            }
        });

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

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
                "#", "NOMBRE", "IMAGEN", "PRECIO"
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

        jLabel4.setText("Notas");

        javax.swing.GroupLayout detailPanelLayout = new javax.swing.GroupLayout(detailPanel);
        detailPanel.setLayout(detailPanelLayout);
        detailPanelLayout.setHorizontalGroup(
            detailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scroll, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        detailPanelLayout.setVerticalGroup(
            detailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(detailPanelLayout.createSequentialGroup()
                .addComponent(scroll, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        btnaddprod.setText("+");
        btnaddprod.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnaddprodActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PanelContainerLayout = new javax.swing.GroupLayout(PanelContainer);
        PanelContainer.setLayout(PanelContainerLayout);
        PanelContainerLayout.setHorizontalGroup(
            PanelContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelContainerLayout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addGroup(PanelContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(PanelContainerLayout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbCustomers, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(PanelContainerLayout.createSequentialGroup()
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbProducts, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnaddprod, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, PanelContainerLayout.createSequentialGroup()
                        .addComponent(rbIsDelivery)
                        .addGap(18, 18, 18)
                        .addComponent(rbNotDelivery))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(detailPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, PanelContainerLayout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbAddresses, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(FooterPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 9, Short.MAX_VALUE)
                .addComponent(FooterPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10))
        );

        add(PanelContainer, "form-loaded");
    }// </editor-fold>//GEN-END:initComponents

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

    private void cbAddressesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbAddressesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbAddressesActionPerformed

    private void cbProductsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbProductsActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbProductsActionPerformed

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
    }//GEN-LAST:event_rbIsDeliveryActionPerformed

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
    }//GEN-LAST:event_rbNotDeliveryActionPerformed

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
    private javax.swing.JPanel detailPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JRadioButton rbIsDelivery;
    private javax.swing.JRadioButton rbNotDelivery;
    private javax.swing.JScrollPane scroll;
    private javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables
}
