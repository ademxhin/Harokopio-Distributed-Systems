package dit.hua.gr.greenride.core.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity // ΑΠΑΡΑΙΤΗΤΟ: Δηλώνει στην JPA ότι αυτή η κλάση είναι πίνακας στη ΒΔ
@Table(name = "ride")
public class Ride {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String departure; // Αφετηρία

    @NotNull
    private String destination; // Προορισμός

    @NotNull
    private LocalDateTime departureTime; // Ώρα αναχώρησης

    @Min(1)
    private int availableSeats; // Διαθέσιμες θέσεις

    private int bookedSeats = 0; // Κρατημένες θέσεις (χρειάζεται για το AdminStats)

    // Υποχρεωτικός constructor για την JPA
    protected Ride() {}

    public Ride(String departure, String destination, LocalDateTime departureTime, int availableSeats) {
        this.departure = departure;
        this.destination = destination;
        this.departureTime = departureTime;
        this.availableSeats = availableSeats;
    }

    // --- Getters & Setters ---
    public Long getId() { return id; }
    public String getDeparture() { return departure; }
    public String getDestination() { return destination; }
    public int getAvailableSeats() { return availableSeats; }
    public int getBookedSeats() { return bookedSeats; }
    public void setBookedSeats(int bookedSeats) { this.bookedSeats = bookedSeats; }
}