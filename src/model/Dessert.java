package model;

// Dessert item, extends MenuItem
public class Dessert extends MenuItem {
    private boolean containsNuts;

    public Dessert(String name, String description, double price, int prepTime, boolean containsNuts) {
        super(name, description, price, prepTime);
        this.containsNuts = containsNuts;
    }

    public Dessert(int id, String name, String description, double price, int prepTime, boolean containsNuts) {
        super(id, name, description, price, prepTime);
        this.containsNuts = containsNuts;
    }

    @Override
    public String getCategory() {
        return "Dessert";
    }

    public boolean isContainsNuts() { return containsNuts; }
    public void setContainsNuts(boolean containsNuts) { this.containsNuts = containsNuts; }
}
