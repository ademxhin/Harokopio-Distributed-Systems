package dit.hua.gr.greenride.core.repository;

import dit.hua.gr.greenride.core.model.Person;
import dit.hua.gr.greenride.core.model.PersonType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for {@link Person} entity.
 */
@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

    /**
     * Find a person by public userId.
     *
     * @param userId the public user identifier
     * @return an Optional containing the person if found, empty otherwise
     */
    Optional<Person> findByUserId(final String userId);

    /**
     * Find a person by email address (case-insensitive).
     *
     * @param emailAddress the email address to search for
     * @return an Optional containing the person if found, empty otherwise
     */
    Optional<Person> findByEmailAddressIgnoreCase(final String emailAddress);

    /**
     * Find all persons of a specific type, ordered by last name.
     *
     * @param personType the person type to filter by
     * @return a list of persons of the given type, ordered by last name
     */
    List<Person> findAllByPersonTypeOrderByLastName(final PersonType personType);

    /**
     * Check if a person exists with the given email address (case-sensitive).
     *
     * Used by PersonServiceImpl to enforce email uniqueness.
     *
     * @param emailAddress the email address to check
     * @return true if a person with this email address already exists
     */
    boolean existsByEmailAddress(final String emailAddress);

    /**
     * Check if a person exists with the given mobile phone number.
     *
     * @param mobilePhoneNumber the mobile phone number to check
     * @return true if a person with this mobile number already exists
     */
    boolean existsByMobilePhoneNumber(final String mobilePhoneNumber);

    /**
     * Check if a person exists with the given public userId.
     *
     * Used by PersonServiceImpl to ensure unique user identifiers.
     *
     * @param userId the public user identifier to check
     * @return true if a person with this userId already exists
     */
    boolean existsByUserId(final String userId);
}
