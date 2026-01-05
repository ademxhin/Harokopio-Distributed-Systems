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

    public AdminService(PersonRepository personRepository,
                        RideRepository rideRepository) {
        this.personRepository = personRepository;
        this.rideRepository = rideRepository;
    }

    public AdminStats getSystemStatistics() {

        long totalRides = rideRepository.count();
        double averageOccupancy = rideRepository.calculateAverageOccupancy();

        // USERs that can act as drivers (DRIVER or BOTH)
        long driversCount = personRepository.countByPersonTypeAndUserTypeIn(
                PersonType.USER,
                List.of(UserType.DRIVER, UserType.BOTH)
        );

        // USERs that are passengers only
        long passengersOnlyCount = personRepository.countByPersonTypeAndUserTypeIn(
                PersonType.USER,
                List.of(UserType.PASSENGER)
        );

        return new AdminStats(
                totalRides,
                averageOccupancy,
                driversCount,
                passengersOnlyCount
        );
    }

    public List<Person> getFlaggedUsers() {
        return personRepository.findAllByReportCountGreaterThan(5);
    }
}