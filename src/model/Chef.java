package model;

import java.util.ArrayList;

// Represents a chef in the kitchen
public class Chef {
    private static int nextChefId = 1;
    private int chefId;
    private String name;
    private String specialty; // like "Main Course", "Dessert", "Beverage"
    private ArrayList<Integer> assignedOrderIds; // list of order IDs assigned to this chef

    public Chef(String name, String specialty) {
        this.chefId = nextChefId++;
        this.name = name;
        this.specialty = specialty;
        this.assignedOrderIds = new ArrayList<Integer>();
    }

    public Chef(int chefId, String name, String specialty) {
        this.chefId = chefId;
        this.name = name;
        this.specialty = specialty;
        this.assignedOrderIds = new ArrayList<Integer>();
        if (chefId >= nextChefId) {
            nextChefId = chefId + 1;
        }
    }

    // Assign an order to this chef
    public void assignOrder(int orderId) {
        if (!assignedOrderIds.contains(orderId)) {
            assignedOrderIds.add(orderId);
        }
    }

    // Remove an order from this chef
    public void removeOrder(int orderId) {
        assignedOrderIds.remove(Integer.valueOf(orderId));
    }

    // Get number of current orders
    public int getOrderCount() {
        return assignedOrderIds.size();
    }

    // Getters and setters
    public int getChefId() { return chefId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }
    public ArrayList<Integer> getAssignedOrderIds() { return assignedOrderIds; }

    public static void setNextChefId(int id) { nextChefId = id; }

    @Override
    public String toString() {
        return "Chef #" + chefId + " " + name + " (" + specialty + ") - Orders: " + assignedOrderIds.size();
    }
}
