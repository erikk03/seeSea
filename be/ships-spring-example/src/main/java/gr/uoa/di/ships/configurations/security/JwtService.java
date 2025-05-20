package gr.uoa.di.ships.configurations.security;

import gr.uoa.di.ships.persistence.model.RegisteredUser;
import gr.uoa.di.ships.services.implementation.UserDetailsServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.function.Function;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  private final SecretKey secretKey;
  private final Long tokenExpirationHours;
  private final UserDetailsServiceImpl userDetailsService;

  public JwtService(@Value("${crypto.base64TokenKey}") String secretKeyStr,
                    @Value("${crypto.tokenExpirationHours}") Long tokenExpirationHours,
                    UserDetailsServiceImpl userDetailsService) {
    this.userDetailsService = userDetailsService;
    secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKeyStr));
    this.tokenExpirationHours = tokenExpirationHours;
  }

  public String generateToken(String username) {
    return createToken(username);
  }

  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public boolean validateToken(String token, UserDetails userDetails) {
    final String userName = extractUsername(token);
    return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
  }

  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
    final Claims claims = extractAllClaims(token);
    return claimResolver.apply(claims);
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser()
        .verifyWith(secretKey)
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }

  private String createToken(String subject) {
    RegisteredUser registeredUser = (RegisteredUser) userDetailsService.loadUserByUsername(subject);
    return Jwts.builder()
        .subject(subject)
        .claim("userId", registeredUser.getId())
        .claim("role", registeredUser.getRole().getName())
        .claim("email", registeredUser.getEmail())
        .claim("username", registeredUser.getUsername())
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + millisToHours(tokenExpirationHours)))
        .signWith(secretKey)
        .compact();
  }

  private Long millisToHours(Long millis) {
    return millis * 1000 * 3600;
  }
}