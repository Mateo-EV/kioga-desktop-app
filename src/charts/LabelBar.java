/* Decompiler 897ms, total 1163ms, lines 73 */
package charts;

import com.formdev.flatlaf.util.ColorFunctions;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D.Double;
import javax.swing.JLabel;
import raven.chart.utils.ChartUtils;

public class LabelBar extends JLabel {

    private final HorizontalBarChart barChart;
    private final LabelBar.LabelType type;
    private final float percent;

    public LabelBar(HorizontalBarChart barChart, LabelBar.LabelType type,
        float percent) {
        this.barChart = barChart;
        this.type = type;
        this.percent = percent;
    }

    private Color getBarChartColor() {
        return this.barChart.getBarColor();
    }

    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        ChartUtils.registerRenderingHin(g2);
        Shape shape = this.initShape(g2);
        g2.fill(shape);
        g2.dispose();
        super.paintComponent(g);
    }

    private Shape initShape(Graphics2D g2) {
        int width = this.getWidth();
        int height = this.getHeight();
        int arc = Math.min(width, height);
        float animate = this.barChart.animator.isRunning() ? this.barChart.animator.getAnimate() : 1.0F;
        Color gradientColor = ColorFunctions.lighten(this.getBarChartColor(),
            0.05F);
        Double shape;
        double v;
        if (this.getComponentOrientation().isLeftToRight()) {
            if (this.type == LabelBar.LabelType.HORIZONTAL) {
                shape = new Double(0.0D, 0.0D,
                    (double) ((float) width * this.percent * animate),
                    (double) height, (double) arc, (double) arc);
                g2.setPaint(new GradientPaint(0.0F, 0.0F,
                    this.getBarChartColor(), (float) width, 0.0F, gradientColor));
            } else {
                v = (double) ((float) height * this.percent);
                shape = new Double(0.0D, (double) height - v, (double) width, v,
                    (double) arc, (double) arc);
                g2.setPaint(new GradientPaint(0.0F, (float) height,
                    this.getBarChartColor(), 0.0F, 0.0F, gradientColor));
            }
        } else if (this.type == LabelBar.LabelType.HORIZONTAL) {
            float size = (float) width * this.percent * animate;
            float x = (float) width - size;
            shape = new Double((double) x, 0.0D, (double) size, (double) height,
                (double) arc, (double) arc);
            g2.setPaint(new GradientPaint(0.0F, 0.0F, gradientColor,
                (float) width, 0.0F, this.getBarChartColor()));
        } else {
            v = (double) ((float) height * this.percent);
            shape = new Double(0.0D, (double) height - v, (double) width, v,
                (double) arc, (double) arc);
            g2.setPaint(new GradientPaint(0.0F, (float) height, gradientColor,
                0.0F, 0.0F, this.getBarChartColor()));
        }

        return shape;
    }

    public static enum LabelType {
        HORIZONTAL,
        VERTICAL;
    }
}
