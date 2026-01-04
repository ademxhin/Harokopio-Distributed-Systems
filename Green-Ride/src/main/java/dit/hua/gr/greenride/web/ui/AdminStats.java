package dit.hua.gr.greenride.web.ui;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminStats {
    private long totalRides;
    private double averageOccupancy;
    private long activeDrivers;
    private long activePassengers;
}