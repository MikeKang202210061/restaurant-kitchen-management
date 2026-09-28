package exception;

// Custom exception for when a menu item is not found
public class MenuItemNotFoundException extends Exception {
    public MenuItemNotFoundException(String message) {
        super(message);
    }
}
