package dit.hua.gr.greenride.core.repository;

import dit.hua.gr.greenride.core.model.Person;
import dit.hua.gr.greenride.core.model.PersonType;
import dit.hua.gr.greenride.core.model.UserType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long> {

    Optional<Person> findByEmailAddress(String emailAddress);

    boolean existsByEmailAddress(String emailAddress);
    boolean existsByMobilePhoneNumber(String mobilePhoneNumber);
    boolean existsByUserId(String userId);

    List<Person> findAllByReportCountGreaterThan(int reportCount);

    // For stats (optional)
    long countByPersonTypeAndUserTypeIn(PersonType personType, List<UserType> userTypes);
}
