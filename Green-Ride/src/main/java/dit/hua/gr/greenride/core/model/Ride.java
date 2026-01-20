package dit.hua.gr.greenride.core.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "ride")
@Getter
@Setter
@NoArgsConstructor
public class Ride {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String startLocation;

    @Column(nullable = false)
    private String endLocation;

    @Column(nullable = false)
    private LocalDateTime departureTime;

    @Column(nullable = false)
    private int seatsAvailable;

    @Column(nullable = false)
    private int bookedSeats = 0;

    @ManyToOne(optional = false)
    @JoinColumn(name = "driver_id", nullable = false)
    private Person driver;

    public String getOrigin() {
        return startLocation;
    }

    public String getDestination() {
        return endLocation;
    }

    public int getAvailableSeats() {
        return seatsAvailable - bookedSeats;
    }
}