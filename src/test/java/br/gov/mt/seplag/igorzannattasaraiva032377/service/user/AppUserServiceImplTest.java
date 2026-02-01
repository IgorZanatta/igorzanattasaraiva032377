package br.gov.mt.seplag.igorzannattasaraiva032377.service.user;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import br.gov.mt.seplag.igorzannattasaraiva032377.dto.user.request.CreateAppUserRequest;
import br.gov.mt.seplag.igorzannattasaraiva032377.dto.user.request.UpdateAppUserRequest;
import br.gov.mt.seplag.igorzannattasaraiva032377.dto.user.request.UpdatePasswordRequest;
import br.gov.mt.seplag.igorzannattasaraiva032377.dto.user.response.AppUserResponse;
import br.gov.mt.seplag.igorzannattasaraiva032377.entity.user.AppUserEntity;
import br.gov.mt.seplag.igorzannattasaraiva032377.exception.ConflictException;
import br.gov.mt.seplag.igorzannattasaraiva032377.exception.ResourceNotFoundException;
import br.gov.mt.seplag.igorzannattasaraiva032377.mapper.user.AppUserMapper;
import br.gov.mt.seplag.igorzannattasaraiva032377.repository.user.AppUserRepository;
import br.gov.mt.seplag.igorzannattasaraiva032377.service.auditLog.AuditLogService;

@ExtendWith(MockitoExtension.class)
class AppUserServiceImplTest {

    @Mock
    private AppUserRepository repository;

    @Mock
    private AppUserMapper mapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private AppUserServiceImpl appUserService;

    @Test
    void create_shouldPersistUserAndLogAudit() {
        CreateAppUserRequest request = new CreateAppUserRequest(" User ", "USER@Example.com ", "pass");

        when(repository.existsByEmail("user@example.com")).thenReturn(false);
        when(passwordEncoder.encode("pass")).thenReturn("encoded");

        AppUserEntity saved = new AppUserEntity();
        saved.setId(UUID.randomUUID());
        saved.setName("User");
        saved.setEmail("user@example.com");
        saved.setActive(true);

        when(repository.save(any(AppUserEntity.class))).thenReturn(saved);

        AppUserResponse response = new AppUserResponse(saved.getId(), saved.getName(), saved.getEmail(), saved.isActive(), null, null, null);
        when(mapper.toResponse(saved)).thenReturn(response);

        AppUserResponse result = appUserService.create(request);

        assertEquals(response, result);
        verify(repository).existsByEmail("user@example.com");
        verify(passwordEncoder).encode("pass");
        verify(auditLogService).log(eq("AppUserEntity"), eq(saved.getId().toString()), eq("CREATE"), isNull(), eq(response));
    }

    @Test
    void create_shouldThrowWhenEmailAlreadyUsed() {
        CreateAppUserRequest request = new CreateAppUserRequest("User", "user@example.com", "pass");
        when(repository.existsByEmail("user@example.com")).thenReturn(true);

        assertThrows(ConflictException.class, () -> appUserService.create(request));
    }

    @Test
    void findById_shouldReturnResponse() {
        UUID id = UUID.randomUUID();
        AppUserEntity entity = new AppUserEntity();
        entity.setId(id);
        entity.setName("User");
        entity.setEmail("user@example.com");
        entity.setActive(true);

        when(repository.findById(id)).thenReturn(Optional.of(entity));
        AppUserResponse response = new AppUserResponse(id, "User", "user@example.com", true, null, null, null);
        when(mapper.toResponse(entity)).thenReturn(response);

        AppUserResponse result = appUserService.findById(id);
        assertEquals(response, result);
    }

