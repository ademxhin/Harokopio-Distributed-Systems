package dit.hua.gr.greenride.core.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ride")
public class Ride {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String startLocation;
    private String endLocation;
    private LocalDateTime departureTime;
    private int seatsAvailable;
    private int bookedSeats = 0;

    @ManyToOne
    @JoinColumn(name = "driver_id")
    private Person driver;

    public Ride() {}

    public String getOrigin() { return startLocation; }

    public String getDestination() { return endLocation; }

    public int getAvailableSeats() { return seatsAvailable; }

    public Long getId() { return id; }

    public String getStartLocation() { return startLocation; }
    public void setStartLocation(String startLocation) { this.startLocation = startLocation; }

    public String getEndLocation() { return endLocation; }
    public void setEndLocation(String endLocation) { this.endLocation = endLocation; }

    public LocalDateTime getDepartureTime() { return departureTime; }
    public void setDepartureTime(LocalDateTime departureTime) { this.departureTime = departureTime; }

    public int getSeatsAvailable() { return seatsAvailable; }
    public void setSeatsAvailable(int seatsAvailable) { this.seatsAvailable = seatsAvailable; }

    public int getBookedSeats() { return bookedSeats; }
    public void setBookedSeats(int bookedSeats) { this.bookedSeats = bookedSeats; }

    public Person getDriver() { return driver; }
    public void setDriver(Person driver) { this.driver = driver; }
}