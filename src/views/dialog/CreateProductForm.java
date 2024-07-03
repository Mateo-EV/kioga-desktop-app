package views.dialog;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import controllers.ProductController;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.io.File;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import jnafilechooser.api.JnaFileChooser;
import models.Brand;
import models.Category;
import models.Product;
import models.Subcategory;
import net.miginfocom.swing.MigLayout;
import raven.popup.GlassPanePopup;
import raven.toast.Notifications;
import ui.components.ActionButton;
import ui.components.LoadingButton;
import ui.components.LoadingSkeleton;
import utils.ApiClient;
import utils.ComboBoxLoader;
import utils.GlobalCacheState;
import utils.structure.ArbolBinario;

/**
 *
 * @author intel
 */
public class CreateProductForm extends javax.swing.JPanel {

    private static final DefaultComboBoxModel<Category> categoriesModel = new DefaultComboBoxModel();
    private static final DefaultComboBoxModel<Brand> brandsModel = new DefaultComboBoxModel();
    static private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(
        new Locale("es", "PE"));

    private final DefaultComboBoxModel<Subcategory> subcategoriesModel = new DefaultComboBoxModel();

    private final Product product = new Product();

    private final JPanel loadingPanel = new JPanel();

    private final LoadingSkeleton skeletons[] = {
        new LoadingSkeleton(),
        new LoadingSkeleton(),
        new LoadingSkeleton(),
        new LoadingSkeleton(),
        new LoadingSkeleton(),};

    public CreateProductForm() {
        initComponents();
        initcbModels();
        init();
    }

    static public void syncCategories() {
        categoriesModel.removeAllElements();
        GlobalCacheState.getCategories().forEach(customer -> {
            categoriesModel.addElement(customer);
        });
    }

    static public void syncBrands() {
        brandsModel.removeAllElements();
        GlobalCacheState.getBrands().forEach(product -> {
            brandsModel.addElement(product);
        });
    }

