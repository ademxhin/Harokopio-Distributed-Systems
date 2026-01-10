package dit.hua.gr.greenride.service;

import dit.hua.gr.greenride.core.model.Person;
import dit.hua.gr.greenride.core.model.PersonType;
import dit.hua.gr.greenride.core.model.UserType;
import dit.hua.gr.greenride.core.repository.PersonRepository;
import dit.hua.gr.greenride.core.repository.RideRepository;
import dit.hua.gr.greenride.web.ui.AdminStats;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdminService {

    private final PersonRepository personRepository;
    private final RideRepository rideRepository;

    private final List<String> kickedUserNames = new ArrayList<>();

    public AdminService(PersonRepository personRepository, RideRepository rideRepository) {
        this.personRepository = personRepository;
        this.rideRepository = rideRepository;
    }

    public AdminStats getSystemStatistics() {
        long totalRides = rideRepository.count();
        Double avgOccupancy = rideRepository.calculateAverageOccupancy();
        long totalUsers = personRepository.count();

        // ✅ ΑΠΛΟΠΟΙΗΣΗ: Μετράμε απευθείας ανά τύπο χρήστη
        // Χρησιμοποιούμε τη μέθοδο countByUserType που προσθέσαμε στο PersonRepository
        long drivers = personRepository.countByUserType(UserType.DRIVER);
        long passengers = personRepository.countByUserType(UserType.PASSENGER);

        return new AdminStats(
                totalUsers,
                avgOccupancy != null ? avgOccupancy : 0.0,
                totalRides,
                drivers,
                passengers
        );
    }

    public List<String> getFlaggedUsers() {
        return kickedUserNames;
    }


    public List<Person> getAllUsersExcludingAdmins() {
        return personRepository.findAll().stream()
                .filter(p -> !p.isAdmin())
                .toList();
    }

    public List<String> getKickedUserNames() {
        return kickedUserNames;
    }

    public void logKickedUser(String fullName) {
        kickedUserNames.add(fullName);
    }
}
