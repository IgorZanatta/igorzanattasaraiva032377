package br.gov.mt.seplag.igorzannattasaraiva032377.security.jwt;

import java.util.Collections;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import br.gov.mt.seplag.igorzannattasaraiva032377.service.user.UserDetailsImpl;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;

class JwtUtilsTest {

    private JwtUtils jwtUtils;

    private String base64Secret;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();

        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        base64Secret = Encoders.BASE64.encode(key.getEncoded());

        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", base64Secret);
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 300_000L);
        ReflectionTestUtils.setField(jwtUtils, "jwtRefreshExpirationMs", 3_600_000L);
    }

    @Test
    void generateAccessToken_shouldProduceValidTokenWithExpectedClaims() {
        UUID userId = UUID.randomUUID();
        UserDetailsImpl userDetails = new UserDetailsImpl(
                userId,
                "User Name",
                "user@example.com",
                "password",
                true,
                Collections.emptyList()
        );

        String token = jwtUtils.generateAccessToken(userDetails);

        assertNotNull(token);
        assertTrue(jwtUtils.validateJwtToken(token));
        assertEquals("user@example.com", jwtUtils.getUsernameFromToken(token));
        assertEquals("access", jwtUtils.getTokenType(token));
        assertEquals(userId, jwtUtils.getUserId(token));

        Date expiration = jwtUtils.getExpiration(token);
        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    void validateJwtToken_shouldReturnFalseForExpiredToken() {
        var now = new Date();
        var expired = new Date(now.getTime() - 60_000);

        var key = Keys.hmacShaKeyFor(io.jsonwebtoken.io.Decoders.BASE64.decode(base64Secret));

        String expiredToken = Jwts.builder()
                .setSubject("user@example.com")
                .setIssuedAt(new Date(now.getTime() - 120_000))
                .setExpiration(expired)
                .claim("typ", "access")
                .claim("uid", UUID.randomUUID().toString())
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        boolean valid = jwtUtils.validateJwtToken(expiredToken);

        assertFalse(valid, "Expired token must be considered invalid");
    }
}
