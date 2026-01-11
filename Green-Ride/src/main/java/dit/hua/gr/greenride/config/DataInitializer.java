package dit.hua.gr.greenride.config;

import dit.hua.gr.greenride.core.model.Person;
import dit.hua.gr.greenride.core.repository.PersonRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.annotation.PostConstruct;

@Configuration
public class DataInitializer {

    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(PersonRepository personRepository, PasswordEncoder passwordEncoder) {
        this.personRepository = personRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {
        if (personRepository.findByEmailAddress("admin@example.com").isEmpty()) {
            Person admin = new Person();
            admin.setUserId("admin001");
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setEmailAddress("admin@example.com");
            admin.setHashedPassword(passwordEncoder.encode("adminpass"));
            admin.setMobilePhoneNumber("6900000000");
            admin.setPersonType(dit.hua.gr.greenride.core.model.PersonType.ADMIN);

            personRepository.save(admin);
            System.out.println("✔ Admin created successfully");
        }
    }

}
