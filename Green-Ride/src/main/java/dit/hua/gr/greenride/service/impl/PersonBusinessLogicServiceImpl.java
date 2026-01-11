package dit.hua.gr.greenride.service.impl;

import dit.hua.gr.greenride.core.model.Person;
import dit.hua.gr.greenride.core.model.PersonType;
import dit.hua.gr.greenride.core.model.UserType;
import dit.hua.gr.greenride.core.port.PhoneNumberPort;
import dit.hua.gr.greenride.core.port.SmsNotificationPort;
import dit.hua.gr.greenride.core.port.impl.dto.PhoneNumberValidationResult;
import dit.hua.gr.greenride.core.repository.PersonRepository;
import dit.hua.gr.greenride.service.PersonBusinessLogicService;
import dit.hua.gr.greenride.service.mapper.PersonMapper;
import dit.hua.gr.greenride.service.model.CreatePersonRequest;
import dit.hua.gr.greenride.service.model.CreatePersonResult;
import dit.hua.gr.greenride.service.model.PersonView;
import dit.hua.gr.greenride.web.ui.exceptions.ExternalServiceUnavailableException;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.Set;
import java.util.UUID;

@Service
public class PersonBusinessLogicServiceImpl implements PersonBusinessLogicService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonBusinessLogicServiceImpl.class);

    private final Validator validator;
    private final PasswordEncoder passwordEncoder;
    private final PersonRepository personRepository;
    private final PersonMapper personMapper;
    private final PhoneNumberPort phoneNumberPort;
    private final SmsNotificationPort smsNotificationPort;

    public PersonBusinessLogicServiceImpl(final Validator validator,
                                          final PasswordEncoder passwordEncoder,
                                          final PersonRepository personRepository,
                                          final PersonMapper personMapper,
                                          final PhoneNumberPort phoneNumberPort,
                                          final SmsNotificationPort smsNotificationPort) {

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
        if (createPersonRequest == null) throw new NullPointerException("createPersonRequest is null");

        // Validate CreatePersonRequest
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
            return CreatePersonResult.fail(sb.toString());
        }

        // Unpack & normalize
        final String firstName = createPersonRequest.firstName().strip();
        final String lastName = createPersonRequest.lastName().strip();
        final String emailAddress = createPersonRequest.emailAddress().strip();
        String mobilePhoneNumber = createPersonRequest.mobilePhoneNumber().strip();
        final String rawPassword = createPersonRequest.rawPassword();
        final String confirmRawPassword = createPersonRequest.confirmRawPassword();

        final UserType userType = createPersonRequest.userType();
        if (userType == null) {
            return CreatePersonResult.fail("Please select a role");
        }

        // Confirm password check (business rule)
        if (!rawPassword.equals(confirmRawPassword)) {
            return CreatePersonResult.fail("Password and Confirm Password do not match");
        }

        // Advanced phone validation via PhoneNumberPort
        final PhoneNumberValidationResult phoneResult;
        try {
            phoneResult = this.phoneNumberPort.validate(mobilePhoneNumber);
        } catch (RestClientException ex) {
            throw new ExternalServiceUnavailableException("NOC phone validation service is unavailable", ex);
        } catch (RuntimeException ex) {
            throw ex;
        }

        if (!phoneResult.isValidMobile()) {
            return CreatePersonResult.fail("Mobile Phone Number is not valid");
        }
        mobilePhoneNumber = phoneResult.e164();

        // Uniqueness checks
        if (this.personRepository.existsByEmailAddress(emailAddress)) {
            return CreatePersonResult.fail("Email Address already registered");
        }

        if (this.personRepository.existsByMobilePhoneNumber(mobilePhoneNumber)) {
            return CreatePersonResult.fail("Mobile Phone Number already registered");
        }

        // Generate unique public userId
        final String userId = generateUniqueUserId();

        // Hash password
        final String hashedPassword = this.passwordEncoder.encode(rawPassword);

        Person person = new Person(
                userId,
                firstName,
                lastName,
                mobilePhoneNumber,
                emailAddress,
                userType,
                hashedPassword
        );

        // Ensure system role is USER (optional if constructor already sets it)
        person.setPersonType(PersonType.USER);

        // Validate Person entity
        final Set<ConstraintViolation<Person>> personViolations = this.validator.validate(person);
        if (!personViolations.isEmpty()) {
            throw new RuntimeException("Invalid Person instance created from request");
        }

        // Persist
        person = this.personRepository.save(person);

        // Notify via SMS if required
        if (notify) {
            final String content = String.format(
                    "You have successfully registered for the GreenRide application. " +
                            "Use your email (%s) to log in.", emailAddress);
            final boolean sent = this.smsNotificationPort.sendSms(mobilePhoneNumber, content);
            if (!sent) {
                LOGGER.warn("SMS send to {} failed!", mobilePhoneNumber);
            }
        }

        // Map to PersonView
        final PersonView personView = this.personMapper.convertPersonToPersonView(person);

        return CreatePersonResult.success(personView);
    }

    private String generateUniqueUserId() {
        String candidate;
        do {
            candidate = "GR-" + UUID.randomUUID()
                    .toString()
                    .replace("-", "")
                    .substring(0, 8)
                    .toUpperCase();
        } while (personRepository.existsByUserId(candidate));
        return candidate;
    }
}