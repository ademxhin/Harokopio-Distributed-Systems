package dit.hua.gr.greenride.core.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;

@Service
public class JwtService {

    private final Key key;
    private final String issuer;
    private final String audience;
    private final long ttlMinutes;

    public JwtService(@Value("${app.jwt.secret}") final String secret,
                      @Value("${app.jwt.issuer}") final String issuer,
                      @Value("${app.jwt.audience}") final String audience,
                      @Value("${app.jwt.ttl-minutes}") final long ttlMinutes) {

        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.issuer = issuer;
        this.audience = audience;
        this.ttlMinutes = ttlMinutes;
    }

    public String issue(final String subject, final Collection<String> roles) {
        final Instant now = Instant.now();

        return Jwts.builder()
                .setSubject(subject)
                .setIssuer(this.issuer)
                .setAudience(this.audience)
                .claim("roles", roles)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(Duration.ofMinutes(this.ttlMinutes))))
                .signWith(this.key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parse(final String token) {
        return Jwts.parserBuilder()
                .requireIssuer(this.issuer)
                .requireAudience(this.audience)
                .setSigningKey(this.key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
