package dit.hua.gr.greenride.core.model;

import jakarta.persistence.*;
import lombok.*;
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

    private String origin;
    private String destination;
    private LocalDateTime departureTime;

    // Διαθέσιμες θέσεις
    private int availableSeats;

    // Πόσες έχουν κλειστεί (αν θες να το χρησιμοποιήσεις αργότερα)
    private int bookedSeats = 0;

    // Ο οδηγός που δημιούργησε τη διαδρομή
    @ManyToOne
    @JoinColumn(name = "driver_id")
    private Person driver;

    // Συμβατότητα με CreateRideForm.seatsAvailable
    public void setSeatsAvailable(int seatsAvailable) {
        this.availableSeats = seatsAvailable;
    }
}
