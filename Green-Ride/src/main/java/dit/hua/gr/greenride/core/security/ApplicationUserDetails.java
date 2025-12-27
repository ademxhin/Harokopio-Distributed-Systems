package dit.hua.gr.greenride.core.security;

import dit.hua.gr.greenride.core.model.PersonType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public final class ApplicationUserDetails implements UserDetails {

    private final long personId;
    private final String emailAddress;
    private final String passwordHash;
    private final PersonType type;

    public ApplicationUserDetails(final long personId,
                                  final String emailAddress,
                                  final String passwordHash,
                                  final PersonType type) {

        if (personId <= 0) throw new IllegalArgumentException("Invalid personId");
        if (emailAddress == null || emailAddress.isBlank()) throw new IllegalArgumentException("Invalid email");
        if (passwordHash == null || passwordHash.isBlank()) throw new IllegalArgumentException("Invalid password");
        if (type == null) throw new NullPointerException("PersonType is null");

        this.personId = personId;
        this.emailAddress = emailAddress;
        this.passwordHash = passwordHash;
        this.type = type;
    }

    // FIXED: Proper constructor delegation
    public ApplicationUserDetails(Long id, String emailAddress, String hashedPassword, PersonType personType) {
        this(id.longValue(), emailAddress, hashedPassword, personType);
    }

    public long personId() {
        return this.personId;
    }

    public PersonType type() {
        return this.type;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role = switch (this.type) {
            case USER -> "ROLE_USER";
            case ADMIN -> "ROLE_ADMIN";
        };
        return Collections.singletonList(new SimpleGrantedAuthority(role));
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
