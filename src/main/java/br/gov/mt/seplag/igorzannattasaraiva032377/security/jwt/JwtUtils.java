package br.gov.mt.seplag.igorzannattasaraiva032377.security.jwt;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import br.gov.mt.seplag.igorzannattasaraiva032377.service.user.UserDetailsImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils {

    private static final Logger log = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    // valor em milissegundos (5 minutos = 300000)
    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    // refresh token (milissegundos) – default 30 dias se não definido
    @Value("${jwt.refresh-expiration:2592000000}")
    private long jwtRefreshExpirationMs;

    public String generateAccessToken(UserDetailsImpl userDetail) {
        return generateToken(userDetail, jwtExpirationMs, "access");
    }

    public String generateRefreshToken(UserDetailsImpl userDetail) {
        return generateToken(userDetail, jwtRefreshExpirationMs, "refresh");
    }

    private Key getSigninKey() {
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
        return key;
    }

    private String generateToken(UserDetailsImpl userDetail, long expiresInMillis, String tokenType) {
        var now = new Date();
        var expiry = new Date(now.getTime() + expiresInMillis);

        return Jwts.builder()
                .setSubject(userDetail.getUsername())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .claim("typ", tokenType)
                .claim("uid", userDetail.getId() != null ? userDetail.getId().toString() : null)
                .signWith(getSigninKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(getSigninKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(getSigninKey()).build().parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException e) {
            log.warn("Token inválido: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.warn("Token expirado: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("Token não suportado: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("Token argumento inválido: {}", e.getMessage());
        }

        return false;
    }

    public String getTokenType(String token) {
        var claims = Jwts.parser().setSigningKey(getSigninKey()).build().parseClaimsJws(token).getBody();
        Object typ = claims.get("typ");
        return typ != null ? typ.toString() : "";
    }

    public Date getExpiration(String token) {
        return Jwts.parser().setSigningKey(getSigninKey()).build().parseClaimsJws(token).getBody().getExpiration();
    }

    public UUID getUserId(String token) {
        var claims = Jwts.parser().setSigningKey(getSigninKey()).build().parseClaimsJws(token).getBody();
        Object uid = claims.get("uid");
        if (uid == null) return null;
        try {
            return UUID.fromString(uid.toString());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}