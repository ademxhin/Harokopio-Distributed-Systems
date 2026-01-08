package dit.hua.gr.greenride.core.security;

import dit.hua.gr.greenride.core.model.Person;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public final class CurrentUserProvider {

    public Optional<CurrentUser> getCurrentUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            return Optional.empty();
        }

        if (authentication.getPrincipal() instanceof ApplicationUserDetails userDetails) {

            Person person = userDetails.getPerson();

            return Optional.of(
                    new CurrentUser(
                            person.getId(),
                            person.getEmailAddress(),
                            person.getPersonType()
                    )
            );
        }

        return Optional.empty();
    }
}
