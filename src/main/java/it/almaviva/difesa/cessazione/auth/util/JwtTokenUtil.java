package it.almaviva.difesa.cessazione.auth.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import it.almaviva.difesa.cessazione.auth.model.CustomUserDetail;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtTokenUtil implements Serializable {

    private static final long serialVersionUID = -5486146463276878351L;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    public String getUsername(String token) {
        return getClaim(token, Claims::getSubject);
    }

    public Date getExpirationDate(String token) {
        return getClaim(token, Claims::getExpiration);
    }

    public <T> T getClaim(String token, Function<Claims, T> claimsResolver) {
        final var claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(CustomUserDetail customUserDetail) {
        Map<String, Object> claims = new HashMap<>();

        List<String> authorities = customUserDetail.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        /*
            CLAIMS
         */
        claims.put("uuid", customUserDetail.getUuid());
        claims.put("fiscalCode", customUserDetail.getUsername());
        claims.put("firstName", customUserDetail.getFirstName());
        claims.put("lastName", customUserDetail.getLastName());
        claims.put("rank", customUserDetail.getRankDescription());
        claims.put("auth", authorities);
        claims.put("employeeId", customUserDetail.getEmployeeId());
        claims.put("andipId", customUserDetail.getEmployeeId());

        return doGenerateToken(claims, customUserDetail.getUsername());
    }

    public String generateRefreshToken() {
        return UUID.randomUUID().toString();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        var tokenAuthorities = (ArrayList<String>) getAllClaimsFromToken(token).get("auth");
        var userDBAuthorities = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        Set<String> tokenAuthoritiesSet = new TreeSet<>(tokenAuthorities);
        Set<String> userDBAuthoritiesSet = new TreeSet<>(userDBAuthorities);

        var sameAuthorities = tokenAuthoritiesSet.equals(userDBAuthoritiesSet);

        final String username = getUsername(token);
        return (sameAuthorities && username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(this.secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date((new Date()).getTime() + expiration * 1000))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        final var expirationDate = getExpirationDate(token);
        return expirationDate.before(new Date());
    }
}