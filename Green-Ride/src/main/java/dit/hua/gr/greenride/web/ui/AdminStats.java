package dit.hua.gr.greenride.web.ui;

public class AdminStats {
    private long totalUsers;
    private double averageOccupancy;
    private long totalRides;
    private long activeDrivers;
    private long activePassengers;

    // Constructor με 5 παραμέτρους για να δέχεται όλα τα στατιστικά
    public AdminStats(long totalUsers, double averageOccupancy, long totalRides, long activeDrivers, long activePassengers) {
        this.totalUsers = totalUsers;
        this.averageOccupancy = averageOccupancy;
        this.totalRides = totalRides;
        this.activeDrivers = activeDrivers;
        this.activePassengers = activePassengers;
    }

    // Getters - Απαραίτητοι για να μπορεί η Thymeleaf να διαβάσει τις τιμές
    public long getTotalUsers() { return totalUsers; }
    public double getAverageOccupancy() { return averageOccupancy; }
    public long getTotalRides() { return totalRides; }
    public long getActiveDrivers() { return activeDrivers; }
    public long getActivePassengers() { return activePassengers; }

    // Setters (Προαιρετικοί αλλά καλό είναι να υπάρχουν)
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }
    public void setAverageOccupancy(double averageOccupancy) { this.averageOccupancy = averageOccupancy; }
    public void setTotalRides(long totalRides) { this.totalRides = totalRides; }
    public void setActiveDrivers(long activeDrivers) { this.activeDrivers = activeDrivers; }
    public void setActivePassengers(long activePassengers) { this.activePassengers = activePassengers; }
}