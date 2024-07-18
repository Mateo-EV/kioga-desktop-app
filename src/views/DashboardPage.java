package views;

import charts.HorizontalBarChart;
import com.formdev.flatlaf.FlatClientProperties;
import controllers.ReportController;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import raven.alerts.MessageAlerts;
import raven.chart.ChartLegendRenderer;
import raven.chart.data.category.DefaultCategoryDataset;
import raven.chart.data.pie.DefaultPieDataset;
import raven.chart.line.LineChart;
import raven.chart.pie.PieChart;
import ui.components.LoadingSkeleton;
import ui.components.SimpleForm;
import utils.ApiClient;
import utils.DateCalculator;

public class DashboardPage extends SimpleForm {

    public DashboardPage(boolean logged) {
        if (logged) {
            init();
        }
    }

    @Override
    public void formRefresh() {
        removeAll();
        revalidate();
        repaint();
        loadData();
    }

    @Override
    public void formInitAndOpen() {

    }

    @Override
    public void formOpen() {

    }

    public void init() {
        setLayout(new MigLayout("wrap,fillx,gap 10", "fill", "top"));
        loadData();
    }

    private void loadData() {
        showLoadingSkeleton();

        ReportController.getInstance().getData(new ApiClient.onResponse() {
            @Override
            public void onSuccess(ApiClient.ApiResponse response) {
                createPieChart();
                createLineChart();
                createBarChart();
                stopLoading();
            }

            @Override
            public void onError(ApiClient.ApiResponse response) {
                MessageAlerts.getInstance().showMessage(
                    "Error",
                    response.getMessage(),
                    MessageAlerts.MessageType.ERROR
                );
                stopLoading();
            }

        });
    }

    private final LoadingSkeleton loadingSkeletons[] = {
        new LoadingSkeleton(),
        new LoadingSkeleton(),
        new LoadingSkeleton(),
        new LoadingSkeleton(),
        new LoadingSkeleton(),
        new LoadingSkeleton(),};

    private final String positionsSkeletons[] = {
        "split 3,height 290",
        "height 290",
        "height 290",
        "height 290",
        "split 2,gap 0 20, height 290",
        "height 290"
    };

    private void showLoadingSkeleton() {
        for (int i = 0; i < loadingSkeletons.length; i++) {
            LoadingSkeleton loadingSkeleton = loadingSkeletons[i];
            add(loadingSkeleton, positionsSkeletons[i]);
            loadingSkeleton.startLoading();
        }
    }

    private void stopLoading() {
        for (LoadingSkeleton loadingSkeleton : loadingSkeletons) {
            loadingSkeleton.stopLoading();
            remove(loadingSkeleton);
        }
        revalidate();
        repaint();
    }

    private void createPieChart() {
        pieChart1 = new PieChart();
        JLabel header1 = new JLabel("Productos con más ingresos");
        header1.putClientProperty(FlatClientProperties.STYLE, ""
            + "font:+1");
        pieChart1.setHeader(header1);
        pieChart1.getChartColor().addColor(Color.decode("#f87171"),
            Color.decode("#fb923c"), Color.decode("#fbbf24"), Color.decode(
            "#a3e635"), Color.decode("#34d399"), Color.decode("#22d3ee"),
            Color.decode("#818cf8"), Color.decode("#c084fc"));
        pieChart1.putClientProperty(FlatClientProperties.STYLE, ""
            + "border:5,5,5,5,$Component.borderColor,,20");
        pieChart1.setDataset(createProductIncomeData());
        add(pieChart1, "split 3,height 290");

        pieChart2 = new PieChart();
        JLabel header2 = new JLabel("Productos más vendidos");
        header2.putClientProperty(FlatClientProperties.STYLE, ""
            + "font:+1");
        pieChart2.setHeader(header2);
        pieChart2.getChartColor().addColor(Color.decode("#f87171"),
            Color.decode("#fb923c"), Color.decode("#fbbf24"), Color.decode(
            "#a3e635"), Color.decode("#34d399"), Color.decode("#22d3ee"),
            Color.decode("#818cf8"), Color.decode("#c084fc"));
        pieChart2.putClientProperty(FlatClientProperties.STYLE, ""
            + "border:5,5,5,5,$Component.borderColor,,20");
        pieChart2.setDataset(createProductSalesData());
        add(pieChart2, "height 290");

        pieChart3 = new PieChart();
        JLabel header3 = new JLabel("Productos más populares");
        header3.putClientProperty(FlatClientProperties.STYLE, ""
            + "font:+1");
        pieChart3.setHeader(header3);
        pieChart3.getChartColor().addColor(Color.decode("#f87171"),
            Color.decode("#fb923c"), Color.decode("#fbbf24"), Color.decode(
            "#a3e635"), Color.decode("#34d399"), Color.decode("#22d3ee"),
            Color.decode("#818cf8"), Color.decode("#c084fc"));
        pieChart3.setChartType(PieChart.ChartType.DONUT_CHART);
        pieChart3.putClientProperty(FlatClientProperties.STYLE, ""
            + "border:5,5,5,5,$Component.borderColor,,20");
        pieChart3.setDataset(createProductPopularityData());
        add(pieChart3, "height 290");
    }

    private void createLineChart() {
        lineChart = new LineChart();
        lineChart.setChartType(LineChart.ChartType.CURVE);
        lineChart.putClientProperty(FlatClientProperties.STYLE, ""
            + "border:5,5,5,5,$Component.borderColor,,20");
        add(lineChart);
        createLineChartData();
    }