    private void initcbModels() {
        cbCategories.setModel(categoriesModel);
        cbBrands.setModel(brandsModel);
        cbSubcategories.setModel(subcategoriesModel);
//
        cbSubcategories.setRenderer(
            (list, value, index, isSelected, cellHasFocus) -> {
                DefaultListCellRenderer renderer = new DefaultListCellRenderer();
                JLabel label = (JLabel) renderer.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    return label;
                }

                label.setText(value.getName());
                return label;
            }
        );
        cbCategories.setRenderer(
            (list, value, index, isSelected, cellHasFocus) -> {
                DefaultListCellRenderer renderer = new DefaultListCellRenderer();
                JLabel label = (JLabel) renderer.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    return label;
                }

                label.setText(value.getName());
                return label;
            }
        );
        cbBrands.setRenderer(
            (list, value, index, isSelected, cellHasFocus) -> {
                DefaultListCellRenderer renderer = new DefaultListCellRenderer();
                JLabel label = (JLabel) renderer.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    return label;
                }

                label.setText(value.getName());
                return label;
            }
        );
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
        loadingPanel.removeAll();
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

    private void init() {

        initLayoutForLoading();
        showLoadingState();

        pic.setImage(new FlatSVGIcon("resources/icon/image.svg", 5f));
        txtDiscount.setValue(0d);

        ProductController.getInstance().getDataToSave(
            new ApiClient.onResponse() {
            @Override
            public void onSuccess(ApiClient.ApiResponse apiResponse) {
                System.out.println(apiResponse.getData());
                Object responses[] = (Object[]) apiResponse.getData();
                ArbolBinario<Category> categories = (ArbolBinario<Category>) responses[0];
                ArbolBinario<Brand> brands = (ArbolBinario<Brand>) responses[1];

                ComboBoxLoader.loadItems(categoriesModel, categories);
                ComboBoxLoader.loadItems(brandsModel, brands);
                stopLoadingState();
            }

            @Override
            public void onError(ApiClient.ApiResponse apiResponse) {
                GlassPanePopup.closePopupLast();
                stopLoadingState();
                Notifications.getInstance().show(
                    Notifications.Type.ERROR,
                    apiResponse.getMessage()
                );
            }
        });

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
            product.setName(txtName.getText());
            product.setBrand((Brand) brandsModel.getSelectedItem());
            product.setCategory((Category) categoriesModel.getSelectedItem());
            product.setDescription(txtDescription.getText());
            product.setStock((int) txtStock.getValue());
            product.setSubcategory(
                (Subcategory) subcategoriesModel.getSelectedItem());
            product.setPrice((double) txtPrice.getValue());
            product.setDiscount(((double) slDiscount.getValue()) / 100);

            ProductController.getInstance().save(product,
                new ApiClient.onResponse() {
                @Override
                public void onSuccess(ApiClient.ApiResponse apiResponse) {
                    GlassPanePopup.closePopupLast();
                    Notifications.getInstance().show(
                        Notifications.Type.SUCCESS,
                        "Producto agregado correctamente"
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
        txtDescription = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        FooterPanel = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        cbCategories = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        cbBrands = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        txtPrice = new javax.swing.JFormattedTextField(currencyFormat);
        jLabel6 = new javax.swing.JLabel();
        slDiscount = new javax.swing.JSlider();
        jLabel7 = new javax.swing.JLabel();
        txtPriceDiscounted = new javax.swing.JFormattedTextField(currencyFormat);
        txtDiscount = new javax.swing.JFormattedTextField();
        lbSubcategory = new javax.swing.JLabel();
        cbSubcategories = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        txtStock = new javax.swing.JFormattedTextField();
        jLabel10 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        pic = new javaswingdev.picturebox.PictureBox();
        btnAddImage = new ui.components.ActionButton();
        btnDeleteImage = new ui.components.ActionButton(ActionButton.DESTRUCTIVE);

        setLayout(new java.awt.CardLayout());

        txtDescription.setColumns(20);
        txtDescription.setRows(5);
        jScrollPane1.setViewportView(txtDescription);

        jLabel1.setText("Nombre");

        FooterPanel.setLayout(new java.awt.BorderLayout());

        jLabel4.setText("Descripción");

        jLabel2.setText("Categoria");

        cbCategories.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbCategoriesActionPerformed(evt);
            }
        });

        jLabel3.setText("Marca");

        jLabel5.setText("Precio");

        txtPrice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPriceActionPerformed(evt);
            }
        });
        txtPrice.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPriceKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtPriceKeyTyped(evt);
            }
        });

        jLabel6.setText("Descuento");

        slDiscount.setMaximum(50);
        slDiscount.setValue(0);
        slDiscount.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                slDiscountStateChanged(evt);
            }
        });

        jLabel7.setText("Precio Descontado");

        txtPriceDiscounted.setEditable(false);
        txtPriceDiscounted.setEnabled(false);
        txtPriceDiscounted.setValue(0);

        txtDiscount.setEditable(false);
        txtDiscount.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0%"))));
        txtDiscount.setEnabled(false);

        lbSubcategory.setText("Subcategoria");

        jLabel9.setText("Stock");

        txtStock.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        txtStock.setValue(0);

        jLabel10.setText("Image");

        btnAddImage.setText("Agregar");
        btnAddImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddImageActionPerformed(evt);
            }
        });

        btnDeleteImage.setText("Eliminar");
        btnDeleteImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteImageActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(pic, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24)
                .addComponent(btnAddImage, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnDeleteImage, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAddImage, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDeleteImage, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pic, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 21, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout PanelContainerLayout = new javax.swing.GroupLayout(PanelContainer);
        PanelContainer.setLayout(PanelContainerLayout);
        PanelContainerLayout.setHorizontalGroup(
            PanelContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelContainerLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(FooterPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 453, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(50, 50, 50))
            .addGroup(PanelContainerLayout.createSequentialGroup()
                .addGroup(PanelContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(PanelContainerLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 342, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, PanelContainerLayout.createSequentialGroup()
                        .addGap(50, 50, 50)
                        .addGroup(PanelContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(PanelContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, PanelContainerLayout.createSequentialGroup()
                                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, PanelContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(PanelContainerLayout.createSequentialGroup()
                                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtStock, javax.swing.GroupLayout.PREFERRED_SIZE, 342, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(PanelContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, PanelContainerLayout.createSequentialGroup()
                                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(slDiscount, javax.swing.GroupLayout.PREFERRED_SIZE, 287, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(txtDiscount, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE))
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, PanelContainerLayout.createSequentialGroup()
                                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(txtPrice))
                                        .addGroup(PanelContainerLayout.createSequentialGroup()
                                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(cbCategories, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGroup(PanelContainerLayout.createSequentialGroup()
                                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, 341, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, PanelContainerLayout.createSequentialGroup()
                                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(cbBrands, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, PanelContainerLayout.createSequentialGroup()
                                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(txtPriceDiscounted, javax.swing.GroupLayout.PREFERRED_SIZE, 341, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, PanelContainerLayout.createSequentialGroup()
                                            .addComponent(lbSubcategory, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(cbSubcategories, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))))))
                .addGap(50, 50, 50))
        );
        PanelContainerLayout.setVerticalGroup(
            PanelContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelContainerLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(PanelContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbCategories, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbSubcategory, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbSubcategories, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbBrands, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(slDiscount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDiscount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPriceDiscounted, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtStock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(39, 39, 39)
                .addGroup(PanelContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(25, 25, 25)
                .addComponent(FooterPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25))
        );

        add(PanelContainer, "form-loaded");
    }// </editor-fold>//GEN-END:initComponents

    private void cbCategoriesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbCategoriesActionPerformed
        Category category = (Category) categoriesModel.getSelectedItem();

        subcategoriesModel.removeAllElements();

        if (category.getSubcategories().isEmpty()) {
            lbSubcategory.setVisible(false);
            cbSubcategories.setVisible(false);
        } else {
            for (Subcategory subcategory : category.getSubcategories()) {
                subcategoriesModel.addElement(subcategory);
            }
            lbSubcategory.setVisible(true);
            cbSubcategories.setVisible(true);
        }
    }//GEN-LAST:event_cbCategoriesActionPerformed

    private void btnAddImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddImageActionPerformed
        JnaFileChooser ch = new JnaFileChooser();
        ch.addFilter("Image", "png", "jpg");
        boolean act = ch.showOpenDialog(SwingUtilities.getWindowAncestor(this));
        if (act) {
            File file = ch.getSelectedFile();
            pic.setImage(new ImageIcon(file.getAbsolutePath()));
            product.setImageFile(file);
        }
    }//GEN-LAST:event_btnAddImageActionPerformed

    private void btnDeleteImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteImageActionPerformed
        pic.setImage(new FlatSVGIcon("resources/icon/image.svg", 5f));
        product.setImageFile(null);
    }//GEN-LAST:event_btnDeleteImageActionPerformed

    private void slDiscountStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_slDiscountStateChanged
        double discount = ((double) slDiscount.getValue()) / 100;
        txtDiscount.setValue(discount);

        double price = (double) txtPrice.getValue();
        txtPriceDiscounted.setValue(price * (1 - discount));

    }//GEN-LAST:event_slDiscountStateChanged

    private void txtPriceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPriceActionPerformed

    }//GEN-LAST:event_txtPriceActionPerformed

    private void txtPriceKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPriceKeyTyped

    }//GEN-LAST:event_txtPriceKeyTyped

    private void txtPriceKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPriceKeyReleased
        if (txtPrice.getValue() == null) {
            return;
        }

        double price = (double) txtPrice.getValue();
        double discount = ((double) slDiscount.getValue()) / 100;
        txtPriceDiscounted.setValue(price * (1 - discount));
    }//GEN-LAST:event_txtPriceKeyReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel FooterPanel;
    private javax.swing.JPanel PanelContainer;
    private ui.components.ActionButton btnAddImage;
    private ui.components.ActionButton btnDeleteImage;
    private javax.swing.ButtonGroup btnGisDelivery;
    private javax.swing.JComboBox<Brand> cbBrands;
    private javax.swing.JComboBox<Category> cbCategories;
    private javax.swing.JComboBox<Subcategory> cbSubcategories;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbSubcategory;
    private javaswingdev.picturebox.PictureBox pic;
    private javax.swing.JSlider slDiscount;
    private javax.swing.JTextArea txtDescription;
    private javax.swing.JFormattedTextField txtDiscount;
    private javax.swing.JTextField txtName;
    private javax.swing.JFormattedTextField txtPrice;
    private javax.swing.JFormattedTextField txtPriceDiscounted;
    private javax.swing.JFormattedTextField txtStock;
    // End of variables declaration//GEN-END:variables
}
