package dit.hua.gr.greenride.core.repository;

import dit.hua.gr.greenride.core.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long> {

    // Για το Security
    Optional<Person> findByEmailAddress(String emailAddress);

    // Για το Validation εγγραφής
    boolean existsByEmailAddress(String emailAddress);
    boolean existsByMobilePhoneNumber(String mobilePhoneNumber);
    boolean existsByUserId(String userId);

    // Για τα στατιστικά του Admin
    long countByIsDriverTrue();
    long countByIsDriverFalse();

    List<Person> findAllByReportCountGreaterThan(int reportCount);
}