package utils;

import javax.swing.SwingWorker;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import utils.structure.Cola;

public class BackgroundSwingWorker extends SwingWorker<Object, Void> {
    private String[] endpoints;
    private String[] methods;
    private Map<String, Object>[] data;
    private ApiClient.onResponse callback;
    private boolean[] expectList;
    private boolean singleRequest;
    private Cola<Exception> exceptions;

    // Constructor para múltiples peticiones con claves personalizadas
    public BackgroundSwingWorker(String[] endpoints, String[] methods, Map<String, Object>[] data, ApiClient.onResponse callback, boolean[] expectList) {
        this.endpoints = endpoints;
        this.methods = methods;
        this.data = data;
        this.callback = callback;
        this.expectList = expectList;
        this.singleRequest = false;
        this.exceptions = new Cola();
    }

    public BackgroundSwingWorker(String endpoint, String method, Map<String, Object> data, ApiClient.onResponse callback, boolean expectList) {
        this.endpoints = new String[]{endpoint};
        this.methods = new String[]{method};
        this.data = new Map[]{data};
        this.callback = callback;
        this.expectList = new boolean[]{expectList};
        this.singleRequest = true;
    }

    @Override
    protected Object doInBackground() throws Exception {
        try {
            if(singleRequest) {
                if (expectList[0]) {
                    return ApiClient.sendRequestForList(endpoints[0], methods[0], data[0]);
                } else {
                    return ApiClient.sendRequest(endpoints[0], methods[0], data[0]);
                }
            }
           
            int numRequests = endpoints.length;
            CountDownLatch latch = new CountDownLatch(numRequests);
            Object[] combinedResults = new Object[numRequests];
            
            for (int i = 0; i < numRequests; i++) {
                final int index = i;
                new Thread(() -> {
                    try {
                        Object result;
                        if (expectList[index]) {
                            result = ApiClient.sendRequestForList(endpoints[index], methods[index], data[index]);
                        } else {
                            result = ApiClient.sendRequest(endpoints[index], methods[index], data[index]);
                        }
                        synchronized (combinedResults) {
                            combinedResults[index] = result;
                        }
                    } catch (Exception e) {
                        synchronized (exceptions) {
                            exceptions.enqueue(e);
                        }
                    } finally {
                        latch.countDown();
                    }
                }).start();
            }
            
            latch.await();
            
            if (!exceptions.isEmpty()) {
                throw new Exception(exceptions.dequeue());
            }
            
            return combinedResults;
            
        } catch (IOException | ApiClient.ApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void done() {
        try {
            Object response = get();
            callback.onSuccess(new ApiClient.ApiResponse(ApiClient.ResponseType.SUCCESS, "Request successful", response));
        } catch (Exception e) {
            if (e.getCause() instanceof ApiClient.ApiException) {
                ApiClient.ApiException apiException = (ApiClient.ApiException) e.getCause();
                callback.onError(new ApiClient.ApiResponse(ApiClient.ResponseType.ERROR, apiException.getErrorBody()));
            } else {
                callback.onError(new ApiClient.ApiResponse(ApiClient.ResponseType.ERROR, "Algo malo ocurrió"));
            }
            e.printStackTrace();
        }
    }
}
