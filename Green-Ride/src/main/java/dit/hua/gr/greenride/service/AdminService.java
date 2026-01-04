package dit.hua.gr.greenride.service;

import dit.hua.gr.greenride.core.model.Person;
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
        return new AdminStats(
                rideRepository.count(),
                rideRepository.calculateAverageOccupancy(),
                personRepository.countByIsDriverTrue(), // Λύνει το σφάλμα
                personRepository.countByIsDriverFalse() // Λύνει το σφάλμα
        );
    }

    // Λύνει το σφάλμα στον AdminController
    public List<Person> getFlaggedUsers() {
        return personRepository.findAllByReportCountGreaterThan(5);
    }
}