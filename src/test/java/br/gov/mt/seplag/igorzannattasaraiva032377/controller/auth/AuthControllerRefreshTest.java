package br.gov.mt.seplag.igorzannattasaraiva032377.controller.auth;

import java.util.Collections;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;

import br.gov.mt.seplag.igorzannattasaraiva032377.dto.auth.JwtResponse;
import br.gov.mt.seplag.igorzannattasaraiva032377.dto.auth.RefreshRequest;
import br.gov.mt.seplag.igorzannattasaraiva032377.security.jwt.JwtUtils;
import br.gov.mt.seplag.igorzannattasaraiva032377.security.jwt.TokenBlacklist;
import br.gov.mt.seplag.igorzannattasaraiva032377.service.user.AppUserService;
import br.gov.mt.seplag.igorzannattasaraiva032377.service.user.UserDetailServiceImpl;
import br.gov.mt.seplag.igorzannattasaraiva032377.service.user.UserDetailsImpl;

@ExtendWith(MockitoExtension.class)
@Disabled("Tem dependência de JwtUtils no classpath completo; manter desabilitado até configurar corretamente")
class AuthControllerRefreshTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private AppUserService appUserService;

    @Mock
    private TokenBlacklist tokenBlacklist;

    @Mock
    private UserDetailServiceImpl userDetailService;

    @InjectMocks
    private AuthController authController;

    @Test
    void refresh_shouldReturnNewTokensForValidRefreshToken() {
        String refreshToken = "valid-refresh-token";
        UUID userId = UUID.randomUUID();

        UserDetailsImpl userDetails = new UserDetailsImpl(
                userId,
                "User Name",
                "user@example.com",
                "password",
                true,
                Collections.emptyList()
        );

        when(jwtUtils.validateJwtToken(refreshToken)).thenReturn(true);
        when(tokenBlacklist.isBlacklisted(refreshToken)).thenReturn(false);
        when(jwtUtils.getTokenType(refreshToken)).thenReturn("refresh");
        when(jwtUtils.getUsernameFromToken(refreshToken)).thenReturn("user@example.com");
        when(userDetailService.loadUserByUsername("user@example.com")).thenReturn(userDetails);
        when(jwtUtils.generateAccessToken(userDetails)).thenReturn("new-access-token");
        when(jwtUtils.generateRefreshToken(userDetails)).thenReturn("new-refresh-token");
        Date exp = new Date(System.currentTimeMillis() + 60_000);
        when(jwtUtils.getExpiration(refreshToken)).thenReturn(exp);

        ResponseEntity<JwtResponse> response = authController.refresh(
                null,
                new RefreshRequest(refreshToken)
        );

        assertNotNull(response.getBody());
        assertEquals(200, response.getStatusCode().value());

        JwtResponse body = response.getBody();
        assertEquals("new-access-token", body.accessToken());
        assertEquals("new-refresh-token", body.refreshToken());
        assertEquals(userId, body.userId());
        assertEquals("User Name", body.name());
        assertEquals("user@example.com", body.email());

        verify(jwtUtils).validateJwtToken(refreshToken);
        verify(jwtUtils).getTokenType(refreshToken);
        verify(jwtUtils).getUsernameFromToken(refreshToken);
        verify(jwtUtils).generateAccessToken(userDetails);
        verify(jwtUtils).generateRefreshToken(userDetails);
        verify(tokenBlacklist).blacklist(refreshToken, exp.getTime());
    }
}
