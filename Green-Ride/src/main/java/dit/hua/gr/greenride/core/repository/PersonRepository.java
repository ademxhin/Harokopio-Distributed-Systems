package dit.hua.gr.greenride.core.repository;

import dit.hua.gr.greenride.core.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long> {

    boolean existsByEmailAddress(String emailAddress);
    boolean existsByUserId(String userId);

    boolean existsByMobilePhoneNumber(String mobilePhoneNumber);

    Optional<Person> findByEmailAddress(String emailAddress);
}
