package dit.hua.gr.greenride.core.repository;

import dit.hua.gr.greenride.core.model.Booking;
import dit.hua.gr.greenride.core.model.Person;
import dit.hua.gr.greenride.core.model.Ride; // ✅ Προσθήκη αυτού
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByPerson(Person person);

    // ✅ Χρήσιμη μέθοδος για να βρίσκουμε ποιες διαδρομές έχει κλείσει ο χρήστης
    List<Booking> findAllByPerson(Person person);

    List<Booking> findByPersonAndRide_DepartureTimeBefore(Person person, LocalDateTime time);

    boolean existsByRideAndPerson(Ride ride, Person person);
}