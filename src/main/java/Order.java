import java.time.LocalDateTime;

public class Order {

    private String id;
    private LocalDateTime date;
    private String customer;
    private String itemsJson;
    private OrderStatus status;

    // Required for Jackson / frameworks
    public Order() {}

    public Order(String id, LocalDateTime date, String customer, String itemsJson) {
        this.id = id;
        this.date = date;
        this.customer = customer;
        this.itemsJson = itemsJson;
        this.status = OrderStatus.PENDING;
    }

    public String getId() { return id; }
    public LocalDateTime getDate() { return date; }
    public String getCustomer() { return customer; }
    public String getItemsJson() { return itemsJson; }
    public OrderStatus getStatus() { return status; }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
