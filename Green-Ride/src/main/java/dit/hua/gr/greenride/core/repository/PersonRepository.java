package dit.hua.gr.greenride.core.repository;

import dit.hua.gr.greenride.core.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

    Optional<Person> findByEmailAddress(String emailAddress);

    // Μέθοδοι Uniqueness για το PersonBusinessLogicServiceImpl
    boolean existsByEmailAddress(String emailAddress);
    boolean existsByMobilePhoneNumber(String mobilePhoneNumber);
    boolean existsByUserId(String userId);

    // Μέθοδοι Στατιστικών για τον AdminService
    long countByIsDriverTrue();
    long countByIsDriverFalse();
    List<Person> findAllByReportCountGreaterThan(int reportCount);
}