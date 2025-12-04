package dit.hua.gr.greenride.web.ui;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.security.authentication.AnonymousAuthenticationToken;

final class AuthUtils {

    private AuthUtils() {
        throw new UnsupportedOperationException();
    }

    public static boolean isAuthenticated(final Authentication authentication) {
        if(authentication == null) return false;
        if(authentication instanceof AnonymousAuthenticationToken) return false;
        return authentication.isAuthenticated();
    }

    public static boolean isAnonymous(final Authentication authentication) {
        if(authentication == null) return true;
        if(authentication instanceof AnonymousAuthenticationToken) return true;
        return !authentication.isAuthenticated();
    }
}
