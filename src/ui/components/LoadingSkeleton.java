package ui.components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.AlphaComposite;
import javax.swing.JPanel;
import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.interpolation.PropertySetter;

public class LoadingSkeleton extends JPanel {
    private static final int PULSE_DURATION = 1000; // Duración del pulso en milisegundos
    private float pulseAlpha = 0.05f; // Transparencia inicial (20%)
    private final int borderRadius = 20; // Radio del borde redondeado

    private Animator animator;

    public LoadingSkeleton() {
        initPulseAnimation();
    }

    private void initPulseAnimation() {
        animator = PropertySetter.createAnimator(
            PULSE_DURATION, this, "pulseAlpha", 0.05f, 0.2f);
        animator.setRepeatCount(Animator.INFINITE);
        animator.setRepeatBehavior(Animator.RepeatBehavior.REVERSE);
    }

    public void startLoading() {
        animator.start();
        revalidate();
        repaint();
    }

    public void stopLoading() {
        animator.stop();
        pulseAlpha = 0.05f; // Reiniciar la opacidad a 20%
        revalidate();
        repaint();
    }

    public void setPulseAlpha(float pulseAlpha) {
        this.pulseAlpha = pulseAlpha;
        revalidate();
        repaint();
    }

    public float getPulseAlpha() {
        return pulseAlpha;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, pulseAlpha));
        Color pulseColor = new Color(211, 211, 211);
        g2d.setColor(pulseColor);
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), borderRadius, borderRadius); // Dibujar rectángulo redondeado
        g2d.dispose();
    }
}
