package model;

// Represents one item in an order, links to a MenuItem
public class OrderItem implements Trackable {
    private MenuItem menuItem;
    private int quantity;
    private String status; // "Pending", "In Progress", "Completed"
    private String notes;

    public OrderItem(MenuItem menuItem, int quantity, String notes) {
        this.menuItem = menuItem;
        this.quantity = quantity;
        this.status = "Pending";
        this.notes = notes;
    }

    public OrderItem(MenuItem menuItem, int quantity, String status, String notes) {
        this.menuItem = menuItem;
        this.quantity = quantity;
        this.status = status;
        this.notes = notes;
    }

    // Implement Trackable interface
    @Override
    public String getStatus() {
        return status;
    }

    @Override
    public void updateStatus(String newStatus) {
        this.status = newStatus;
    }

    @Override
    public String getTrackingInfo() {
        return menuItem.getName() + " x" + quantity + " [" + status + "]";
    }

    // Calculate subtotal for this item
    public double getSubtotal() {
        return menuItem.getPrice() * quantity;
    }

    // Getters and setters
    public MenuItem getMenuItem() { return menuItem; }
    public void setMenuItem(MenuItem menuItem) { this.menuItem = menuItem; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    @Override
    public String toString() {
        return menuItem.getName() + " x" + quantity + " ($" + String.format("%.2f", getSubtotal()) + ") - " + status;
    }
}
