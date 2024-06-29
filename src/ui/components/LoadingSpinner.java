package ui.components;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JLabel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.Timer;

public class LoadingSpinner extends JLabel {
    private Timer timer;
    private double angle = 0d;

    public LoadingSpinner() {
        FlatSVGIcon icon = new FlatSVGIcon("resources/icon/loader-circle.svg", 0.45f);
        setIcon(icon);
        
        timer = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                angle += 3.6;
                if (angle >= 360) {
                    angle = 0;
                }
                repaint();
            }
        });
    }

    public void start() {
        timer.start();
    }

    public void stop() {
        timer.stop();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        int x = (getWidth() - getIcon().getIconWidth()) / 2;
        int y = (getHeight() - getIcon().getIconHeight()) / 2;
        g2d.rotate(Math.toRadians(angle), getWidth() / 2, getHeight() / 2);
        g2d.drawImage(((ImageIcon) getIcon()).getImage(), x, y, this);
        g2d.dispose();
    }
    
}
