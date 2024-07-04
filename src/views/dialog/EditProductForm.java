package views.dialog;

import controllers.ProductController;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import jnafilechooser.api.JnaFileChooser;
import models.Brand;
import models.Category;
import models.Identifiable;
import models.Product;
import models.Subcategory;
import net.miginfocom.swing.MigLayout;
import raven.popup.GlassPanePopup;
import raven.toast.Notifications;
import ui.components.ActionButton;
import ui.components.LoadingButton;
import ui.components.LoadingSkeleton;
import ui.table.TableImage;
import utils.ApiClient;
import utils.ComboBoxLoader;
import utils.GlobalCacheState;
import utils.ServiceWorker;
import utils.structure.ArbolBinario;

/**
 *
 * @author intel
 */
public class EditProductForm extends javax.swing.JPanel {

    private final DefaultComboBoxModel<Category> categoriesModel = new DefaultComboBoxModel();
    private final DefaultComboBoxModel<Brand> brandsModel = new DefaultComboBoxModel();
    static private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(
        new Locale("es", "PE"));

    private final DefaultComboBoxModel<Subcategory> subcategoriesModel = new DefaultComboBoxModel();

    private final Product product = new Product();
    private Product productLoaded = new Product();

    private final JPanel loadingPanel = new JPanel();

    private final LoadingSkeleton skeletons[] = {
        new LoadingSkeleton(),
        new LoadingSkeleton(),
        new LoadingSkeleton(),
        new LoadingSkeleton(),
        new LoadingSkeleton(),};

    public EditProductForm(int productId) {
        initComponents();
        initcbModels();
        productLoaded.setId(productId);
        init();
    }

    public void selectItemById(JComboBox comboBox, int id) {
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            Identifiable item = (Identifiable) comboBox.getItemAt(i);
            if (item.getId() == id) {
                comboBox.setSelectedIndex(i);
                break;
            }
        }
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

    private void updateDiscountedPrice() {
        try {
            String text = txtPrice.getText();
            if (text != null && !text.isEmpty()) {
                double originalPrice = Double.parseDouble(text);
                double discountRate = ((double) slDiscount.getValue()) / 100;
                double discountedPrice = originalPrice * (1 - discountRate);
                txtPriceDiscounted.setValue(discountedPrice);
            } else {
                txtPriceDiscounted.setValue(null);
            }
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            txtPriceDiscounted.setValue(null);
        }
    }

