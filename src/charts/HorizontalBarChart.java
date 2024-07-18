/* Decompiler 1867ms, total 2110ms, lines 173 */
package charts;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.image.BufferedImage;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import raven.chart.data.pie.DefaultPieDataset;
import raven.chart.simple.SimpleDataBarChart;
import raven.chart.utils.ChartAnimator;

public class HorizontalBarChart extends JPanel {

    protected NumberFormat valuesFormat = null;
    private DefaultPieDataset<String> dataset = new SimpleDataBarChart();
    private Color barColor = new Color(40, 139, 78);
    protected ChartAnimator animator;
    protected JLayeredPane layeredPane;
    private HorizontalBarChart.PanelRender panelRender;
    private JPanel panelHeader;
    private JPanel panelFooter;
    private JLabel labelNoData;

    public HorizontalBarChart() {
        this.init();
    }

    public HorizontalBarChart(boolean format) {
        this.init();
        if (format) {
            valuesFormat = NumberFormat.getCurrencyInstance(
                new Locale("es", "PE"));
        }
    }

    private void init() {
        this.initAnimator();
        this.layeredPane = new JLayeredPane();
        this.layeredPane.setLayout(new MigLayout("wrap 1,fill", "fill",
            "[grow 0][fill][grow 0]"));
        this.setLayout(new BorderLayout());
        this.add(this.layeredPane);
        this.panelRender = new HorizontalBarChart.PanelRender();
        this.panelHeader = new JPanel(new BorderLayout());
        this.panelFooter = new JPanel(new BorderLayout());
        this.panelRender.putClientProperty("FlatLaf.style", "background:null");
        this.panelHeader.putClientProperty("FlatLaf.style", "background:null");
        this.panelFooter.putClientProperty("FlatLaf.style", "background:null");
        this.labelNoData = new JLabel("Empty Data", new FlatSVGIcon(
            "com/raven/chart/empty.svg"), 0);
        this.labelNoData.setHorizontalTextPosition(0);
        this.labelNoData.setVerticalTextPosition(3);
        this.layeredPane.add(this.panelHeader);
        this.layeredPane.add(this.panelRender);
        this.layeredPane.add(this.panelFooter);
        this.updateDataset();
    }

    private void initAnimator() {
        this.animator = new ChartAnimator() {
            public BufferedImage createImage(BufferedImage image, float animate) {
                return null;
            }

            public void animatorChanged(float animator) {
                HorizontalBarChart.this.repaint();
            }
        };
    }

    public DefaultPieDataset<String> getDataset() {
        return this.dataset;
    }

    public void setDataset(DefaultPieDataset<String> dataset) {
        this.dataset = dataset;
        this.updateDataset();
    }

    public void startAnimation() {
        this.animator.start();
    }

    private void updateDataset() {
        this.panelRender.removeAll();
        this.panelRender.revalidate();
        int count = this.dataset.getItemCount();
        if (count > 0) {
            this.noData(false);
            double maxValue = this.dataset.getMaxValues().doubleValue();

            for (int i = 0; i < count; ++i) {
                double value = this.dataset.getValue(i).doubleValue();
                float percent = (float) (value / maxValue);
                this.panelRender.addItem((String) this.dataset.getKey(i), value,
                    percent);
            }
        } else {
            this.noData(true);
        }

        this.panelRender.repaint();
        this.panelRender.revalidate();
    }

    private void noData(boolean noData) {
        if (noData) {
            this.layeredPane.remove(this.labelNoData);
            this.layeredPane.add(this.labelNoData, 0);
        } else {
            this.layeredPane.remove(this.labelNoData);
        }

    }

    public void setHeader(Component component) {
        this.panelHeader.removeAll();
        this.panelHeader.add(component);
        this.panelHeader.revalidate();
        this.panelHeader.repaint();
    }

    public void setFooter(Component component) {
        this.panelFooter.removeAll();
        this.panelFooter.add(component);
        this.panelFooter.revalidate();
        this.panelFooter.repaint();
    }

    public Color getBarColor() {
        return this.barColor;
    }

    public void setBarColor(Color barColor) {
        this.barColor = barColor;
    }

    private class PanelRender extends JPanel {

        private List<HorizontalBarChart.PanelRender.Item> items;

        public PanelRender() {
            this.init();
        }

        private void init() {
            this.items = new ArrayList();
            this.setLayout(new MigLayout("wrap 3,fill",
                "[grow 0][fill,100::400]50[grow 0,trailing]"));
        }

        public void addItem(String key, double value, float Percent) {
            HorizontalBarChart.PanelRender.Item item = new HorizontalBarChart.PanelRender.Item(
                new JLabel(key), new LabelBar(HorizontalBarChart.this,
                    LabelBar.LabelType.HORIZONTAL, Percent), new JLabel(
                    valuesFormat != null ? valuesFormat.format(value) : String.valueOf(
                        (int) value)));
            this.items.add(item);
            this.add(item.label);
            this.add(item.bar, "height 8");
            this.add(item.value);
        }

        public void removeAll() {
            super.removeAll();
            this.items.clear();
        }

        private class Item {

            protected JLabel label;
            protected LabelBar bar;
            protected JLabel value;

            public Item(JLabel label, LabelBar bar, JLabel value) {
                this.label = label;
                this.bar = bar;
                this.value = value;
            }
        }
    }
}