    @Test
    void findById_shouldThrowWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> appUserService.findById(id));
    }

    @Test
    void findByEmail_shouldNormalizeAndReturnResponse() {
        AppUserEntity entity = new AppUserEntity();
        entity.setId(UUID.randomUUID());
        entity.setEmail("user@example.com");
        AppUserResponse response = new AppUserResponse(entity.getId(), "User", "user@example.com", true, null, null, null);

        when(repository.findByEmail("user@example.com")).thenReturn(Optional.of(entity));
        when(mapper.toResponse(entity)).thenReturn(response);

        AppUserResponse result = appUserService.findByEmail(" USER@EXAMPLE.COM ");
        assertEquals(response, result);
    }

    @Test
    void listAll_shouldMapToResponses() {
        AppUserEntity entity = new AppUserEntity();
        entity.setId(UUID.randomUUID());
        entity.setEmail("user@example.com");
        entity.setName("User");
        entity.setActive(true);

        when(repository.findAll()).thenReturn(Collections.singletonList(entity));
        AppUserResponse response = new AppUserResponse(entity.getId(), "User", "user@example.com", true, null, null, null);
        when(mapper.toResponse(entity)).thenReturn(response);

        List<AppUserResponse> result = appUserService.listAll();
        assertEquals(1, result.size());
        assertEquals(response, result.get(0));
    }

    @Test
    void update_shouldApplyChangesAndLogAudit() {
        UUID id = UUID.randomUUID();
        AppUserEntity entity = new AppUserEntity();
        entity.setId(id);
        entity.setName("Old");
        entity.setEmail("old@example.com");
        entity.setActive(true);

        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(repository.existsByEmail("new@example.com")).thenReturn(false);
        when(repository.save(any(AppUserEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        AppUserResponse before = new AppUserResponse(id, "Old", "old@example.com", true, null, null, null);
        AppUserResponse after = new AppUserResponse(id, "New", "new@example.com", false, null, null, null);
        when(mapper.toResponse(entity)).thenReturn(before, after);

        UpdateAppUserRequest request = new UpdateAppUserRequest(" New ", " NEW@example.com ", false);

        AppUserResponse result = appUserService.update(id, request);

        assertEquals(after, result);
        assertEquals("New", entity.getName());
        assertEquals("new@example.com", entity.getEmail());
        assertFalse(entity.isActive());

        verify(auditLogService).log(eq("AppUserEntity"), eq(id.toString()), eq("UPDATE"), eq(before), eq(after));
    }

    @Test
    void update_shouldThrowWhenEmailAlreadyUsedByOther() {
        UUID id = UUID.randomUUID();
        AppUserEntity entity = new AppUserEntity();
        entity.setId(id);
        entity.setEmail("old@example.com");

        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(repository.existsByEmail("new@example.com")).thenReturn(true);

        UpdateAppUserRequest request = new UpdateAppUserRequest(null, "new@example.com", null);
        assertThrows(ConflictException.class, () -> appUserService.update(id, request));
    }

    @Test
    void updatePassword_shouldEncodeAndLog() {
        UUID id = UUID.randomUUID();
        AppUserEntity entity = new AppUserEntity();
        entity.setId(id);

        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(passwordEncoder.encode("new"))
                .thenReturn("encoded-new");

        appUserService.updatePassword(id, new UpdatePasswordRequest("new"));

        assertEquals("encoded-new", entity.getPasswordHash());
        verify(repository).save(entity);
        verify(auditLogService).log("AppUserEntity", id.toString(), "UPDATE_PASSWORD", null, null);
    }

    @Test
    void deactivate_shouldSetInactiveAndLog() {
        UUID id = UUID.randomUUID();
        AppUserEntity entity = new AppUserEntity();
        entity.setId(id);
        entity.setActive(true);

        when(repository.findById(id)).thenReturn(Optional.of(entity));

        appUserService.deactivate(id);

        assertFalse(entity.isActive());
        verify(repository).save(entity);
        verify(auditLogService).log("AppUserEntity", id.toString(), "DEACTIVATE", null, null);
    }

    @Test
    void recordLogin_shouldSetLastLoginAndLog() {
        UUID id = UUID.randomUUID();
        AppUserEntity entity = new AppUserEntity();
        entity.setId(id);

        when(repository.findById(id)).thenReturn(Optional.of(entity));

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

        appUserService.recordLogin(id, null);

        assertNotNull(entity.getLastLogin());
        verify(repository).save(entity);
    }
}
