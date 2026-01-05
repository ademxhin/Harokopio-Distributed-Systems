package dit.hua.gr.greenride.core.security;

import dit.hua.gr.greenride.core.model.Person;
import dit.hua.gr.greenride.core.repository.PersonRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ApplicationUserDetailsService implements UserDetailsService {

    private final PersonRepository personRepository;

    public ApplicationUserDetailsService(final PersonRepository personRepository) {
        if (personRepository == null) throw new NullPointerException("personRepository is null");
        this.personRepository = personRepository;
    }

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or blank");
        }

        final String normalized = username.trim();

        final Person person = this.personRepository
                .findByEmailAddress(normalized)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Person with email " + normalized + " does not exist")
                );

        return new ApplicationUserDetails(
                person.getId(),
                person.getEmailAddress(),
                person.getHashedPassword(),
                person.getPersonType(),
                person.getUserType()
        );
    }
}
