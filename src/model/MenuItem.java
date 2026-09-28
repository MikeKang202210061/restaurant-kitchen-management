package model;

// Abstract class for all menu items
public abstract class MenuItem {
    private static int nextId = 1;
    private int id;
    private String name;
    private String description;
    private double price;
    private int prepTime; // preparation time in minutes

    // Constructor for creating new item (auto id)
    public MenuItem(String name, String description, double price, int prepTime) {
        this.id = nextId++;
        this.name = name;
        this.description = description;
        this.price = price;
        this.prepTime = prepTime;
    }

    // Constructor for loading from file (with id)
    public MenuItem(int id, String name, String description, double price, int prepTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.prepTime = prepTime;
        if (id >= nextId) {
            nextId = id + 1;
        }
    }

    // Abstract method - each subclass returns its own category
    public abstract String getCategory();

    // Method overloading - show basic info
    public String getInfo() {
        return "[" + getCategory() + "] " + name + " - $" + String.format("%.2f", price);
    }

    // Method overloading - show detailed info
    public String getInfo(boolean detailed) {
        if (detailed) {
            return "[" + getCategory() + "] " + name + " - " + description
                    + " | Price: $" + String.format("%.2f", price) + " | Prep: " + prepTime + " min";
        }
        return getInfo();
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public int getPrepTime() { return prepTime; }
    public void setPrepTime(int prepTime) { this.prepTime = prepTime; }

    public static void setNextId(int id) { nextId = id; }
    public static int getNextId() { return nextId; }

    @Override
    public String toString() {
        return "ID:" + id + " " + getInfo(true);
    }
}
