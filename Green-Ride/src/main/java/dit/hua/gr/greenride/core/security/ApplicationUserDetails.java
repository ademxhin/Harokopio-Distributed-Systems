package dit.hua.gr.greenride.core.security;

import dit.hua.gr.greenride.core.model.PersonType;
import dit.hua.gr.greenride.core.model.UserType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class ApplicationUserDetails implements UserDetails {

    private final long personId;
    private final String emailAddress;
    private final String passwordHash;
    private final PersonType type;
    private final UserType userType; // may be null for ADMIN

    public ApplicationUserDetails(final long personId,
                                  final String emailAddress,
                                  final String passwordHash,
                                  final PersonType type,
                                  final UserType userType) {

        if (personId <= 0) throw new IllegalArgumentException("Invalid personId");
        if (emailAddress == null || emailAddress.isBlank()) throw new IllegalArgumentException("Invalid email");
        if (passwordHash == null || passwordHash.isBlank()) throw new IllegalArgumentException("Invalid password");
        if (type == null) throw new NullPointerException("PersonType is null");

        this.personId = personId;
        this.emailAddress = emailAddress;
        this.passwordHash = passwordHash;
        this.type = type;
        this.userType = userType;
    }

    public ApplicationUserDetails(final Long id,
                                  final String emailAddress,
                                  final String hashedPassword,
                                  final PersonType personType,
                                  final UserType userType) {
        this(id.longValue(), emailAddress, hashedPassword, personType, userType);
    }

    public long personId() {
        return this.personId;
    }

    public PersonType type() {
        return this.type;
    }

    public UserType userType() {
        return this.userType;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        final List<GrantedAuthority> authorities = new ArrayList<>();

        if (this.type == PersonType.ADMIN) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            return authorities;
        }

        // optional general role
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        if (this.userType != null) {
            if (this.userType.isPassenger()) {
                authorities.add(new SimpleGrantedAuthority("ROLE_PASSENGER"));
            }
            if (this.userType.isDriver()) {
                authorities.add(new SimpleGrantedAuthority("ROLE_DRIVER"));
            }
        }

        return authorities;
    }

    @Override
    public String getPassword() {
        return this.passwordHash;
    }

    @Override
    public String getUsername() {
        return this.emailAddress;
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
