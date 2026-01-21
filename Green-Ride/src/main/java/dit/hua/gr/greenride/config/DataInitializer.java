package dit.hua.gr.greenride.config;

import dit.hua.gr.greenride.core.model.*;
import dit.hua.gr.greenride.core.repository.*;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.*;

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
            System.out.println("Dummy data already exists. Skipping initialization.");
            return;
        }

        List<Person> drivers = createDrivers();
        List<Person> passengers = createPassengers();

        List<Ride> rides = new ArrayList<>();
        for (Person driver : drivers) {
            for (int i = 0; i < 2; i++) {
                Ride ride = createRide(
                        driver,
                        randomDepartureBetween5And120Minutes(),
                        1 + random.nextInt(4)
                );
                rides.add(rideRepository.save(ride));
            }
        }

        Collections.shuffle(rides, random);

        for (Person passenger : passengers) {
            int bookingsToCreate = 2;
            int created = 0;

            for (Ride ride : rides) {
                if (created >= bookingsToCreate) break;

                boolean ok = tryCreateConfirmedBooking(passenger, ride);
                if (ok) created++;
            }
        }

        System.out.println("Dummy data initialized successfully");
    }

    private boolean tryCreateConfirmedBooking(Person passenger, Ride ride) {
        if (ride.getSeatsAvailable() <= 0) {
            return false;
        }

        Booking b = new Booking();
        b.setPerson(passenger);
        b.setRide(ride);
        b.setStatus(BookingStatus.APPROVED);
        b.setCreatedAt(LocalDateTime.now());

        ride.setSeatsAvailable(ride.getSeatsAvailable() - 1);
        ride.setBookedSeats(ride.getBookedSeats() + 1);

        rideRepository.save(ride);
        bookingRepository.save(b);
        return true;
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

    private List<Person> createPeople(String[][] names, PersonType type) {
        List<Person> created = new ArrayList<>();

        for (String[] n : names) {
            Person p = new Person();
            p.setUserId("gr-" + n[0].toLowerCase() + random.nextInt(10_000));
            p.setFirstName(n[0]);
            p.setLastName(n[1]);
            p.setEmailAddress((n[0] + "." + n[1]).toLowerCase() + "@greenride.com");
            p.setHashedPassword(passwordEncoder.encode("password"));
            p.setMobilePhoneNumber(randomGreekMobile());
            p.setPersonType(type);
            p.setBanned(false);
            p.setReportCount(0);

            created.add(personRepository.save(p));
        }
        return created;
    }

    private Ride createRide(Person driver, LocalDateTime departure, int seats) {
        Ride ride = new Ride();
        ride.setDriver(driver);
        ride.setStartLocation(randomAthensLocation());
        ride.setEndLocation(randomAthensLocationDifferent(ride.getStartLocation()));
        ride.setDepartureTime(departure);
        ride.setSeatsAvailable(seats); // remaining seats
        ride.setBookedSeats(0);

        return ride;
    }

    private LocalDateTime randomDepartureBetween5And120Minutes() {
        int minutes = 5 + random.nextInt(116); // 5–120
        return LocalDateTime.now().plusMinutes(minutes);
    }

    private String randomGreekMobile() {
        return "69" + (10000000 + random.nextInt(90000000));
    }

    private static final String[] ATHENS_LOCATIONS = {
            "Syntagma",
            "Omonia",
            "Monastiraki",
            "Kolonaki",
            "Exarchia",
            "Kifisia",
            "Marousi",
            "Chalandri",
            "Peristeri",
            "Ilion",
            "Nea Smyrni",
            "Palaio Faliro",
            "Glyfada",
            "Voula",
            "Alimos",
            "Zografou",
            "Kaisariani",
            "Vyronas",
            "Petralona",
            "Piraeus"
    };

    private String randomAthensLocation() {
        return ATHENS_LOCATIONS[random.nextInt(ATHENS_LOCATIONS.length)];
    }

    private String randomAthensLocationDifferent(String from) {
        String to;
        do {
            to = randomAthensLocation();
        } while (to.equals(from));
        return to;
    }
}