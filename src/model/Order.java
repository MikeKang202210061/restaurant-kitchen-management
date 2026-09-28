package model;

import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;

// Represents a customer order containing multiple order items
public class Order implements Trackable {
    private static int nextOrderId = 1;
    private int orderId;
    private String customerName;
    private ArrayList<OrderItem> items;
    private String status; // "Pending", "In Progress", "Completed", "Cancelled"
    private String orderTime;
    private String completedTime;

    // Constructor for new order
    public Order(String customerName) {
        this.orderId = nextOrderId++;
        this.customerName = customerName;
        this.items = new ArrayList<OrderItem>();
        this.status = "Pending";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.orderTime = sdf.format(new Date());
        this.completedTime = "";
    }

    // Constructor for loading from file
    public Order(int orderId, String customerName, String status, String orderTime, String completedTime) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.items = new ArrayList<OrderItem>();
        this.status = status;
        this.orderTime = orderTime;
        this.completedTime = completedTime;
        if (orderId >= nextOrderId) {
            nextOrderId = orderId + 1;
        }
    }

    // Add an item to the order
    public void addItem(OrderItem item) {
        items.add(item);
    }

    // Remove an item from the order by index
    public void removeItem(int index) {
        if (index >= 0 && index < items.size()) {
            items.remove(index);
        }
    }

    // Calculate total price of the order
    public double getTotal() {
        double total = 0;
        for (int i = 0; i < items.size(); i++) {
            total = total + items.get(i).getSubtotal();
        }
        return total;
    }

    // Calculate total estimated prep time
    public int getTotalPrepTime() {
        int maxTime = 0;
        for (int i = 0; i < items.size(); i++) {
            int t = items.get(i).getMenuItem().getPrepTime() * items.get(i).getQuantity();
            if (t > maxTime) {
                maxTime = t;
            }
        }
        return maxTime;
    }

    // Check if all items are completed
    public boolean allItemsCompleted() {
        for (int i = 0; i < items.size(); i++) {
            if (!items.get(i).getStatus().equals("Completed")) {
                return false;
            }
        }
        return items.size() > 0;
    }

    // Implement Trackable interface
    @Override
    public String getStatus() {
        return status;
    }

    @Override
    public void updateStatus(String newStatus) {
        this.status = newStatus;
        if (newStatus.equals("Completed")) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            this.completedTime = sdf.format(new Date());
        }
    }

    @Override
    public String getTrackingInfo() {
        return "Order #" + orderId + " for " + customerName + " [" + status + "] - $" + String.format("%.2f", getTotal());
    }

    // Getters and setters
    public int getOrderId() { return orderId; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public ArrayList<OrderItem> getItems() { return items; }
    public String getOrderTime() { return orderTime; }
    public String getCompletedTime() { return completedTime; }
    public void setCompletedTime(String completedTime) { this.completedTime = completedTime; }

    public static void setNextOrderId(int id) { nextOrderId = id; }
    public static int getNextOrderId() { return nextOrderId; }

    @Override
    public String toString() {
        return "Order #" + orderId + " | " + customerName + " | " + status + " | $" + String.format("%.2f", getTotal()) + " | " + orderTime;
    }
}
