package br.gov.mt.seplag.igorzannattasaraiva032377.controller.auth;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import br.gov.mt.seplag.igorzannattasaraiva032377.dto.auth.JwtResponse;
import br.gov.mt.seplag.igorzannattasaraiva032377.dto.auth.RefreshRequest;
import br.gov.mt.seplag.igorzannattasaraiva032377.service.auth.AuthService;

@ExtendWith(MockitoExtension.class)
class AuthControllerRefreshTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    void refresh_shouldReturnNewTokensForValidRefreshToken() {
        String refreshToken = "valid-refresh-token";
        UUID userId = UUID.randomUUID();

        JwtResponse expectedResponse = new JwtResponse(
                "new-access-token",
                "new-refresh-token",
                userId,
                "User Name",
                "user@example.com"
        );

        when(authService.refreshToken(refreshToken)).thenReturn(expectedResponse);

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

        verify(authService).refreshToken(refreshToken);
    }
}
