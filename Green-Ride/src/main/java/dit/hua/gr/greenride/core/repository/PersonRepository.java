package dit.hua.gr.greenride.core.repository;

import dit.hua.gr.greenride.core.model.Person;
import dit.hua.gr.greenride.core.model.PersonType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long> {

    Optional<Person> findByEmailAddress(String emailAddress);
    boolean existsByEmailAddress(String emailAddress);
    boolean existsByMobilePhoneNumber(String mobilePhoneNumber);
    boolean existsByUserId(String userId);

    List<Person> findByFirstNameContainingIgnoreCaseAndPersonType(String firstName, PersonType personType);
    List<Person> findAllByPersonType(PersonType personType);

    long countByPersonType(PersonType personType);
}