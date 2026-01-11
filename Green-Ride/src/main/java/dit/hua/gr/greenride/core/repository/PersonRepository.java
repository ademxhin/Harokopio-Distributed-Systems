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

    long countByPersonTypeAndUserTypeIn(PersonType personType, List<UserType> userTypes);
    List<Person> findAllByReportCountGreaterThan(int count);

    List<Person> findByFirstNameContainingIgnoreCaseAndUserType(String name, UserType type);
    List<Person> findAllByUserType(UserType type);

    long countByUserType(UserType userType);
    long countByPersonType(PersonType personType);
}