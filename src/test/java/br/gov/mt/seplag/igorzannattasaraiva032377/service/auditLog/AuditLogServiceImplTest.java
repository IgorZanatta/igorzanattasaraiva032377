package br.gov.mt.seplag.igorzannattasaraiva032377.service.auditLog;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.gov.mt.seplag.igorzannattasaraiva032377.entity.auditLog.AuditLogEntity;
import br.gov.mt.seplag.igorzannattasaraiva032377.entity.user.AppUserEntity;
import br.gov.mt.seplag.igorzannattasaraiva032377.repository.auditLog.AuditLogRepository;
import br.gov.mt.seplag.igorzannattasaraiva032377.repository.user.AppUserRepository;
import br.gov.mt.seplag.igorzannattasaraiva032377.service.user.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
class AuditLogServiceImplTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private AuditLogServiceImpl auditLogService;

    @AfterEach
    void cleanup() {
        SecurityContextHolder.clearContext();
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void log_shouldPersistAuditWithUserAndRequestData() throws Exception {
        UUID userId = UUID.randomUUID();
        UserDetailsImpl principal = new UserDetailsImpl(userId, "User", "user@example.com", "pwd", true, Collections.emptyList());
        var auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        AppUserEntity userEntity = new AppUserEntity();
        userEntity.setId(userId);
        when(appUserRepository.findById(userId)).thenReturn(Optional.of(userEntity));

        RequestAttributes attrs = new ServletRequestAttributes(request);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(request.getHeader("User-Agent")).thenReturn("JUnit");
        RequestContextHolder.setRequestAttributes(attrs);

        when(objectMapper.writeValueAsString(any())).thenReturn("{json}");

        auditLogService.log("Entity", "123", "CREATE", new Object(), new Object());

        ArgumentCaptor<AuditLogEntity> captor = ArgumentCaptor.forClass(AuditLogEntity.class);
        verify(auditLogRepository).save(captor.capture());
        AuditLogEntity saved = captor.getValue();

        assertEquals("Entity", saved.getEntityName());
        assertEquals("123", saved.getEntityId());
        assertEquals("CREATE", saved.getAction());
        assertEquals(userEntity, saved.getPerformedBy());
        assertEquals("127.0.0.1", saved.getIpAddress());
        assertEquals("JUnit", saved.getUserAgent());
        assertEquals("{json}", saved.getOldData());
        assertEquals("{json}", saved.getNewData());
    }

    @Test
    void log_shouldNotThrowWhenSomethingFails() throws Exception {
        when(objectMapper.writeValueAsString(any())).thenThrow(new RuntimeException("fail"));

        assertDoesNotThrow(() -> auditLogService.log("Entity", "1", "ACTION", new Object(), new Object()));
        // no save expected because serialization failed before persisting
        verify(auditLogRepository, never()).save(any());
    }
}
