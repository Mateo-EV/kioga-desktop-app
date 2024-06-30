package utils;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

public class ImageLoader extends SwingWorker<ImageIcon, Void> {

    private static final ConcurrentHashMap<String, ImageIcon> imageCache = new ConcurrentHashMap<>();
    private final String url;
    private final JLabel label;

    public ImageLoader(String url, JLabel label) {
        this.url = url;
        this.label = label;
    }

    @Override
    protected ImageIcon doInBackground() throws Exception {
        if (imageCache.containsKey(url)) {
            return imageCache.get(url);
        } else {
            try {
                URL imageUrl = new URL(url);
                BufferedImage image = ImageIO.read(imageUrl);
                ImageIcon icon = new ImageIcon(resizeImage(image, 60, 60));
                imageCache.put(url, icon);
                return icon;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    @Override
    protected void done() {
        try {
            final ImageIcon icon = get();
            SwingUtilities.invokeLater(() -> {
                if (icon != null) {
                    label.setIcon(icon);
                    label.setText("");
                } else {
                    label.setText("Failed to load image.");
                }
                label.revalidate();
                label.repaint();
            });
        } catch (Exception e) {
            e.printStackTrace();
            SwingUtilities.invokeLater(() -> {
                label.setText("Failed to load image.");
                label.revalidate();
                label.repaint();
            });
        }
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int width,
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

    public static void loadImage(String url, JLabel label) {
        new ImageLoader(url, label).execute();
    }
}
