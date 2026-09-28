package model;

// Beverage item, extends MenuItem
public class Beverage extends MenuItem {
    private boolean isCold;

    public Beverage(String name, String description, double price, int prepTime, boolean isCold) {
        super(name, description, price, prepTime);
        this.isCold = isCold;
    }

    public Beverage(int id, String name, String description, double price, int prepTime, boolean isCold) {
        super(id, name, description, price, prepTime);
        this.isCold = isCold;
    }

    @Override
    public String getCategory() {
        return "Beverage";
    }

    // Override getInfo to show if cold or hot
    @Override
    public String getInfo(boolean detailed) {
        String temp = "Hot";
        if (isCold) {
            temp = "Cold";
        }
        if (detailed) {
            return "[" + getCategory() + "-" + temp + "] " + getName() + " - " + getDescription()
                    + " | Price: $" + String.format("%.2f", getPrice()) + " | Prep: " + getPrepTime() + " min";
        }
        return "[" + getCategory() + "-" + temp + "] " + getName() + " - $" + String.format("%.2f", getPrice());
    }

    public boolean isCold() { return isCold; }
    public void setCold(boolean cold) { isCold = cold; }
}
