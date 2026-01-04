package dit.hua.gr.greenride.service;

import dit.hua.gr.greenride.core.model.Person;
import dit.hua.gr.greenride.core.repository.PersonRepository;
import dit.hua.gr.greenride.core.repository.RideRepository;
import dit.hua.gr.greenride.web.ui.AdminStats;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class AdminService {

    private final RideRepository rideRepository;
    private final PersonRepository personRepository;

    public AdminService(RideRepository rideRepository, PersonRepository personRepository) {
        this.rideRepository = rideRepository;
        this.personRepository = personRepository;
    }

    @Transactional(readOnly = true)
    public AdminStats getSystemStatistics() {
        long totalRides = rideRepository.count(); // Αριθμός διαδρομών
        Double avgOccupancy = rideRepository.calculateAverageOccupancy(); // Μέσος όρος πληρότητας

        long drivers = personRepository.countByIsDriverTrue();
        long passengers = personRepository.countByIsDriverFalse();

        return new AdminStats(totalRides, avgOccupancy != null ? avgOccupancy : 0.0, drivers, passengers);
    }

    @Transactional(readOnly = true)
    public List<Person> getFlaggedUsers() {
        return personRepository.findAllByReportCountGreaterThan(5); // Διαχείριση κακόβουλων
    }
}