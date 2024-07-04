/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package views.dialog;

import controllers.CategoryController;
import java.awt.BorderLayout;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import models.Category;
import raven.popup.GlassPanePopup;
import raven.toast.Notifications;
import ui.components.ActionButton;
import ui.components.LoadingButton;
import utils.ApiClient;
import utils.ServiceWorker;

/**
 *
 * @author intel
 */
public class DeleteCategoryForm extends javax.swing.JPanel {

    private final List<Category> categoriesToDel;

    public DeleteCategoryForm(List<Category> categoriesToDel) {
        initComponents();
        this.categoriesToDel = categoriesToDel;
        init();
    }

    private void init() {
        LoadingButton deleteBtn = new LoadingButton("Eliminar",
            ActionButton.DESTRUCTIVE);
        ActionButton cancelBtn = new ActionButton("Cancelar",
            ActionButton.SECONDARY);
        FooterPanel.add(deleteBtn, BorderLayout.EAST);
        FooterPanel.add(cancelBtn, BorderLayout.WEST);

        cancelBtn.addActionListener((e) -> {
            GlassPanePopup.closePopupLast();
        });
        deleteBtn.addActionListener((e) -> {
            deleteBtn.startLoading();
            ServiceWorker.execute(() -> {
                CountDownLatch latch = new CountDownLatch(
                    categoriesToDel.size());
                for (Category category : categoriesToDel) {
                    CategoryController.getInstance().delete(category.getId(),
                        new ApiClient.onResponse() {
                        @Override
                        public void onSuccess(ApiClient.ApiResponse apiResponse) {
                            Notifications.getInstance().show(
                                Notifications.Type.SUCCESS,
                                "Categoría eliminada correctamente");
                            latch.countDown();
                        }

                        @Override
                        public void onError(ApiClient.ApiResponse apiResponse) {
                            Notifications.getInstance().show(
                                Notifications.Type.ERROR,
                                "Error al eliminar la categoría");
                            latch.countDown();
                        }
                    });
                }
                try {
                    latch.await();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                deleteBtn.stopLoading();
                GlassPanePopup.closePopupLast();
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
        FooterPanel = new javax.swing.JPanel();

        FooterPanel.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout PanelContainerLayout = new javax.swing.GroupLayout(PanelContainer);
        PanelContainer.setLayout(PanelContainerLayout);
        PanelContainerLayout.setHorizontalGroup(
            PanelContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelContainerLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(FooterPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 353, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25))
        );
        PanelContainerLayout.setVerticalGroup(
            PanelContainerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelContainerLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(FooterPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(PanelContainer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(PanelContainer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel FooterPanel;
    private javax.swing.JPanel PanelContainer;
    private javax.swing.ButtonGroup btnGisDelivery;
    // End of variables declaration//GEN-END:variables
}
