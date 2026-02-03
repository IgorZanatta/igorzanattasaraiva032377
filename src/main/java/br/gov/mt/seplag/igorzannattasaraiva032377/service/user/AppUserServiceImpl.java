package br.gov.mt.seplag.igorzannattasaraiva032377.service.user;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.mt.seplag.igorzannattasaraiva032377.entity.user.AppUserEntity;
import br.gov.mt.seplag.igorzannattasaraiva032377.exception.ResourceNotFoundException;
import br.gov.mt.seplag.igorzannattasaraiva032377.repository.user.AppUserRepository;
import br.gov.mt.seplag.igorzannattasaraiva032377.service.auditLog.AuditLogService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppUserServiceImpl implements AppUserService {

    private final AppUserRepository repository;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public void recordLogin(UUID id, LocalDateTime loginAt) {
        var entity = getEntityOrThrow(id);
        entity.setLastLogin(loginAt != null ? loginAt : LocalDateTime.now());
        repository.save(entity);

        auditLogService.log(
            "AppUserEntity",
            entity.getId().toString(),
            "RECORD_LOGIN",
            null,
            null
        );
    }

    private AppUserEntity getEntityOrThrow(UUID id) {
        return repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
