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

    // --- ΟΙ SETTERS ΠΟΥ ΛΕΙΠΟΥΝ ΚΑΙ ΠΡΟΚΑΛΟΥΝ ΤΑ ERROR ---

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    public void setEndLocation(String endLocation) {
        this.endLocation = endLocation;
    }

    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }

    // Μέσα στο Ride.java πρόσθεσε:

    public String getStartLocation() {
        return startLocation;
    }

    public String getEndLocation() {
        return endLocation;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    // Υπόλοιποι απαραίτητοι Getters/Setters
    public Long getId() { return id; }
    public int getSeatsAvailable() { return seatsAvailable; }
    public void setSeatsAvailable(int seatsAvailable) { this.seatsAvailable = seatsAvailable; }
    public int getBookedSeats() { return bookedSeats; }
    public void setBookedSeats(int bookedSeats) { this.bookedSeats = bookedSeats; }
    public Person getDriver() { return driver; }
    public void setDriver(Person driver) { this.driver = driver; }
}