package br.gov.mt.seplag.igorzannattasaraiva032377.service.user;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import br.gov.mt.seplag.igorzannattasaraiva032377.entity.user.AppUserEntity;
import br.gov.mt.seplag.igorzannattasaraiva032377.repository.user.AppUserRepository;
import br.gov.mt.seplag.igorzannattasaraiva032377.service.auditLog.AuditLogService;

@ExtendWith(MockitoExtension.class)
class AppUserServiceImplTest {

    @Mock
    private AppUserRepository repository;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private AppUserServiceImpl appUserService;

    @Test
    void recordLogin_shouldSetLastLoginAndLog() {
        UUID id = UUID.randomUUID();
        AppUserEntity entity = new AppUserEntity();
        entity.setId(id);

        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(repository.save(any(AppUserEntity.class))).thenReturn(entity);

        LocalDateTime now = LocalDateTime.now();
        appUserService.recordLogin(id, now);

        assertEquals(now, entity.getLastLogin());
        verify(repository).save(entity);
        verify(auditLogService).log("AppUserEntity", id.toString(), "RECORD_LOGIN", null, null);
    }

    @Test
    void recordLogin_shouldUseNowWhenNullPassed() {
        UUID id = UUID.randomUUID();
        AppUserEntity entity = new AppUserEntity();
        entity.setId(id);

        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(repository.save(any(AppUserEntity.class))).thenReturn(entity);

        appUserService.recordLogin(id, null);

        assertNotNull(entity.getLastLogin());
        verify(repository).save(entity);
    }
}
