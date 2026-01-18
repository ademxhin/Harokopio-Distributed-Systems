package dit.hua.gr.greenride.web.ui;

public class AdminStats {

    private long totalRides;
    private double averageOccupancyPct; // 0..100

    private long totalUsers;
    private long activeDrivers;
    private long activePassengers;

    public AdminStats(long totalRides,
                      double averageOccupancyPct,
                      long totalUsers,
                      long activeDrivers,
                      long activePassengers) {
        this.totalRides = totalRides;
        this.averageOccupancyPct = averageOccupancyPct;
        this.totalUsers = totalUsers;
        this.activeDrivers = activeDrivers;
        this.activePassengers = activePassengers;
    }

    public long getTotalRides() { return totalRides; }

    public double getAverageOccupancyPct() { return averageOccupancyPct; }

    public long getAverageOccupancyPctRounded() { return Math.round(averageOccupancyPct); }

    public long getTotalUsers() { return totalUsers; }
    public long getActiveDrivers() { return activeDrivers; }
    public long getActivePassengers() { return activePassengers; }

    public void setTotalRides(long totalRides) { this.totalRides = totalRides; }
    public void setAverageOccupancyPct(double averageOccupancyPct) { this.averageOccupancyPct = averageOccupancyPct; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }
    public void setActiveDrivers(long activeDrivers) { this.activeDrivers = activeDrivers; }
    public void setActivePassengers(long activePassengers) { this.activePassengers = activePassengers; }
}