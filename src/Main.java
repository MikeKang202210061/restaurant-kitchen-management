import gui.MainWindow;
import javax.swing.SwingUtilities;

// Main entry point for the Restaurant Kitchen Order Management System
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                MainWindow window = new MainWindow();
                window.setVisible(true);
            }
        });
    }
}
