package models;

public enum OrderStatus {
    PENDING ("Pendiente"),
    WAITING ("En Espera"),
    ENVIADO ("Enviado"),
    ENTREGADO ("Entregado"),
    CANCELADO ("Cancelado");
    
    private final String status;

    OrderStatus(String status) {
        this.status = status;
    }

    public String getValue() {
        return status;
    }

    public static OrderStatus fromString(String status) {
        for (OrderStatus orderStatus : OrderStatus.values()) {
            if (orderStatus.getValue().equalsIgnoreCase(status)) {
                return orderStatus;
            }
        }
        throw new IllegalArgumentException("Unknown status: " + status);
    }
}
