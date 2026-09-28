package model;

// Main course dish, extends MenuItem
public class MainCourse extends MenuItem {
    private String cuisineType; // like Chinese, Italian, etc.

    public MainCourse(String name, String description, double price, int prepTime, String cuisineType) {
        super(name, description, price, prepTime);
        this.cuisineType = cuisineType;
    }

    public MainCourse(int id, String name, String description, double price, int prepTime, String cuisineType) {
        super(id, name, description, price, prepTime);
        this.cuisineType = cuisineType;
    }

    @Override
    public String getCategory() {
        return "Main Course";
    }

    public String getCuisineType() { return cuisineType; }
    public void setCuisineType(String cuisineType) { this.cuisineType = cuisineType; }
}
