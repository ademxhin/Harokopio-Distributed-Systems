package dit.hua.gr.greenride.core.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;

    public JwtAuthenticationFilter(final JwtService jwtService) {
        if (jwtService == null) throw new NullPointerException("jwtService is null");
        this.jwtService = jwtService;
    }

    private void writeError(final HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"invalid_token\"}");
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        final String path = request.getServletPath();

        if (!path.startsWith("/api/")) return true;
        if (path.equals("/api/auth/login")) return true;
        if (path.equals("/api/auth/register")) return true;
        if (path.equals("/api/v1/auth/client-tokens")) return true;

        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void doFilterInternal(final HttpServletRequest request,
                                    final HttpServletResponse response,
                                    final FilterChain filterChain)
            throws ServletException, IOException {

        LOGGER.info("JWT filter hit path={} authHeaderPresent={}",
                request.getServletPath(),
                request.getHeader("Authorization") != null);

        final String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            this.writeError(response);   // 401 + {"error":"invalid_token"}
            return;
        }

        final String token = authorizationHeader.substring(7);

        try {
            final Claims claims = this.jwtService.parse(token);
            final String subject = claims.getSubject();
            final Collection<String> roles = (Collection<String>) claims.get("roles");

            final var authorities =
                    roles == null
                            ? List.<GrantedAuthority>of()
                            : roles.stream()
                            .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                            .map(SimpleGrantedAuthority::new)
                            .toList();

            final User principal = new User(subject, "", authorities);

            final UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(principal, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            LOGGER.info("JWT auth set for subject={} authorities={}", subject, authorities);

        } catch (Exception ex) {
            LOGGER.warn("JwtAuthenticationFilter failed", ex);
            this.writeError(response);
            return;
        }

        filterChain.doFilter(request, response);
    }
}