package dit.hua.gr.greenride.core.security;

import dit.hua.gr.greenride.core.model.Person;
import dit.hua.gr.greenride.core.model.PersonType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public record ApplicationUserDetails(Person person) implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        PersonType type = person.getPersonType();
        if (type == null) return List.of();
        return List.of(new SimpleGrantedAuthority("ROLE_" + type.name()));
    }

    @Override public String getPassword() { return person.getHashedPassword(); }
    @Override public String getUsername() { return person.getEmailAddress(); }

    @Override public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() {
        return person == null || !person.isBanned();
    }

    @Override public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() {
        return person == null || !person.isBanned();
    }
}