    private void loadImage() {
        Map<String, ImageIcon> imageCache = GlobalCacheState.getImages();
        if (imageCache.containsKey(productLoaded.getImage())) {
            pic.setImage(imageCache.get(productLoaded.getImage()));
        } else {
            try {
                URL imageUrl = new URL(productLoaded.getImage());
                BufferedImage image = ImageIO.read(imageUrl);
                ImageIcon icon = new ImageIcon(TableImage.resizeImage(image, 60,
                    60));
                imageCache.put(productLoaded.getImage(), icon);
                pic.setImage(icon);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void init() {

        initLayoutForLoading();
        showLoadingState();

        txtPrice.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateDiscountedPrice();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateDiscountedPrice();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateDiscountedPrice();
            }
        });

        ServiceWorker.execute(() -> {
            CountDownLatch latch = new CountDownLatch(2);
            ProductController.getInstance().getDataToSave(
                new ApiClient.onResponse() {
                @Override
                public void onSuccess(ApiClient.ApiResponse apiResponse) {
                    Object responses[] = (Object[]) apiResponse.getData();
                    ArbolBinario<Category> categories = (ArbolBinario<Category>) responses[0];
                    ArbolBinario<Brand> brands = (ArbolBinario<Brand>) responses[1];

                    ComboBoxLoader.loadItems(categoriesModel, categories);
                    ComboBoxLoader.loadItems(brandsModel, brands);
                    latch.countDown();
                }

                @Override
                public void onError(ApiClient.ApiResponse apiResponse) {
                    GlassPanePopup.closePopupLast();
                    latch.countDown();
                    Notifications.getInstance().show(
                        Notifications.Type.ERROR,
                        apiResponse.getMessage()
                    );
                }
            });

            ProductController.getInstance().findById(productLoaded.getId(),
                new ApiClient.onResponse() {
                @Override
                public void onSuccess(ApiClient.ApiResponse apiResponse) {
                    productLoaded = (Product) apiResponse.getData();
                    txtName.setText(productLoaded.getName());
                    txtPrice.setText(String.valueOf(productLoaded.getPrice()));
                    slDiscount.setValue(
                        (int) (productLoaded.getDiscount() * 100));
                    txtDiscount.setValue(productLoaded.getDiscount());
                    txtPriceDiscounted.setValue(
                        productLoaded.getPriceDiscounted());
                    txtStock.setValue(productLoaded.getStock());
                    txtDescription.setText(productLoaded.getDescription());

                    loadImage();
                    latch.countDown();
                }

                @Override
                public void onError(ApiClient.ApiResponse apiResponse) {
                    latch.countDown();
                    GlassPanePopup.closePopupLast();
                    Notifications.getInstance().show(
                        Notifications.Type.ERROR,
                        apiResponse.getMessage()
                    );
                }
            });

            try {
                latch.await();
                stopLoadingState();
                selectItemById(cbBrands, productLoaded.getBrand().getId());
                selectItemById(cbCategories, productLoaded.getBrand().getId());
            } catch (InterruptedException ex) {
                GlassPanePopup.closePopupLast();
                Notifications.getInstance().show(
                    Notifications.Type.ERROR,
                    "Algo malo ocurrió"
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
            double price;

            try {
                price = Double.parseDouble(txtPrice.getText());
            } catch (NumberFormatException ex) {
                Notifications.getInstance().show(Notifications.Type.ERROR,
                    "Ingresa el precio correctamente");
                return;
            }

            int stock;

            try {
                Number number = (Number) txtStock.getValue();
                stock = number.intValue();
            } catch (Exception ex) {
                Notifications.getInstance().show(Notifications.Type.ERROR,
                    "Ingresa el stock correctamente");
                return;
            }

            saveBtn.startLoading();
            product.setId(productLoaded.getId());
            product.setName(txtName.getText());
            product.setBrand((Brand) brandsModel.getSelectedItem());
            product.setCategory((Category) categoriesModel.getSelectedItem());
            product.setDescription(txtDescription.getText());
            product.setStock(stock);
            if (subcategoriesModel.getSelectedItem() != null) {
                product.setSubcategory(
                    (Subcategory) subcategoriesModel.getSelectedItem());
            }

            product.setIsActive(checkBoxIsActive.isSelected());
            product.setPrice(price);
            product.setDiscount(((double) slDiscount.getValue()) / 100);

            ProductController.getInstance().update(product,
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
        txtPrice = new javax.swing.JTextField();
        checkBoxIsActive = new javax.swing.JCheckBox();

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

        txtDiscount.setEditable(false);
        txtDiscount.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0%"))));
        txtDiscount.setEnabled(false);

        lbSubcategory.setText("Subcategoria");

        jLabel9.setText("Stock");

        txtStock.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));

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
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnAddImage, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnDeleteImage, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(pic, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(21, Short.MAX_VALUE))
        );

        checkBoxIsActive.setSelected(true);
        checkBoxIsActive.setText("Activo");

        javax.swing.GroupLayout PanelContainerLayout = new javax.swing.GroupLayout(PanelContainer);
        PanelContainer.setLayout(PanelContainerLayout);
        PanelContainerLayout.setHorizontalGroup(
            PanelContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelContainerLayout.createSequentialGroup()
                .addGroup(PanelContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelContainerLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(FooterPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 453, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(PanelContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(PanelContainerLayout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 342, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, PanelContainerLayout.createSequentialGroup()
                            .addGap(50, 50, 50)
                            .addGroup(PanelContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(PanelContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(checkBoxIsActive)
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
                                                    .addComponent(cbSubcategories, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))))))))
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
                .addGap(7, 7, 7)
                .addComponent(checkBoxIsActive)
                .addGap(10, 10, 10)
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

            if (productLoaded.getSubcategory() != null) {
                selectItemById(cbSubcategories, product.getSubcategory().getId());
            }
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
        loadImage();
        product.setImageFile(null);
    }//GEN-LAST:event_btnDeleteImageActionPerformed

    private void slDiscountStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_slDiscountStateChanged
        double discount = ((double) slDiscount.getValue()) / 100;
        txtDiscount.setValue(discount);

        updateDiscountedPrice();

    }//GEN-LAST:event_slDiscountStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel FooterPanel;
    private javax.swing.JPanel PanelContainer;
    private ui.components.ActionButton btnAddImage;
    private ui.components.ActionButton btnDeleteImage;
    private javax.swing.ButtonGroup btnGisDelivery;
    private javax.swing.JComboBox<Brand> cbBrands;
    private javax.swing.JComboBox<Category> cbCategories;
    private javax.swing.JComboBox<Subcategory> cbSubcategories;
    private javax.swing.JCheckBox checkBoxIsActive;
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
    private javax.swing.JTextField txtPrice;
    private javax.swing.JFormattedTextField txtPriceDiscounted;
    private javax.swing.JFormattedTextField txtStock;
    // End of variables declaration//GEN-END:variables
}
