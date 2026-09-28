package util;

import model.*;
import java.io.*;
import java.util.ArrayList;

// Handles saving and loading data to/from text files
public class DataManager {
    // Use the directory where the program is running
    private static String BASE_DIR = System.getProperty("user.dir");
    private static String MENU_FILE = BASE_DIR + File.separator + "data" + File.separator + "menu.txt";
    private static String ORDERS_FILE = BASE_DIR + File.separator + "data" + File.separator + "orders.txt";
    private static String CHEFS_FILE = BASE_DIR + File.separator + "data" + File.separator + "chefs.txt";

    // Allow setting base directory from outside (useful when running from IDE)
    public static void setBaseDir(String dir) {
        BASE_DIR = dir;
        MENU_FILE = BASE_DIR + File.separator + "data" + File.separator + "menu.txt";
        ORDERS_FILE = BASE_DIR + File.separator + "data" + File.separator + "orders.txt";
        CHEFS_FILE = BASE_DIR + File.separator + "data" + File.separator + "chefs.txt";
    }

    // Make sure data folder exists
    public static void ensureDataFolder() {
        File folder = new File(BASE_DIR + File.separator + "data");
        if (!folder.exists()) {
            folder.mkdir();
        }
    }

    // ---- Save Menu Items ----
    public static void saveMenuItems(ArrayList<MenuItem> items) {
        ensureDataFolder();
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(MENU_FILE));
            for (int i = 0; i < items.size(); i++) {
                MenuItem item = items.get(i);
                // Format: type|id|name|description|price|prepTime|extraField
                if (item instanceof MainCourse) {
                    MainCourse mc = (MainCourse) item;
                    writer.println("MainCourse|" + mc.getId() + "|" + mc.getName() + "|"
                            + mc.getDescription() + "|" + mc.getPrice() + "|" + mc.getPrepTime()
                            + "|" + mc.getCuisineType());
                } else if (item instanceof Beverage) {
                    Beverage bv = (Beverage) item;
                    writer.println("Beverage|" + bv.getId() + "|" + bv.getName() + "|"
                            + bv.getDescription() + "|" + bv.getPrice() + "|" + bv.getPrepTime()
                            + "|" + bv.isCold());
                } else if (item instanceof Dessert) {
                    Dessert ds = (Dessert) item;
                    writer.println("Dessert|" + ds.getId() + "|" + ds.getName() + "|"
                            + ds.getDescription() + "|" + ds.getPrice() + "|" + ds.getPrepTime()
                            + "|" + ds.isContainsNuts());
                }
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Error saving menu items: " + e.getMessage());
        }
    }

    // ---- Load Menu Items ----
    public static ArrayList<MenuItem> loadMenuItems() {
        ArrayList<MenuItem> items = new ArrayList<MenuItem>();
        File file = new File(MENU_FILE);
        if (!file.exists()) {
            return items;
        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader(MENU_FILE));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length < 7) continue;

                String type = parts[0];
                int id = Integer.parseInt(parts[1]);
                String name = parts[2];
                String desc = parts[3];
                double price = Double.parseDouble(parts[4]);
                int prepTime = Integer.parseInt(parts[5]);
                String extra = parts[6];

                if (type.equals("MainCourse")) {
                    items.add(new MainCourse(id, name, desc, price, prepTime, extra));
                } else if (type.equals("Beverage")) {
                    items.add(new Beverage(id, name, desc, price, prepTime, Boolean.parseBoolean(extra)));
                } else if (type.equals("Dessert")) {
                    items.add(new Dessert(id, name, desc, price, prepTime, Boolean.parseBoolean(extra)));
                }
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("Error loading menu items: " + e.getMessage());
        }
        return items;
    }

    // ---- Save Orders ----
    public static void saveOrders(ArrayList<Order> orders) {
        ensureDataFolder();
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(ORDERS_FILE));
            for (int i = 0; i < orders.size(); i++) {
                Order order = orders.get(i);
                // First line: ORDER|id|customerName|status|orderTime|completedTime
                writer.println("ORDER|" + order.getOrderId() + "|" + order.getCustomerName()
                        + "|" + order.getStatus() + "|" + order.getOrderTime() + "|" + order.getCompletedTime());

                // Then each item: ITEM|menuItemId|quantity|status|notes
                for (int j = 0; j < order.getItems().size(); j++) {
                    OrderItem oi = order.getItems().get(j);
                    writer.println("ITEM|" + oi.getMenuItem().getId() + "|" + oi.getQuantity()
                            + "|" + oi.getStatus() + "|" + oi.getNotes());
                }
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Error saving orders: " + e.getMessage());
        }
    }

    // ---- Load Orders ----
    public static ArrayList<Order> loadOrders(ArrayList<MenuItem> menuItems) {
        ArrayList<Order> orders = new ArrayList<Order>();
        File file = new File(ORDERS_FILE);
        if (!file.exists()) {
            return orders;
        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader(ORDERS_FILE));
            String line;
            Order currentOrder = null;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");

                if (parts[0].equals("ORDER") && parts.length >= 6) {
                    int id = Integer.parseInt(parts[1]);
                    String customer = parts[2];
                    String status = parts[3];
                    String orderTime = parts[4];
                    String completedTime = parts[5];
                    currentOrder = new Order(id, customer, status, orderTime, completedTime);
                    orders.add(currentOrder);
                } else if (parts[0].equals("ITEM") && parts.length >= 5 && currentOrder != null) {
                    int menuItemId = Integer.parseInt(parts[1]);
                    int quantity = Integer.parseInt(parts[2]);
                    String status = parts[3];
                    String notes = parts[4];

                    // Find the menu item by id
                    MenuItem menuItem = null;
                    for (int i = 0; i < menuItems.size(); i++) {
                        if (menuItems.get(i).getId() == menuItemId) {
                            menuItem = menuItems.get(i);
                            break;
                        }
                    }
                    if (menuItem != null) {
                        OrderItem orderItem = new OrderItem(menuItem, quantity, status, notes);
                        currentOrder.addItem(orderItem);
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("Error loading orders: " + e.getMessage());
        }
        return orders;
    }

    // ---- Save Chefs ----
    public static void saveChefs(ArrayList<Chef> chefs) {
        ensureDataFolder();
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(CHEFS_FILE));
            for (int i = 0; i < chefs.size(); i++) {
                Chef chef = chefs.get(i);
                // Format: id|name|specialty|orderId1,orderId2,...
                String orderIds = "";
                for (int j = 0; j < chef.getAssignedOrderIds().size(); j++) {
                    if (j > 0) orderIds = orderIds + ",";
                    orderIds = orderIds + chef.getAssignedOrderIds().get(j);
                }
                writer.println(chef.getChefId() + "|" + chef.getName() + "|" + chef.getSpecialty() + "|" + orderIds);
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Error saving chefs: " + e.getMessage());
        }
    }

    // ---- Load Chefs ----
    public static ArrayList<Chef> loadChefs() {
        ArrayList<Chef> chefs = new ArrayList<Chef>();
        File file = new File(CHEFS_FILE);
        if (!file.exists()) {
            return chefs;
        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader(CHEFS_FILE));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length < 3) continue;

                int id = Integer.parseInt(parts[0]);
                String name = parts[1];
                String specialty = parts[2];
                Chef chef = new Chef(id, name, specialty);

                if (parts.length >= 4 && !parts[3].isEmpty()) {
                    String[] orderIds = parts[3].split(",");
                    for (int i = 0; i < orderIds.length; i++) {
                        chef.assignOrder(Integer.parseInt(orderIds[i].trim()));
                    }
                }
                chefs.add(chef);
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("Error loading chefs: " + e.getMessage());
        }
        return chefs;
    }
}
