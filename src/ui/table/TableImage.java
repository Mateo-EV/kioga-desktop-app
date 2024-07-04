/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package ui.table;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import utils.GlobalCacheState;

/**
 *
 * @author intel
 */
public class TableImage extends javax.swing.JPanel {

    /**
     * Creates new form TableImage
     *
     * @param url
     */
    public TableImage(String url) {
        initComponents();
        pic.setText("loading");
        Map<String, ImageIcon> imageCache = GlobalCacheState.getImages();
        if (imageCache.containsKey(url)) {
            pic.setIcon(imageCache.get(url));
        } else {
            try {
                URL imageUrl = new URL(url);
                BufferedImage image = ImageIO.read(imageUrl);
                ImageIcon icon = new ImageIcon(resizeImage(image, 60, 60));
                imageCache.put(url, icon);
                pic.setIcon(icon);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        pic.setText("");
//        ImageLoader.loadImage(imageUrl, pic);
    }

    static public BufferedImage resizeImage(BufferedImage originalImage,
        int width,
        int height) {
        Image resizedImage = originalImage.getScaledInstance(width, height,
            Image.SCALE_SMOOTH);
        BufferedImage bufferedResizedImage = new BufferedImage(width, height,
            BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bufferedResizedImage.createGraphics();
        g2d.drawImage(resizedImage, 0, 0, null);
        g2d.dispose();
        return bufferedResizedImage;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pic = new javax.swing.JLabel();

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(pic, javax.swing.GroupLayout.DEFAULT_SIZE, 47, Short.MAX_VALUE)
                .addGap(10, 10, 10))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(pic, javax.swing.GroupLayout.DEFAULT_SIZE, 48, Short.MAX_VALUE)
                .addGap(10, 10, 10))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel pic;
    // End of variables declaration//GEN-END:variables
}