package dit.hua.gr.greenride.core.repository;

import dit.hua.gr.greenride.core.model.Booking;
import dit.hua.gr.greenride.core.model.Person;
import dit.hua.gr.greenride.core.model.Ride;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByPerson(Person person);

    List<Booking> findAllByPerson(Person person);

    List<Booking> findByPersonAndRide_DepartureTimeBefore(Person person, LocalDateTime time);

    List<Booking> findByRide(Ride ride);

    Optional<Booking> findByIdAndPerson(Long id, Person person);

    boolean existsByRideAndPerson(Ride ride, Person person);

    void deleteAllByRide(Ride ride);
}