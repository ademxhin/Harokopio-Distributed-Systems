package dit.hua.gr.greenride.web.ui;

public class AdminStats {
    private long totalUsers;
    private double avgRating;
    private long totalRides;
    private long totalBookings;

    // Πρόσθετα πεδία (προαιρετικά)
    private double averageOccupancy;
    private long activeDrivers;
    private long activePassengers;

    // Κενός Constructor
    public AdminStats() {}

    // Ο Constructor που καλεί ο AdminService
    public AdminStats(long totalUsers, double avgRating, long totalRides, long totalBookings) {
        this.totalUsers = totalUsers;
        this.avgRating = avgRating;
        this.totalRides = totalRides;
        this.totalBookings = totalBookings;
    }

    // --- ΧΕΙΡΟΚΙΝΗΤΟΙ GETTERS (Απαραίτητοι για τη Thymeleaf) ---
    public long getTotalUsers() { return totalUsers; }
    public double getAvgRating() { return avgRating; }
    public long getTotalRides() { return totalRides; }
    public long getTotalBookings() { return totalBookings; }

    public double getAverageOccupancy() { return averageOccupancy; }
    public long getActiveDrivers() { return activeDrivers; }
    public long getActivePassengers() { return activePassengers; }
}