    private void createBarChart() {
        // BarChart 1
        barChart1 = new HorizontalBarChart(true);
        JLabel header1 = new JLabel("Ventas Mensuales");
        header1.putClientProperty(FlatClientProperties.STYLE, ""
            + "font:+1;"
            + "border:0,0,5,0");
        barChart1.setHeader(header1);
        barChart1.setBarColor(Color.decode("#f97316"));
        barChart1.setDataset(createMontlySalesData());
        JPanel panel1 = new JPanel(new BorderLayout());
        panel1.putClientProperty(FlatClientProperties.STYLE, ""
            + "border:5,5,5,5,$Component.borderColor,,20");
        panel1.add(barChart1);
        add(panel1, "split 2,gap 0 20");

        // BarChart 2
        barChart2 = new HorizontalBarChart();
        JLabel header2 = new JLabel("Pedidos por Estado");
        header2.putClientProperty(FlatClientProperties.STYLE, ""
            + "font:+1;"
            + "border:0,0,5,0");
        barChart2.setHeader(header2);
        barChart2.setBarColor(Color.decode("#10b981"));
        barChart2.setDataset(createOrdersByStatusData());
        JPanel panel2 = new JPanel(new BorderLayout());
        panel2.putClientProperty(FlatClientProperties.STYLE, ""
            + "border:5,5,5,5,$Component.borderColor,,20");
        panel2.add(barChart2);
        add(panel2);
    }

    private DefaultPieDataset createMontlySalesData() {
        DefaultPieDataset<String> dataset = new DefaultPieDataset() {

        };
        for (Map<String, String> productMap : ReportController.getInstance().getMonthlySales()) {
            String name = truncate(productMap.get("month"));
            double sales = Double.parseDouble(productMap.get("monthly_sales"));
            dataset.addValue(name, sales);
        }
        return dataset;
    }

    private DefaultPieDataset createOrdersByStatusData() {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        for (Map<String, Object> productMap : ReportController.getInstance().getOrdersByStatus()) {
            String name = truncate((String) productMap.get("status"));
            int count = ((Number) productMap.get("order_count")).intValue();
            dataset.addValue(name, count);
        }
        return dataset;
    }

    private DefaultPieDataset createProductIncomeData() {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        for (Map<String, String> productMap : ReportController.getInstance().getProductIncome()) {
            String name = truncate(productMap.get("product"));
            double income = Double.parseDouble(productMap.get("income"));
            dataset.addValue(name, income);
        }

        return dataset;
    }

    private DefaultPieDataset createProductSalesData() {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        for (Map<String, String> productMap : ReportController.getInstance().getProductSales()) {
            String name = truncate(productMap.get("product"));
            int sales = Integer.parseInt(productMap.get("sales"));
            dataset.addValue(name, sales);
        }

        return dataset;
    }

    private DefaultPieDataset createProductPopularityData() {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        for (Map<String, Object> productMap : ReportController.getInstance().getProductPopularity()) {
            String name = truncate((String) productMap.get("product"));
            int orders = ((Number) productMap.get("orders")).intValue();
            dataset.addValue(name, orders);
        }

        return dataset;
    }

    private String truncate(String text) {
        if (text == null) {
            return null;
        }
        if (text.length() <= 50) {
            return text;
        }
        return text.substring(0, 50 - 3) + "...";
    }

    private void createLineChartData() {
        List<Map<String, String>> data = ReportController.getInstance().getDailySales();

        DefaultCategoryDataset<String, String> categoryDataset = new DefaultCategoryDataset<>();
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("MMM dd, yyyy");
        for (Map<String, String> record : data) {
            try {
                Date date = inputDateFormat.parse(record.get("date"));
                String formattedDate = outputDateFormat.format(date);
                categoryDataset.addValue(
                    Double.parseDouble(record.get("income")), "income",
                    formattedDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        /**
         * Control the legend we do not show all legend
         */
        try {
            Date date = outputDateFormat.parse(categoryDataset.getColumnKey(0));
            Date dateEnd = outputDateFormat.parse(categoryDataset.getColumnKey(
                categoryDataset.getColumnCount() - 1));

            DateCalculator dcal = new DateCalculator(date, dateEnd);
            long diff = dcal.getDifferenceDays();

            double d = Math.ceil((diff / 10f));
            lineChart.setLegendRenderer(new ChartLegendRenderer() {
                @Override
                public Component getLegendComponent(Object legend, int index) {
                    if (index % d == 0) {
                        return super.getLegendComponent(legend, index);
                    } else {
                        return null;
                    }
                }
            });
        } catch (ParseException e) {
            System.err.println(e);
        }

        lineChart.setCategoryDataset(categoryDataset);
        lineChart.getChartColor().addColor(Color.decode("#38bdf8"),
            Color.decode("#fb7185"), Color.decode("#34d399"));
        JLabel header = new JLabel("Ingresos Diarios");
        header.putClientProperty(FlatClientProperties.STYLE, ""
            + "font:+1;"
            + "border:0,0,5,0");
        lineChart.setHeader(header);
    }

    private LineChart lineChart;
    private HorizontalBarChart barChart1;
    private HorizontalBarChart barChart2;
    private PieChart pieChart1;
    private PieChart pieChart2;
    private PieChart pieChart3;
}
