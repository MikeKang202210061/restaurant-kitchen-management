package model;

import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;

// Sales analytics class - handles revenue reports and dish statistics
public class SalesAnalytics {
    private ArrayList<Order> orders;

    public SalesAnalytics(ArrayList<Order> orders) {
        this.orders = orders;
    }

    // Get total revenue for today
    public double getTodayRevenue() {
        double total = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String today = sdf.format(new Date());

        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);
            if (order.getStatus().equals("Completed") && order.getOrderTime().startsWith(today)) {
                total = total + order.getTotal();
            }
        }
        return total;
    }

    // Get total revenue for a specific month (format: "yyyy-MM")
    public double getMonthlyRevenue(String yearMonth) {
        double total = 0;
        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);
            if (order.getStatus().equals("Completed") && order.getOrderTime().startsWith(yearMonth)) {
                total = total + order.getTotal();
            }
        }
        return total;
    }

    // Get total number of completed orders
    public int getTotalCompletedOrders() {
        int count = 0;
        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i).getStatus().equals("Completed")) {
                count++;
            }
        }
        return count;
    }

    // Get how many times each dish was sold (returns parallel arrays for names and counts)
    public ArrayList<String> getDishNames() {
        ArrayList<String> names = new ArrayList<String>();
        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);
            if (order.getStatus().equals("Completed")) {
                for (int j = 0; j < order.getItems().size(); j++) {
                    String dishName = order.getItems().get(j).getMenuItem().getName();
                    if (!names.contains(dishName)) {
                        names.add(dishName);
                    }
                }
            }
        }
        return names;
    }

    public ArrayList<Integer> getDishSoldCounts() {
        ArrayList<String> names = getDishNames();
        ArrayList<Integer> counts = new ArrayList<Integer>();

        for (int i = 0; i < names.size(); i++) {
            counts.add(0);
        }

        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);
            if (order.getStatus().equals("Completed")) {
                for (int j = 0; j < order.getItems().size(); j++) {
                    String dishName = order.getItems().get(j).getMenuItem().getName();
                    int qty = order.getItems().get(j).getQuantity();
                    int index = names.indexOf(dishName);
                    if (index >= 0) {
                        counts.set(index, counts.get(index) + qty);
                    }
                }
            }
        }
        return counts;
    }

    // Get sales report for a date range
    // startDate and endDate format: "yyyy-MM-dd"
    public String getDateRangeReport(String startDate, String endDate) {
        double totalRevenue = 0;
        int orderCount = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);
            if (!order.getStatus().equals("Completed")) {
                continue;
            }
            try {
                String orderDateStr = order.getOrderTime().substring(0, 10);
                Date orderDate = sdf.parse(orderDateStr);
                Date start = sdf.parse(startDate);
                Date end = sdf.parse(endDate);

                if (!orderDate.before(start) && !orderDate.after(end)) {
                    totalRevenue = totalRevenue + order.getTotal();
                    orderCount++;
                }
            } catch (ParseException e) {
                // skip orders with bad date format
            }
        }

        String report = "=== Sales Report ===\n";
        report = report + "Period: " + startDate + " to " + endDate + "\n";
        report = report + "Total Orders: " + orderCount + "\n";
        report = report + "Total Revenue: $" + String.format("%.2f", totalRevenue) + "\n";

        if (orderCount > 0) {
            report = report + "Average Order: $" + String.format("%.2f", totalRevenue / orderCount) + "\n";
        }

        // Add dish breakdown
        report = report + "\n--- Dish Breakdown ---\n";
        ArrayList<String> names = getDishNames();
        ArrayList<Integer> counts = getDishSoldCounts();
        for (int i = 0; i < names.size(); i++) {
            report = report + names.get(i) + ": " + counts.get(i) + " sold\n";
        }

        return report;
    }

    // Get monthly breakdown for a year
    public String getMonthlyBreakdown(String year) {
        String report = "=== Monthly Breakdown for " + year + " ===\n";
        String[] months = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};

        for (int i = 0; i < months.length; i++) {
            String yearMonth = year + "-" + months[i];
            double revenue = getMonthlyRevenue(yearMonth);
            report = report + yearMonth + ": $" + String.format("%.2f", revenue) + "\n";
        }
        return report;
    }
}
