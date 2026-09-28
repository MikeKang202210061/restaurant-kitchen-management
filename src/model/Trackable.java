package model;

// Interface for tracking status of orders and dishes
public interface Trackable {
    String getStatus();
    void updateStatus(String newStatus);
    String getTrackingInfo();
}
