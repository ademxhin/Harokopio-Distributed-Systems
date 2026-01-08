package dit.hua.gr.greenride.core.repository;

import dit.hua.gr.greenride.core.model.Booking;
import dit.hua.gr.greenride.core.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByPerson(Person person);
    List<Booking> findByPersonAndRide_DepartureTimeBefore(Person person, java.time.LocalDateTime time);

}
