package dit.hua.gr.greenride.core.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ride")
@Getter @Setter @NoArgsConstructor
public class Ride {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String origin;
    private String destination;
    private LocalDateTime departureTime;

    // ✅ ΑΛΛΑΓΗ: Το όνομα πρέπει να είναι availableSeats για να "βλέπει" το Query
    private int availableSeats;

    private int bookedSeats = 0;

    @ManyToOne
    private Person driver;
}