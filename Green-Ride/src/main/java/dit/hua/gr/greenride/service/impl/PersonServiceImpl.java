package dit.hua.gr.greenride.service.impl;

import dit.hua.gr.greenride.core.model.Person;
import dit.hua.gr.greenride.core.model.PersonType;
import dit.hua.gr.greenride.core.port.PhoneNumberPort;
import dit.hua.gr.greenride.core.port.SmsNotificationPort;
import dit.hua.gr.greenride.core.port.impl.dto.PhoneNumberValidationResult;
import dit.hua.gr.greenride.core.repository.PersonRepository;
import dit.hua.gr.greenride.service.PersonService;
import dit.hua.gr.greenride.service.mapper.PersonMapper;
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
    private final PersonMapper personMapper;
    private final PhoneNumberPort phoneNumberPort;
    private final SmsNotificationPort smsNotificationPort;

    public PersonServiceImpl(final Validator validator,
                             final PasswordEncoder passwordEncoder,
                             final PersonRepository personRepository,
                             final PersonMapper personMapper,
                             final PhoneNumberPort phoneNumberPort,
                             final SmsNotificationPort smsNotificationPort) {
        if (validator == null) throw new NullPointerException();
        if (passwordEncoder == null) throw new NullPointerException();
        if (personRepository == null) throw new NullPointerException();
        if (personMapper == null) throw new NullPointerException();
        if (phoneNumberPort == null) throw new NullPointerException();
        if (smsNotificationPort == null) throw new NullPointerException();

        this.validator = validator;
        this.passwordEncoder = passwordEncoder;
        this.personRepository = personRepository;
        this.personMapper = personMapper;
        this.phoneNumberPort = phoneNumberPort;
        this.smsNotificationPort = smsNotificationPort;
    }

    @Transactional
    @Override
    public CreatePersonResult createPerson(final CreatePersonRequest createPersonRequest, final boolean notify) {
        if (createPersonRequest == null) throw new NullPointerException();

        // `CreatePersonRequest` validation.
        // --------------------------------------------------

        final Set<ConstraintViolation<CreatePersonRequest>> requestViolations =
                this.validator.validate(createPersonRequest);
        if (!requestViolations.isEmpty()) {
            final StringBuilder sb = new StringBuilder();
            for (final ConstraintViolation<CreatePersonRequest> violation : requestViolations) {
                sb
                        .append(violation.getPropertyPath())
                        .append(": ")
                        .append(violation.getMessage())
                        .append("\n");
            }
            return CreatePersonResult.fail(sb.toString());
        }

        // Unpack (we assume a valid `CreatePersonRequest` instance).
        // --------------------------------------------------

        final String firstName = createPersonRequest.firstName().strip();
        final String lastName = createPersonRequest.lastName().strip();
        final String emailAddress = createPersonRequest.emailAddress().strip();
        String mobilePhoneNumber = createPersonRequest.mobilePhoneNumber().strip();
        final String rawPassword = createPersonRequest.rawPassword();

        // Advanced mobile phone number validation.
        // --------------------------------------------------

        final PhoneNumberValidationResult phoneNumberValidationResult =
                this.phoneNumberPort.validate(mobilePhoneNumber);
        if (!phoneNumberValidationResult.isValidMobile()) {
            return CreatePersonResult.fail("Mobile Phone Number is not valid");
        }
        mobilePhoneNumber = phoneNumberValidationResult.e164();

        // Uniqueness checks.
        // --------------------------------------------------

        if (this.personRepository.existsByEmailAddress(emailAddress)) {
            return CreatePersonResult.fail("Email Address already registered");
        }

        if (this.personRepository.existsByMobilePhoneNumber(mobilePhoneNumber)) {
            return CreatePersonResult.fail("Mobile Phone Number already registered");
        }

        // Generate unique public userId.
        // --------------------------------------------------

        final String userId = generateUniqueUserId();

        // Hash password.
        // --------------------------------------------------

        final String hashedPassword = this.passwordEncoder.encode(rawPassword);

        // Instantiate Person (default USER).
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

        // Validate Person entity.
        // --------------------------------------------------

        final Set<ConstraintViolation<Person>> personViolations = this.validator.validate(person);
        if (!personViolations.isEmpty()) {
            // At this point, errors/violations on the `Person` instance
            // indicate a programmer error, not a client error.
            throw new RuntimeException("invalid Person instance");
        }

        // Persist person (save/insert to database).
        // --------------------------------------------------

        person = this.personRepository.save(person);

        // Send SMS notification if requested.
        // --------------------------------------------------

        if (notify) {
            final String content = String.format(
                    "You have successfully registered for the GreenRide application. " +
                            "Use your email (%s) to log in.", emailAddress);
            final boolean sent = this.smsNotificationPort.sendSms(mobilePhoneNumber, content);
            if (!sent) {
                LOGGER.warn("SMS send to {} failed!", mobilePhoneNumber);
            }
        }

        // Map `Person` to `PersonView`.
        // --------------------------------------------------

        final PersonView personView = this.personMapper.convertPersonToPersonView(person);

        // --------------------------------------------------

        return CreatePersonResult.success(personView);
    }

    /**
     * Generate a unique public userId for a new Person.
     *
     * @return a unique userId in the form GR-XXXXXXXX
     */
    private String generateUniqueUserId() {
        String candidate;
        do {
            candidate = "GR-" + UUID.randomUUID()
                    .toString()
                    .replace("-", "")
                    .substring(0, 8)
                    .toUpperCase();
        } while (this.personRepository.existsByUserId(candidate));
        return candidate;
    }
}