package controllers;

import java.util.List;
import java.util.Map;
import utils.ApiClient;
import utils.BackgroundSwingWorker;

public class ReportController {

    static private ReportController instance = new ReportController();

    static public ReportController getInstance() {
        return instance;
    }

    private List<Map<String, String>> productIncome = null;

    private List<Map<String, String>> productSales = null;

    private List<Map<String, Object>> productPopularity = null;

    private List<Map<String, String>> dailySales = null;

    private List<Map<String, String>> monthlySales = null;

    private List<Map<String, Object>> ordersByStatus = null;

    public List<Map<String, String>> getProductIncome() {
        return productIncome;
    }

    public List<Map<String, String>> getProductSales() {
        return productSales;
    }

    public List<Map<String, Object>> getProductPopularity() {
        return productPopularity;
    }

    public List<Map<String, String>> getDailySales() {
        return dailySales;
    }

    public List<Map<String, String>> getMonthlySales() {
        return monthlySales;
    }

    public List<Map<String, Object>> getOrdersByStatus() {
        return ordersByStatus;
    }

    public void getData(ApiClient.onResponse onResponse) {
        new BackgroundSwingWorker(
            "/admin/dashboard",
            "GET",
            null,
            new ApiClient.onResponse() {
            @Override
            public void onSuccess(ApiClient.ApiResponse apiResponse) {
                Map<String, Object> data = (Map<String, Object>) apiResponse.getData();
                productIncome = (List<Map<String, String>>) data.get(
                    "product_income");
                productSales = (List<Map<String, String>>) data.get(
                    "product_sales");
                productPopularity = (List<Map<String, Object>>) data.get(
                    "product_popularity");
                dailySales = (List<Map<String, String>>) data.get(
                    "daily_sales");
                monthlySales = (List<Map<String, String>>) data.get(
                    "monthly_sales");
                ordersByStatus = (List<Map<String, Object>>) data.get(
                    "orders_by_status");

                onResponse.onSuccess(new ApiClient.ApiResponse(
                    ApiClient.ResponseType.SUCCESS,
                    null,
                    null
                ));
            }

            @Override
            public void onError(ApiClient.ApiResponse apiResponse) {
                onResponse.onError(apiResponse);
            }
        },
            false
        ).execute();
    }
}
