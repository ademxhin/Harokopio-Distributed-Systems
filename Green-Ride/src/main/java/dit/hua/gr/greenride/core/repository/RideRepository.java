package dit.hua.gr.greenride.core.repository;

import dit.hua.gr.greenride.core.model.Person;
import dit.hua.gr.greenride.core.model.Ride;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {

    List<Ride> findByDriver(Person driver);

    @Query("SELECT COALESCE(AVG(CAST(r.bookedSeats AS double) / r.availableSeats) * 100, 0.0) " +
            "FROM Ride r WHERE r.availableSeats > 0")
    Double calculateAverageOccupancy();
}
