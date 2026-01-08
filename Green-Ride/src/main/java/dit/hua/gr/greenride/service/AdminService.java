package dit.hua.gr.greenride.service;

import dit.hua.gr.greenride.core.model.Person;
import dit.hua.gr.greenride.core.model.PersonType;
import dit.hua.gr.greenride.core.model.UserType;
import dit.hua.gr.greenride.core.repository.PersonRepository;
import dit.hua.gr.greenride.core.repository.RideRepository;
import dit.hua.gr.greenride.web.ui.AdminStats;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AdminService {
    private final PersonRepository personRepository;
    private final RideRepository rideRepository;

    public AdminService(PersonRepository personRepository, RideRepository rideRepository) {
        this.personRepository = personRepository;
        this.rideRepository = rideRepository;
    }

    public AdminStats getSystemStatistics() {
        long totalRides = rideRepository.count();
        Double avgOccupancy = rideRepository.calculateAverageOccupancy();

        long drivers = personRepository.countByPersonTypeAndUserTypeIn(
                PersonType.USER, List.of(UserType.DRIVER, UserType.BOTH));
        long passengers = personRepository.countByPersonTypeAndUserTypeIn(
                PersonType.USER, List.of(UserType.PASSENGER));

        return new AdminStats(totalRides, avgOccupancy != null ? avgOccupancy : 0.0, drivers, passengers);
    }

    // ✅ Fixes "cannot find symbol: method getFlaggedUsers()"
    public List<Person> getFlaggedUsers() {
        return personRepository.findAllByReportCountGreaterThan(5);
    }
}