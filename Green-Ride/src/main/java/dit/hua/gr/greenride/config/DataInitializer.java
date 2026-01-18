package dit.hua.gr.greenride.config;

import dit.hua.gr.greenride.core.model.*;
import dit.hua.gr.greenride.core.repository.*;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Configuration
public class DataInitializer {

    private final PersonRepository personRepository;
    private final RideRepository rideRepository;
    private final BookingRepository bookingRepository;
    private final PasswordEncoder passwordEncoder;

    private final Random random = new Random();

    public DataInitializer(PersonRepository personRepository,
                           RideRepository rideRepository,
                           BookingRepository bookingRepository,
                           PasswordEncoder passwordEncoder) {
        this.personRepository = personRepository;
        this.rideRepository = rideRepository;
        this.bookingRepository = bookingRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {

        personRepository.findByEmailAddress("admin@greenride.com").orElseGet(() -> {
            Person admin = new Person();
            admin.setUserId("admin001");
            admin.setFirstName("Admin");
            admin.setLastName("GreenRide");
            admin.setEmailAddress("admin@greenride.com");
            admin.setMobilePhoneNumber(randomGreekMobile());
            admin.setHashedPassword(passwordEncoder.encode("GreenRide2026"));
            admin.setPersonType(PersonType.ADMIN);
            admin.setBanned(false);
            admin.setReportCount(0);

            System.out.println("Admin created");
            return personRepository.save(admin);
        });

        long existingNonAdmin = personRepository.findAll().stream()
                .filter(p -> p.getPersonType() != PersonType.ADMIN)
                .count();

        if (existingNonAdmin > 0) {
            System.out.println("ℹ Dummy data already exists (non-admin users found). Skipping.");
            return;
        }

        List<Person> drivers = createDrivers();
        List<Person> passengers = createPassengers();

        List<Ride> createdRides = new ArrayList<>();

        for (Person driver : drivers) {

            for (int i = 0; i < 2; i++) {
                Ride pastRide = createRide(driver, LocalDateTime.now().minusDays(10 + i),0,3);
                createdRides.add(rideRepository.save(pastRide));
            }

            for (int i = 0; i < 2; i++) {
                Ride futureRide = createRide(driver, LocalDateTime.now().plusDays(3 + i),3,0);
                createdRides.add(rideRepository.save(futureRide));
            }
        }

        List<Ride> pastRides = createdRides.stream()
                .filter(r -> r.getDepartureTime().isBefore(LocalDateTime.now()))
                .toList();

        List<Ride> futureRides = createdRides.stream()
                .filter(r -> r.getDepartureTime().isAfter(LocalDateTime.now()))
                .toList();

        for (Person passenger : passengers) {

            pastRides.stream()
                    .limit(2)
                    .forEach(ride -> bookingRepository.save(createBooking(passenger, ride)));

            futureRides.stream()
                    .limit(2)
                    .forEach(ride -> bookingRepository.save(createBooking(passenger, ride)));
        }

        System.out.println("Dummy data initialized successfully");
    }

    private List<Person> createDrivers() {
        String[][] names = {
                {"Giorgos", "Papadopoulos"},
                {"Nikos", "Ioannou"},
                {"Kostas", "Nikolaidis"},
                {"Dimitris", "Karagiannis"},
                {"Panagiotis", "Vasiliou"}
        };

        return createPeople(names, PersonType.DRIVER);
    }

    private List<Person> createPassengers() {
        String[][] names = {
                {"Maria", "Georgiou"},
                {"Eleni", "Katsouli"},
                {"Anna", "Papadaki"},
                {"Katerina", "Stavrou"},
                {"Sofia", "Dimitriou"}
        };

        return createPeople(names, PersonType.PASSENGER);
    }

    private List<Person> createPeople(String[][] names, PersonType personType) {
        List<Person> created = new ArrayList<>();

        for (String[] n : names) {
            String first = n[0];
            String last = n[1];

            Person p = new Person();
            p.setUserId("gr-" + first.toLowerCase() + random.nextInt(10000));
            p.setFirstName(first);
            p.setLastName(last);
            p.setEmailAddress((first + "." + last).toLowerCase() + "@example.com");
            p.setHashedPassword(passwordEncoder.encode("password"));
            p.setMobilePhoneNumber(randomGreekMobile());
            p.setPersonType(personType);
            p.setBanned(false);
            p.setReportCount(0);

            created.add(personRepository.save(p));
        }

        return created;
    }

    private Ride createRide(Person driver, LocalDateTime departure,
                            int seatsAvailable, int bookedSeats) {

        Ride ride = new Ride();
        ride.setDriver(driver);

        ride.setStartLocation(randomOrigin());
        ride.setEndLocation(randomDestination());
        ride.setDepartureTime(departure);

        ride.setSeatsAvailable(seatsAvailable);
        ride.setBookedSeats(bookedSeats);

        return ride;
    }

    private Booking createBooking(Person passenger, Ride ride) {
        Booking b = new Booking();
        b.setPerson(passenger);
        b.setRide(ride);
        b.setCreatedAt(LocalDateTime.now());

        if (ride.getDepartureTime().isAfter(LocalDateTime.now())) {
            if (ride.getSeatsAvailable() > 0) {
                ride.setSeatsAvailable(ride.getSeatsAvailable() - 1);
                ride.setBookedSeats(ride.getBookedSeats() + 1);
                rideRepository.save(ride);
            }
        }

        return b;
    }

    private String randomGreekMobile() {
        return "69" + (10000000 + random.nextInt(90000000));
    }

    private String randomOrigin() {
        String[] origins = {"Athens", "Piraeus", "Marousi", "Kifisia", "Glyfada"};
        return origins[random.nextInt(origins.length)];
    }

    private String randomDestination() {
        String[] dest = {"Thessaloniki", "Patras", "Larisa", "Volos", "Ioannina"};
        return dest[random.nextInt(dest.length)];
    }
}