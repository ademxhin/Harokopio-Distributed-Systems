package dit.hua.gr.greenride.service.impl;

import dit.hua.gr.greenride.core.model.Person;
import dit.hua.gr.greenride.core.model.PersonType;
import dit.hua.gr.greenride.core.repository.PersonRepository;
import dit.hua.gr.greenride.service.PersonService;
import dit.hua.gr.greenride.service.model.CreatePersonRequest;
import dit.hua.gr.greenride.service.model.CreatePersonResult;
import dit.hua.gr.greenride.service.model.PersonView;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

/**
 * Default implementation of {@link PersonService} for the GreenRide application.
 */
@Service
public class PersonServiceImpl implements PersonService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonServiceImpl.class);

    private final Validator validator;
    private final PasswordEncoder passwordEncoder;
    private final PersonRepository personRepository;

    public PersonServiceImpl(final Validator validator,
                             final PasswordEncoder passwordEncoder,
                             final PersonRepository personRepository) {
        if (validator == null) throw new NullPointerException("validator is null");
        if (passwordEncoder == null) throw new NullPointerException("passwordEncoder is null");
        if (personRepository == null) throw new NullPointerException("personRepository is null");

        this.validator = validator;
        this.passwordEncoder = passwordEncoder;
        this.personRepository = personRepository;
    }

    @Transactional
    @Override
    public CreatePersonResult createPerson(final CreatePersonRequest createPersonRequest, final boolean notify) {
        if (createPersonRequest == null) throw new NullPointerException("createPersonRequest is null");

        // Validate CreatePersonRequest using Bean Validation
        // --------------------------------------------------
        final Set<ConstraintViolation<CreatePersonRequest>> requestViolations =
                this.validator.validate(createPersonRequest);

        if (!requestViolations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<CreatePersonRequest> violation : requestViolations) {
                sb.append(violation.getPropertyPath())
                        .append(": ")
                        .append(violation.getMessage())
                        .append("\n");
            }
            // Για την ώρα πετάμε exception· μπορεί αργότερα να γίνει CreatePersonResult.fail(...)
            throw new IllegalArgumentException("Invalid CreatePersonRequest:\n" + sb);
        }

        // Unpack (we assume valid CreatePersonRequest instance)
        // --------------------------------------------------
        final String firstName = createPersonRequest.firstName().strip();
        final String lastName = createPersonRequest.lastName().strip();
        final String emailAddress = createPersonRequest.emailAddress().strip();
        final String rawMobilePhoneNumber = createPersonRequest.mobilePhoneNumber().strip();
        final String rawPassword = createPersonRequest.rawPassword();

        // Basic normalization
        String mobilePhoneNumber = rawMobilePhoneNumber.replaceAll("\\s+", "");

        // Uniqueness checks
        // --------------------------------------------------
        if (this.personRepository.existsByEmailAddress(emailAddress)) {
            LOGGER.warn("Attempt to register with already used email address: {}", emailAddress);
            throw new IllegalArgumentException("Email address already registered");
        }

        if (this.personRepository.existsByMobilePhoneNumber(mobilePhoneNumber)) {
            LOGGER.warn("Attempt to register with already used mobile number: {}", mobilePhoneNumber);
            throw new IllegalArgumentException("Mobile phone number already registered");
        }

        // Generate public display userId (e.g. GR-AB12CD34) and ensure uniqueness
        // --------------------------------------------------
        final String userId = generateUniqueUserId();

        // Hash password
        // --------------------------------------------------
        final String hashedPassword = this.passwordEncoder.encode(rawPassword);

        // Instantiate Person (default type = USER)
        // --------------------------------------------------
        Person person = new Person(
                userId,
                firstName,
                lastName,
                mobilePhoneNumber,
                emailAddress,
                PersonType.USER,
                hashedPassword
        );

        // Validate Person entity as well (domain-level validation)
        // --------------------------------------------------
        final Set<ConstraintViolation<Person>> personViolations = this.validator.validate(person);
        if (!personViolations.isEmpty()) {
            // Programmer error: the mapping from request → entity is wrong.
            throw new RuntimeException("Invalid Person instance created from request");
        }

        // Persist person (save/insert to database)
        // --------------------------------------------------
        person = this.personRepository.save(person);

        // Optional notification hook
        // --------------------------------------------------
        if (notify) {
            LOGGER.info("User {} ({}) registered successfully. Notification flag is true (no-op for now).",
                    person.getEmailAddress(), person.getUserId());
            // TODO: Implement notification logic (e.g. email or SMS) if required.
        }

        // Map Person to PersonView
        // --------------------------------------------------
        PersonView personView = new PersonView(
                person.getId(),
                person.getFirstName(),
                person.getLastName(),
                person.getMobilePhoneNumber(),
                person.getEmailAddress(),
                person.getPersonType()
        );

        return CreatePersonResult.success(personView);
    }

    /**
     * Generates a unique public display user id (e.g. GR-AB12CD34).
     * It checks the database to ensure uniqueness.
     */
    private String generateUniqueUserId() {
        String candidate;
        do {
            // Example format: GR-XXXXXXXX (8 hex chars)
            candidate = "GR-" + UUID.randomUUID()
                    .toString()
                    .replace("-", "")
                    .substring(0, 8)
                    .toUpperCase();
        } while (personRepository.existsByUserId(candidate));
        return candidate;
    }
}
