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

    public ApplicationUserDetailsService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Person person = personRepository.findByEmailAddress(username.trim())
                .orElseThrow(() -> new UsernameNotFoundException("Person with email " + username + " does not exist"));

        return new ApplicationUserDetails(person);
    }
}
