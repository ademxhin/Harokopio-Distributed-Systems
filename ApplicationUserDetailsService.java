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
        if (personRepository == null) throw new NullPointerException();
        this.personRepository = personRepository;
    }

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        if (username == null) throw new NullPointerException();
        if (username.isBlank()) throw new IllegalArgumentException();
        final Person person = this.personRepository
                .findByEmailAddress(username)
                .orElse(null);
        if (person == null) {
            throw new UsernameNotFoundException("person with emailAddress" + username + " does not exist");
        }
        return new ApplicationUserDetails(
                person.getId(),
                person.getEmailAddress(),
                person.getHashedPassword(),
                person.getPersonType()
        );

    }
}