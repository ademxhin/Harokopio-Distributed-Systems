package dit.hua.gr.greenride.web.ui;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;

/**
 * Authentication utilities for UI controllers.
 */
public final class AuthUtils {

    private AuthUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * Checks whether the provided authentication
     * represents an authenticated user.
     *
     * @param auth the Spring Security Authentication object
     * @return true if the user is authenticated, false otherwise
     */
    public static boolean isAuthenticated(final Authentication auth) {
        if (auth == null) return false;
        if (auth instanceof AnonymousAuthenticationToken) return false;
        return auth.isAuthenticated();
    }

    /**
     * Checks whether the provided authentication
     * represents an anonymous (non-authenticated) user.
     *
     * @param auth the Spring Security Authentication object
     * @return true if the user is anonymous, false otherwise
     */
    public static boolean isAnonymous(final Authentication auth) {
        if (auth == null) return true;
        if (auth instanceof AnonymousAuthenticationToken) return true;
        return !auth.isAuthenticated();
    }
}
