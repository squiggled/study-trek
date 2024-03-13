package vttp.proj2.backend.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;

import vttp.proj2.backend.models.AccountInfoPrincipal;

@Service
public class SecurityTokenService {
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
                .withExpiresAt(now.plus(1, ChronoUnit.HOURS))
                .withClaim("name", auth.getName())
                .withClaim("scope", scope)
                .sign(Algorithm.HMAC256(secretKey));
    }
}