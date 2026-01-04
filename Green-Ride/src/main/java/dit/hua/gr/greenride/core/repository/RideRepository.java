package dit.hua.gr.greenride.core.repository;

import dit.hua.gr.greenride.core.model.Ride;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {

    // Query για τον υπολογισμό μέσης πληρότητας που ζητάει ο Admin [cite: 216]
    @Query("SELECT AVG(CAST(r.bookedSeats AS double) / r.availableSeats) * 100 FROM Ride r WHERE r.availableSeats > 0")
    Double calculateAverageOccupancy();
}