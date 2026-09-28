package gui;

import model.*;
import exception.*;
import model.MenuItem;
import util.DataManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

// Main GUI window for the Restaurant Kitchen Order Management System
public class MainWindow_EN extends JFrame {
    private Kitchen kitchen;
    private SalesAnalytics analytics;

    // Tab panels
    private JTabbedPane tabbedPane;

    // Menu tab components
    private JTable menuTable;
    private DefaultTableModel menuTableModel;
    private JTextField menuNameField, menuDescField, menuPriceField, menuPrepField, menuExtraField;
    private JComboBox<String> menuTypeCombo;
    private JTextField menuSearchField;

    // Order tab components
    private JTable orderTable;
    private DefaultTableModel orderTableModel;
    private JTextField orderCustomerField, orderMenuIdField, orderQtyField, orderNotesField;
    private JTextArea orderDetailArea;

    // Chef tab components
    private JTable chefTable;
    private DefaultTableModel chefTableModel;
    private JTextField chefNameField;
    private JComboBox<String> chefSpecialtyCombo;

    // Analytics tab components
    private JTextArea analyticsArea;
    private JTextField analyticsStartDate, analyticsEndDate, analyticsYear;

    // Status bar
    private JLabel statusLabel;

    public void MainWindow() {
        kitchen = new Kitchen();
        loadData();
        analytics = new SalesAnalytics(kitchen.getOrders());

        setTitle("Restaurant Kitchen Order Management System");
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);

