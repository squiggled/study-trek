package vttp.proj2.backend.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;

import vttp.proj2.backend.models.AccountInfoPrincipal;

@Service
public class SecurityTokenService {

    @Autowired
    UserService userSvc;
    @Value("${jwt.key.secret}")
    private String secretKey;

    public String generateToken(Authentication auth) {
        AccountInfoPrincipal accPrincipal = (AccountInfoPrincipal) auth.getPrincipal();
        Instant now = Instant.now();
        String scope = auth.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        return JWT.create()
                .withIssuer("Study Trek")
                .withIssuedAt(now)
                .withSubject(String.valueOf(accPrincipal.getUsername()))
                .withExpiresAt(now.plus(1, ChronoUnit.DAYS))
                .withClaim("userId", auth.getName())
                .withClaim("scope", scope)
                .sign(Algorithm.HMAC256(secretKey));
    }

    public String generateTokenWithUpdatedRoles(Authentication auth) {
        AccountInfoPrincipal accPrincipal = (AccountInfoPrincipal) auth.getPrincipal();

        Instant now = Instant.now();

        // Fetch updated roles for the user
        String role = userSvc.getRolesById(accPrincipal.getUserId());
        String scope = role;

        return JWT.create()
                .withIssuer("Study Trek")
                .withIssuedAt(now)
                .withSubject(String.valueOf(accPrincipal.getUsername()))
                .withExpiresAt(now.plus(1, ChronoUnit.DAYS))
                .withClaim("userId", auth.getName())
                .withClaim("scope", scope)
                .sign(Algorithm.HMAC256(secretKey));
    }

}