package dit.hua.gr.greenride.core.security;

import dit.hua.gr.greenride.core.model.Person;
import dit.hua.gr.greenride.core.model.PersonType;
import dit.hua.gr.greenride.core.model.UserType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class ApplicationUserDetails implements UserDetails {

    private final Person person;

    public ApplicationUserDetails(Person person) {
        this.person = person;
    }

    public Person getPerson() {
        return this.person;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        List<GrantedAuthority> authorities = new ArrayList<>();

        PersonType type = person.getPersonType();
        UserType userType = person.getUserType();

        if (type == PersonType.ADMIN) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            return authorities;
        }

        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        if (userType != null) {
            if (userType.isPassenger()) {
                authorities.add(new SimpleGrantedAuthority("ROLE_PASSENGER"));
            }
            if (userType.isDriver()) {
                authorities.add(new SimpleGrantedAuthority("ROLE_DRIVER"));
            }
        }

        return authorities;
    }

    @Override public String getPassword() { return person.getHashedPassword(); }
    @Override public String getUsername() { return person.getEmailAddress(); }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