        // Save data when closing
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveData();
                System.exit(0);
            }
        });

        // Build the UI
        buildUI();
        refreshAllTables();
    }

    // Build the main UI with tabs
    private void buildUI() {
        setLayout(new BorderLayout());

        tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Menu Items", buildMenuPanel());
        tabbedPane.addTab("Orders", buildOrderPanel());
        tabbedPane.addTab("Chefs", buildChefPanel());
        tabbedPane.addTab("Sales Analytics", buildAnalyticsPanel());

        add(tabbedPane, BorderLayout.CENTER);

        // Status bar at bottom
        statusLabel = new JLabel(" Ready");
        statusLabel.setBorder(BorderFactory.createEtchedBorder());
        add(statusLabel, BorderLayout.SOUTH);
    }

    // ==================== MENU PANEL ====================
    private JPanel buildMenuPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Table
        String[] columns = {"ID", "Type", "Name", "Description", "Price", "Prep Time", "Extra"};
        menuTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        menuTable = new JTable(menuTableModel);
        JScrollPane scrollPane = new JScrollPane(menuTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Input panel
        JPanel inputPanel = new JPanel(new GridLayout(5, 4, 5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Add / Edit Menu Item"));

        inputPanel.add(new JLabel("Type:"));
        menuTypeCombo = new JComboBox<String>(new String[]{"Main Course", "Beverage", "Dessert"});
        inputPanel.add(menuTypeCombo);

        inputPanel.add(new JLabel("Name:"));
        menuNameField = new JTextField();
        inputPanel.add(menuNameField);

        inputPanel.add(new JLabel("Description:"));
        menuDescField = new JTextField();
        inputPanel.add(menuDescField);

        inputPanel.add(new JLabel("Price:"));
        menuPriceField = new JTextField();
        inputPanel.add(menuPriceField);

        inputPanel.add(new JLabel("Prep Time (min):"));
        menuPrepField = new JTextField();
        inputPanel.add(menuPrepField);

        inputPanel.add(new JLabel("Extra (cuisine/cold/nuts):"));
        menuExtraField = new JTextField();
        inputPanel.add(menuExtraField);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton addBtn = new JButton("Add");
        addBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { addMenuItem(); }
        });
        buttonPanel.add(addBtn);

        JButton updateBtn = new JButton("Update");
        updateBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { updateMenuItem(); }
        });
        buttonPanel.add(updateBtn);

        JButton deleteBtn = new JButton("Delete");
        deleteBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { deleteMenuItem(); }
        });
        buttonPanel.add(deleteBtn);

        JButton sortBtn = new JButton("Sort by Price");
        sortBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { sortMenuByPrice(); }
        });
        buttonPanel.add(sortBtn);

        // Search
        inputPanel.add(new JLabel("Search:"));
        menuSearchField = new JTextField();
        inputPanel.add(menuSearchField);

        JButton searchBtn = new JButton("Search");
        searchBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { searchMenu(); }
        });
        buttonPanel.add(searchBtn);

        JButton showAllBtn = new JButton("Show All");
        showAllBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { refreshMenuTable(); }
        });
        buttonPanel.add(showAllBtn);

        inputPanel.add(new JLabel(""));
        inputPanel.add(new JLabel(""));

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(inputPanel, BorderLayout.CENTER);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);
        panel.add(southPanel, BorderLayout.SOUTH);

        return panel;
    }

    // ==================== ORDER PANEL ====================
    private JPanel buildOrderPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Split: left for order list, right for order details
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(480);

        // Left - order table
        JPanel leftPanel = new JPanel(new BorderLayout());
        String[] columns = {"Order ID", "Customer", "Status", "Total", "Time"};
        orderTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        orderTable = new JTable(orderTableModel);
        orderTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                showOrderDetails();
            }
        });
        leftPanel.add(new JScrollPane(orderTable), BorderLayout.CENTER);

        // Input for creating orders
        JPanel orderInputPanel = new JPanel(new GridLayout(3, 4, 5, 5));
        orderInputPanel.setBorder(BorderFactory.createTitledBorder("Order Operations"));

        orderInputPanel.add(new JLabel("Customer:"));
        orderCustomerField = new JTextField();
        orderInputPanel.add(orderCustomerField);

        orderInputPanel.add(new JLabel("Menu Item ID:"));
        orderMenuIdField = new JTextField();
        orderInputPanel.add(orderMenuIdField);

        orderInputPanel.add(new JLabel("Quantity:"));
        orderQtyField = new JTextField();
        orderInputPanel.add(orderQtyField);

        orderInputPanel.add(new JLabel("Notes:"));
        orderNotesField = new JTextField();
        orderInputPanel.add(orderNotesField);

        // Order buttons
        JPanel orderBtnPanel = new JPanel(new FlowLayout());

        JButton createOrderBtn = new JButton("Create Order");
        createOrderBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { createOrder(); }
        });
        orderBtnPanel.add(createOrderBtn);

        JButton addItemBtn = new JButton("Add Item to Order");
        addItemBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { addItemToOrder(); }
        });
        orderBtnPanel.add(addItemBtn);

        JButton cancelBtn = new JButton("Cancel Order");
        cancelBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { cancelOrder(); }
        });
        orderBtnPanel.add(cancelBtn);

        orderInputPanel.add(new JLabel(""));
        orderInputPanel.add(new JLabel(""));
        orderInputPanel.add(new JLabel(""));
        orderInputPanel.add(new JLabel(""));

        JPanel leftSouth = new JPanel(new BorderLayout());
        leftSouth.add(orderInputPanel, BorderLayout.CENTER);
        leftSouth.add(orderBtnPanel, BorderLayout.SOUTH);
        leftPanel.add(leftSouth, BorderLayout.SOUTH);

        splitPane.setLeftComponent(leftPanel);

        // Right - order details and status update
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Order Details"));
        orderDetailArea = new JTextArea();
        orderDetailArea.setEditable(false);
        orderDetailArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        rightPanel.add(new JScrollPane(orderDetailArea), BorderLayout.CENTER);

        // Status update buttons
        JPanel statusBtnPanel = new JPanel(new FlowLayout());

        JButton inProgressBtn = new JButton("Mark Item In Progress");
        inProgressBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { updateItemStatus("In Progress"); }
        });
        statusBtnPanel.add(inProgressBtn);

        JButton completedBtn = new JButton("Mark Item Completed");
        completedBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { updateItemStatus("Completed"); }
        });
        statusBtnPanel.add(completedBtn);

        rightPanel.add(statusBtnPanel, BorderLayout.SOUTH);
        splitPane.setRightComponent(rightPanel);

        panel.add(splitPane, BorderLayout.CENTER);
        return panel;
    }

    // ==================== CHEF PANEL ====================
    private JPanel buildChefPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columns = {"Chef ID", "Name", "Specialty", "Assigned Orders"};
        chefTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        chefTable = new JTable(chefTableModel);
        panel.add(new JScrollPane(chefTable), BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new GridLayout(2, 4, 5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Chef Management"));

        inputPanel.add(new JLabel("Name:"));
        chefNameField = new JTextField();
        inputPanel.add(chefNameField);

        inputPanel.add(new JLabel("Specialty:"));
        chefSpecialtyCombo = new JComboBox<String>(new String[]{"Main Course", "Beverage", "Dessert", "All"});
        inputPanel.add(chefSpecialtyCombo);

        inputPanel.add(new JLabel(""));
        inputPanel.add(new JLabel(""));
        inputPanel.add(new JLabel(""));
        inputPanel.add(new JLabel(""));

        JPanel btnPanel = new JPanel(new FlowLayout());

        JButton addChefBtn = new JButton("Add Chef");
        addChefBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { addChef(); }
        });
        btnPanel.add(addChefBtn);

        JButton deleteChefBtn = new JButton("Delete Chef");
        deleteChefBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { deleteChef(); }
        });
        btnPanel.add(deleteChefBtn);

        JButton assignBtn = new JButton("Assign Order to Chef");
        assignBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { assignOrderToChef(); }
        });
        btnPanel.add(assignBtn);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(inputPanel, BorderLayout.CENTER);
        southPanel.add(btnPanel, BorderLayout.SOUTH);
        panel.add(southPanel, BorderLayout.SOUTH);

        return panel;
    }

    // ==================== ANALYTICS PANEL ====================
    private JPanel buildAnalyticsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        analyticsArea = new JTextArea();
        analyticsArea.setEditable(false);
        analyticsArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        panel.add(new JScrollPane(analyticsArea), BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Reports"));

        JButton todayBtn = new JButton("Today's Revenue");
        todayBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { showTodayRevenue(); }
        });
        inputPanel.add(todayBtn);

        inputPanel.add(new JLabel("Year:"));
        analyticsYear = new JTextField(6);
        inputPanel.add(analyticsYear);

        JButton monthlyBtn = new JButton("Monthly Breakdown");
        monthlyBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { showMonthlyBreakdown(); }
        });
        inputPanel.add(monthlyBtn);

        inputPanel.add(new JLabel("Start (yyyy-MM-dd):"));
        analyticsStartDate = new JTextField(10);
        inputPanel.add(analyticsStartDate);

        inputPanel.add(new JLabel("End:"));
        analyticsEndDate = new JTextField(10);
        inputPanel.add(analyticsEndDate);

        JButton rangeBtn = new JButton("Date Range Report");
        rangeBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { showDateRangeReport(); }
        });
        inputPanel.add(rangeBtn);

        JButton dishStatsBtn = new JButton("Dish Statistics");
        dishStatsBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { showDishStatistics(); }
        });
        inputPanel.add(dishStatsBtn);

        panel.add(inputPanel, BorderLayout.SOUTH);
        return panel;
    }

    // ==================== MENU ACTIONS ====================
    private void addMenuItem() {
        try {
            String type = (String) menuTypeCombo.getSelectedItem();
            String name = menuNameField.getText().trim();
            String desc = menuDescField.getText().trim();
            double price = Double.parseDouble(menuPriceField.getText().trim());
            int prepTime = Integer.parseInt(menuPrepField.getText().trim());
            String extra = menuExtraField.getText().trim();

            if (name.isEmpty()) {
                showError("Name cannot be empty.");
                return;
            }
            if (price <= 0) {
                showError("Price must be greater than 0.");
                return;
            }

            MenuItem item = null;
            if (type.equals("Main Course")) {
                if (extra.isEmpty()) extra = "General";
                item = new MainCourse(name, desc, price, prepTime, extra);
            } else if (type.equals("Beverage")) {
                boolean cold = extra.toLowerCase().equals("true") || extra.toLowerCase().equals("cold");
                item = new Beverage(name, desc, price, prepTime, cold);
            } else if (type.equals("Dessert")) {
                boolean nuts = extra.toLowerCase().equals("true") || extra.toLowerCase().equals("yes");
                item = new Dessert(name, desc, price, prepTime, nuts);
            }

            kitchen.addMenuItem(item);
            refreshMenuTable();
            clearMenuFields();
            setStatus("Menu item added: " + name);
        } catch (NumberFormatException ex) {
            showError("Please enter valid numbers for price and prep time.");
        }
    }

    private void updateMenuItem() {
        int row = menuTable.getSelectedRow();
        if (row < 0) {
            showError("Please select a menu item to update.");
            return;
        }

        try {
            int id = Integer.parseInt(menuTableModel.getValueAt(row, 0).toString());
            String name = menuNameField.getText().trim();
            String desc = menuDescField.getText().trim();
            double price = Double.parseDouble(menuPriceField.getText().trim());
            int prepTime = Integer.parseInt(menuPrepField.getText().trim());

            if (name.isEmpty()) {
                showError("Name cannot be empty.");
                return;
            }

            kitchen.updateMenuItem(id, name, desc, price, prepTime);
            refreshMenuTable();
            setStatus("Menu item updated: " + name);
        } catch (NumberFormatException ex) {
            showError("Please enter valid numbers.");
        } catch (MenuItemNotFoundException ex) {
            showError(ex.getMessage());
        }
    }

    private void deleteMenuItem() {
        int row = menuTable.getSelectedRow();
        if (row < 0) {
            showError("Please select a menu item to delete.");
            return;
        }

        try {
            int id = Integer.parseInt(menuTableModel.getValueAt(row, 0).toString());
            int confirm = JOptionPane.showConfirmDialog(this, "Delete this menu item?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                kitchen.deleteMenuItem(id);
                refreshMenuTable();
                setStatus("Menu item deleted.");
            }
        } catch (MenuItemNotFoundException ex) {
            showError(ex.getMessage());
        }
    }

    private void sortMenuByPrice() {
        ArrayList<MenuItem> sorted = kitchen.getMenuSortedByPrice();
        menuTableModel.setRowCount(0);
        for (int i = 0; i < sorted.size(); i++) {
            addMenuItemToTable(sorted.get(i));
        }
        setStatus("Menu sorted by price.");
    }

    private void searchMenu() {
        String keyword = menuSearchField.getText().trim();
        if (keyword.isEmpty()) {
            refreshMenuTable();
            return;
        }
        ArrayList<MenuItem> results = kitchen.searchMenuByName(keyword);
        menuTableModel.setRowCount(0);
        for (int i = 0; i < results.size(); i++) {
            addMenuItemToTable(results.get(i));
        }
        setStatus("Found " + results.size() + " results for '" + keyword + "'");
    }

    // ==================== ORDER ACTIONS ====================
    private void createOrder() {
        String customer = orderCustomerField.getText().trim();
        try {
            Order order = kitchen.createOrder(customer);
            refreshOrderTable();
            setStatus("Order #" + order.getOrderId() + " created for " + customer);
        } catch (InvalidOrderException ex) {
            showError(ex.getMessage());
        }
    }

    private void addItemToOrder() {
        int row = orderTable.getSelectedRow();
        if (row < 0) {
            showError("Please select an order first.");
            return;
        }

        try {
            int orderId = Integer.parseInt(orderTableModel.getValueAt(row, 0).toString());
            int menuItemId = Integer.parseInt(orderMenuIdField.getText().trim());
            int qty = Integer.parseInt(orderQtyField.getText().trim());
            String notes = orderNotesField.getText().trim();

            kitchen.addItemToOrder(orderId, menuItemId, qty, notes);
            refreshOrderTable();
            showOrderDetails();
            setStatus("Item added to Order #" + orderId);
        } catch (NumberFormatException ex) {
            showError("Please enter valid numbers for menu item ID and quantity.");
        } catch (InvalidOrderException ex) {
            showError(ex.getMessage());
        } catch (MenuItemNotFoundException ex) {
            showError(ex.getMessage());
        }
    }

    private void cancelOrder() {
        int row = orderTable.getSelectedRow();
        if (row < 0) {
            showError("Please select an order to cancel.");
            return;
        }

        try {
            int orderId = Integer.parseInt(orderTableModel.getValueAt(row, 0).toString());
            kitchen.cancelOrder(orderId);
            refreshOrderTable();
            setStatus("Order #" + orderId + " cancelled.");
        } catch (InvalidOrderException ex) {
            showError(ex.getMessage());
        }
    }

    private void showOrderDetails() {
        int row = orderTable.getSelectedRow();
        if (row < 0) {
            orderDetailArea.setText("");
            return;
        }

        try {
            int orderId = Integer.parseInt(orderTableModel.getValueAt(row, 0).toString());
            Order order = kitchen.findOrderById(orderId);

            String details = "Order #" + order.getOrderId() + "\n";
            details = details + "Customer: " + order.getCustomerName() + "\n";
            details = details + "Status: " + order.getStatus() + "\n";
            details = details + "Order Time: " + order.getOrderTime() + "\n";
            if (!order.getCompletedTime().isEmpty()) {
                details = details + "Completed: " + order.getCompletedTime() + "\n";
            }
            details = details + "Est. Prep Time: " + order.getTotalPrepTime() + " min\n";
            details = details + "\n--- Items ---\n";

            for (int i = 0; i < order.getItems().size(); i++) {
                OrderItem item = order.getItems().get(i);
                details = details + (i) + ". " + item.toString() + "\n";
                if (!item.getNotes().isEmpty()) {
                    details = details + "   Notes: " + item.getNotes() + "\n";
                }
            }
            details = details + "\nTotal: $" + String.format("%.2f", order.getTotal()) + "\n";

            orderDetailArea.setText(details);
        } catch (InvalidOrderException ex) {
            orderDetailArea.setText("Error: " + ex.getMessage());
        }
    }

    private void updateItemStatus(String newStatus) {
        int row = orderTable.getSelectedRow();
        if (row < 0) {
            showError("Please select an order first.");
            return;
        }

        String input = JOptionPane.showInputDialog(this, "Enter item index (shown in details):");
        if (input == null || input.trim().isEmpty()) return;

        try {
            int orderId = Integer.parseInt(orderTableModel.getValueAt(row, 0).toString());
            int itemIndex = Integer.parseInt(input.trim());
            kitchen.updateOrderItemStatus(orderId, itemIndex, newStatus);
            refreshOrderTable();
            showOrderDetails();
            setStatus("Item status updated to " + newStatus);
        } catch (NumberFormatException ex) {
            showError("Please enter a valid item index.");
        } catch (InvalidOrderException ex) {
            showError(ex.getMessage());
        }
    }

    // ==================== CHEF ACTIONS ====================
    private void addChef() {
        String name = chefNameField.getText().trim();
        if (name.isEmpty()) {
            showError("Chef name cannot be empty.");
            return;
        }
        String specialty = (String) chefSpecialtyCombo.getSelectedItem();
        Chef chef = new Chef(name, specialty);
        kitchen.addChef(chef);
        refreshChefTable();
        chefNameField.setText("");
        setStatus("Chef added: " + name);
    }

    private void deleteChef() {
        int row = chefTable.getSelectedRow();
        if (row < 0) {
            showError("Please select a chef to delete.");
            return;
        }
        int chefId = Integer.parseInt(chefTableModel.getValueAt(row, 0).toString());
        kitchen.deleteChef(chefId);
        refreshChefTable();
        setStatus("Chef deleted.");
    }

    private void assignOrderToChef() {
        int row = chefTable.getSelectedRow();
        if (row < 0) {
            showError("Please select a chef first.");
            return;
        }

        String input = JOptionPane.showInputDialog(this, "Enter Order ID to assign:");
        if (input == null || input.trim().isEmpty()) return;

        try {
            int chefId = Integer.parseInt(chefTableModel.getValueAt(row, 0).toString());
            int orderId = Integer.parseInt(input.trim());
            kitchen.assignOrderToChef(orderId, chefId);
            refreshChefTable();
            refreshOrderTable();
            setStatus("Order #" + orderId + " assigned to chef.");
        } catch (NumberFormatException ex) {
            showError("Please enter a valid order ID.");
        } catch (InvalidOrderException ex) {
            showError(ex.getMessage());
        }
    }

    // ==================== ANALYTICS ACTIONS ====================
    private void showTodayRevenue() {
        double revenue = analytics.getTodayRevenue();
        int completed = analytics.getTotalCompletedOrders();
        analyticsArea.setText("=== Today's Revenue ===\n");
        analyticsArea.append("Total Revenue: $" + String.format("%.2f", revenue) + "\n");
        analyticsArea.append("Total Completed Orders (all time): " + completed + "\n");
    }

    private void showMonthlyBreakdown() {
        String year = analyticsYear.getText().trim();
        if (year.isEmpty()) {
            showError("Please enter a year (e.g. 2026).");
            return;
        }
        String report = analytics.getMonthlyBreakdown(year);
        analyticsArea.setText(report);
    }

    private void showDateRangeReport() {
        String start = analyticsStartDate.getText().trim();
        String end = analyticsEndDate.getText().trim();
        if (start.isEmpty() || end.isEmpty()) {
            showError("Please enter both start and end dates.");
            return;
        }
        String report = analytics.getDateRangeReport(start, end);
        analyticsArea.setText(report);
    }

    private void showDishStatistics() {
        ArrayList<String> names = analytics.getDishNames();
        ArrayList<Integer> counts = analytics.getDishSoldCounts();

        String text = "=== Dish Sales Statistics ===\n\n";
        if (names.size() == 0) {
            text = text + "No completed orders yet.\n";
        } else {
            for (int i = 0; i < names.size(); i++) {
                text = text + names.get(i) + ": " + counts.get(i) + " sold\n";
            }
        }
        analyticsArea.setText(text);
    }

    // ==================== TABLE REFRESH ====================
    private void refreshAllTables() {
        refreshMenuTable();
        refreshOrderTable();
        refreshChefTable();
    }

    private void refreshMenuTable() {
        menuTableModel.setRowCount(0);
        ArrayList<MenuItem> items = kitchen.getMenuItems();
        for (int i = 0; i < items.size(); i++) {
            addMenuItemToTable(items.get(i));
        }
    }

    private void addMenuItemToTable(MenuItem item) {
        String extra = "";
        if (item instanceof MainCourse) {
            extra = ((MainCourse) item).getCuisineType();
        } else if (item instanceof Beverage) {
            extra = ((Beverage) item).isCold() ? "Cold" : "Hot";
        } else if (item instanceof Dessert) {
            extra = ((Dessert) item).isContainsNuts() ? "Contains Nuts" : "No Nuts";
        }
        // Polymorphism: calling getCategory() on MenuItem reference
        menuTableModel.addRow(new Object[]{
                item.getId(), item.getCategory(), item.getName(),
                item.getDescription(), String.format("$%.2f", item.getPrice()),
                item.getPrepTime() + " min", extra
        });
    }

    private void refreshOrderTable() {
        orderTableModel.setRowCount(0);
        ArrayList<Order> orders = kitchen.getOrders();
        for (int i = 0; i < orders.size(); i++) {
            Order o = orders.get(i);
            orderTableModel.addRow(new Object[]{
                    o.getOrderId(), o.getCustomerName(), o.getStatus(),
                    "$" + String.format("%.2f", o.getTotal()), o.getOrderTime()
            });
        }
    }

    private void refreshChefTable() {
        chefTableModel.setRowCount(0);
        ArrayList<Chef> chefs = kitchen.getChefs();
        for (int i = 0; i < chefs.size(); i++) {
            Chef c = chefs.get(i);
            String orderIds = "";
            for (int j = 0; j < c.getAssignedOrderIds().size(); j++) {
                if (j > 0) orderIds = orderIds + ", ";
                orderIds = orderIds + c.getAssignedOrderIds().get(j);
            }
            chefTableModel.addRow(new Object[]{
                    c.getChefId(), c.getName(), c.getSpecialty(), orderIds
            });
        }
    }

    // ==================== HELPERS ====================
    private void clearMenuFields() {
        menuNameField.setText("");
        menuDescField.setText("");
        menuPriceField.setText("");
        menuPrepField.setText("");
        menuExtraField.setText("");
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
        statusLabel.setText(" Error: " + message);
    }

    private void setStatus(String message) {
        statusLabel.setText(" " + message);
    }

    // ==================== DATA I/O ====================
    private void loadData() {
        // Load menu items first
        ArrayList<MenuItem> items = DataManager.loadMenuItems();
        for (int i = 0; i < items.size(); i++) {
            kitchen.addMenuItem(items.get(i));
        }

        // Load orders (needs menu items to be loaded first)
        ArrayList<Order> orders = DataManager.loadOrders(kitchen.getMenuItems());
        for (int i = 0; i < orders.size(); i++) {
            kitchen.addOrder(orders.get(i));
        }

        // Load chefs
        ArrayList<Chef> chefs = DataManager.loadChefs();
        for (int i = 0; i < chefs.size(); i++) {
            kitchen.addChef(chefs.get(i));
        }
    }

    private void saveData() {
        DataManager.saveMenuItems(kitchen.getMenuItems());
        DataManager.saveOrders(kitchen.getOrders());
        DataManager.saveChefs(kitchen.getChefs());
    }
}
