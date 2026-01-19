package dit.hua.gr.greenride.service.impl;

import dit.hua.gr.greenride.core.model.Person;
import dit.hua.gr.greenride.core.model.PersonType;
import dit.hua.gr.greenride.core.port.PhoneNumberPort;
import dit.hua.gr.greenride.core.port.impl.dto.PhoneNumberValidationResult;
import dit.hua.gr.greenride.core.repository.PersonRepository;
import dit.hua.gr.greenride.service.PersonService;
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
public class PersonServiceImpl implements PersonService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonServiceImpl.class);

    private final Validator validator;
    private final PasswordEncoder passwordEncoder;
    private final PersonRepository personRepository;
    private final PersonMapper personMapper;
    private final PhoneNumberPort phoneNumberPort;

    public PersonServiceImpl(final Validator validator,
                             final PasswordEncoder passwordEncoder,
                             final PersonRepository personRepository,
                             final PersonMapper personMapper,
                             final PhoneNumberPort phoneNumberPort) {

        this.validator = validator;
        this.passwordEncoder = passwordEncoder;
        this.personRepository = personRepository;
        this.personMapper = personMapper;
        this.phoneNumberPort = phoneNumberPort;
    }

    @Transactional
    @Override
    public CreatePersonResult createPerson(final CreatePersonRequest createPersonRequest, final boolean notify) {
        if (createPersonRequest == null) throw new NullPointerException("createPersonRequest is null");

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

        final String firstName = createPersonRequest.firstName().strip();
        final String lastName = createPersonRequest.lastName().strip();
        final String emailAddress = createPersonRequest.emailAddress().strip();
        String mobilePhoneNumber = createPersonRequest.mobilePhoneNumber().strip();
        final String rawPassword = createPersonRequest.rawPassword();
        final String confirmRawPassword = createPersonRequest.confirmRawPassword();

        final PersonType personType = createPersonRequest.personType();
        if (personType == null) {
            return CreatePersonResult.fail("Please select a role");
        }

        if (personType == PersonType.ADMIN) {
            return CreatePersonResult.fail("Admin registration is not allowed");
        }

        if (!rawPassword.equals(confirmRawPassword)) {
            return CreatePersonResult.fail("Password and Confirm Password do not match");
        }

        final PhoneNumberValidationResult phoneResult;
        try {
            phoneResult = this.phoneNumberPort.validate(mobilePhoneNumber);
        } catch (RestClientException ex) {
            throw new ExternalServiceUnavailableException("NOC phone validation service is unavailable", ex);
        }

        if (!phoneResult.isValidMobile()) {
            return CreatePersonResult.fail("Mobile Phone Number is not valid");
        }
        mobilePhoneNumber = phoneResult.e164();

        if (this.personRepository.existsByEmailAddress(emailAddress)) {
            return CreatePersonResult.fail("Email Address already registered");
        }

        if (this.personRepository.existsByMobilePhoneNumber(mobilePhoneNumber)) {
            return CreatePersonResult.fail("Mobile Phone Number already registered");
        }

        final String userId = generateUniqueUserId();

        final String hashedPassword = this.passwordEncoder.encode(rawPassword);

        Person person = new Person(
                userId,
                firstName,
                lastName,
                mobilePhoneNumber,
                emailAddress,
                personType,
                hashedPassword
        );

        final Set<ConstraintViolation<Person>> personViolations = this.validator.validate(person);
        if (!personViolations.isEmpty()) {
            throw new RuntimeException("Invalid Person instance created from request");
        }

        person = this.personRepository.save(person);

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