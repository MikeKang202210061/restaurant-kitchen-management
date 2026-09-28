package model;

import java.util.ArrayList;
import exception.InvalidOrderException;
import exception.MenuItemNotFoundException;

// Main controller class that manages all orders, menu items, and chefs
public class Kitchen {
    private ArrayList<MenuItem> menuItems;
    private ArrayList<Order> orders;
    private ArrayList<Chef> chefs;

    public Kitchen() {
        menuItems = new ArrayList<MenuItem>();
        orders = new ArrayList<Order>();
        chefs = new ArrayList<Chef>();
    }

    // ---- Menu Item CRUD ----

    // Add a new menu item
    public void addMenuItem(MenuItem item) {
        menuItems.add(item);
    }

    // Find menu item by id
    public MenuItem findMenuItemById(int id) throws MenuItemNotFoundException {
        for (int i = 0; i < menuItems.size(); i++) {
            if (menuItems.get(i).getId() == id) {
                return menuItems.get(i);
            }
        }
        throw new MenuItemNotFoundException("Menu item with ID " + id + " not found.");
    }

    // Update a menu item
    public void updateMenuItem(int id, String name, String description, double price, int prepTime)
            throws MenuItemNotFoundException {
        MenuItem item = findMenuItemById(id);
        item.setName(name);
        item.setDescription(description);
        item.setPrice(price);
        item.setPrepTime(prepTime);
    }

    // Delete a menu item by id
    public void deleteMenuItem(int id) throws MenuItemNotFoundException {
        MenuItem item = findMenuItemById(id);
        menuItems.remove(item);
    }

    // Search menu items by name
    public ArrayList<MenuItem> searchMenuByName(String keyword) {
        ArrayList<MenuItem> results = new ArrayList<MenuItem>();
        for (int i = 0; i < menuItems.size(); i++) {
            if (menuItems.get(i).getName().toLowerCase().contains(keyword.toLowerCase())) {
                results.add(menuItems.get(i));
            }
        }
        return results;
    }

    // Sort menu items by price (simple bubble sort)
    public ArrayList<MenuItem> getMenuSortedByPrice() {
        ArrayList<MenuItem> sorted = new ArrayList<MenuItem>(menuItems);
        for (int i = 0; i < sorted.size() - 1; i++) {
            for (int j = 0; j < sorted.size() - 1 - i; j++) {
                if (sorted.get(j).getPrice() > sorted.get(j + 1).getPrice()) {
                    MenuItem temp = sorted.get(j);
                    sorted.set(j, sorted.get(j + 1));
                    sorted.set(j + 1, temp);
                }
            }
        }
        return sorted;
    }

    // ---- Order CRUD ----

    // Create a new order
    public Order createOrder(String customerName) throws InvalidOrderException {
        if (customerName == null || customerName.trim().isEmpty()) {
            throw new InvalidOrderException("Customer name cannot be empty.");
        }
        Order order = new Order(customerName);
        orders.add(order);
        return order;
    }

    // Add item to an existing order
    public void addItemToOrder(int orderId, int menuItemId, int quantity, String notes)
            throws InvalidOrderException, MenuItemNotFoundException {
        Order order = findOrderById(orderId);
        if (order.getStatus().equals("Completed") || order.getStatus().equals("Cancelled")) {
            throw new InvalidOrderException("Cannot add items to a completed or cancelled order.");
        }
        if (quantity <= 0) {
            throw new InvalidOrderException("Quantity must be greater than 0.");
        }
        MenuItem menuItem = findMenuItemById(menuItemId);
        OrderItem orderItem = new OrderItem(menuItem, quantity, notes);
        order.addItem(orderItem);
    }

    // Find order by id
    public Order findOrderById(int orderId) throws InvalidOrderException {
        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i).getOrderId() == orderId) {
                return orders.get(i);
            }
        }
        throw new InvalidOrderException("Order #" + orderId + " not found.");
    }

    // Update order item status
    public void updateOrderItemStatus(int orderId, int itemIndex, String newStatus)
            throws InvalidOrderException {
        Order order = findOrderById(orderId);
        if (itemIndex < 0 || itemIndex >= order.getItems().size()) {
            throw new InvalidOrderException("Invalid item index.");
        }
        order.getItems().get(itemIndex).updateStatus(newStatus);

        // If all items completed, mark order as completed
        if (order.allItemsCompleted()) {
            order.updateStatus("Completed");
        }
    }

    // Update order status
    public void updateOrderStatus(int orderId, String newStatus) throws InvalidOrderException {
        Order order = findOrderById(orderId);
        order.updateStatus(newStatus);
    }

    // Delete (cancel) an order
    public void cancelOrder(int orderId) throws InvalidOrderException {
        Order order = findOrderById(orderId);
        order.updateStatus("Cancelled");
    }

    // Add an order directly (for loading from file)
    public void addOrder(Order order) {
        orders.add(order);
    }

    // ---- Chef CRUD ----

    // Add a chef
    public void addChef(Chef chef) {
        chefs.add(chef);
    }

    // Find chef by id
    public Chef findChefById(int chefId) {
        for (int i = 0; i < chefs.size(); i++) {
            if (chefs.get(i).getChefId() == chefId) {
                return chefs.get(i);
            }
        }
        return null;
    }

    // Assign an order to a chef
    public void assignOrderToChef(int orderId, int chefId) throws InvalidOrderException {
        Order order = findOrderById(orderId);
        Chef chef = findChefById(chefId);
        if (chef == null) {
            throw new InvalidOrderException("Chef #" + chefId + " not found.");
        }
        chef.assignOrder(orderId);
        if (order.getStatus().equals("Pending")) {
            order.updateStatus("In Progress");
        }
    }

    // Delete a chef
    public void deleteChef(int chefId) {
        Chef chef = findChefById(chefId);
        if (chef != null) {
            chefs.remove(chef);
        }
    }

    // ---- Getters ----
    public ArrayList<MenuItem> getMenuItems() { return menuItems; }
    public ArrayList<Order> getOrders() { return orders; }
    public ArrayList<Chef> getChefs() { return chefs; }

    // Get orders by status
    public ArrayList<Order> getOrdersByStatus(String status) {
        ArrayList<Order> result = new ArrayList<Order>();
        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i).getStatus().equals(status)) {
                result.add(orders.get(i));
            }
        }
        return result;
    }
}
