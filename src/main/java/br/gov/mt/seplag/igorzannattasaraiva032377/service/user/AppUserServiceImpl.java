package br.gov.mt.seplag.igorzannattasaraiva032377.service.user;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppUserServiceImpl implements AppUserService {

    private final AppUserRepository repository;
    private final AppUserMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public AppUserResponse create(CreateAppUserRequest request) {
        var email = request.email().trim().toLowerCase();

        if (repository.existsByEmail(email)) {
            throw new ConflictException("E-mail já está em uso");
        }

        var entity = AppUserEntity.builder()
                .name(request.name().trim())
                .email(email)
                .passwordHash(passwordEncoder.encode(request.password()))
                .active(true)
                .build();

        var saved = repository.save(entity);
        var response = mapper.toResponse(saved);

        auditLogService.log(
            "AppUserEntity",
            saved.getId().toString(),
            "CREATE",
            null,
            response
        );

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public AppUserResponse findById(UUID id) {
        return mapper.toResponse(getEntityOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public AppUserResponse findByEmail(String email) {
        var normalized = email.trim().toLowerCase();

        var entity = repository.findByEmail(normalized)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return mapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppUserResponse> listAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public AppUserResponse update(UUID id, UpdateAppUserRequest request) {
        var entity = getEntityOrThrow(id);
        var before = mapper.toResponse(entity);

        if (request.name() != null) {
            entity.setName(request.name().trim());
        }

        if (request.email() != null) {
            var email = request.email().trim().toLowerCase();
            if (!email.equals(entity.getEmail()) && repository.existsByEmail(email)) {
                throw new ConflictException("E-mail já está em uso");
            }
            entity.setEmail(email);
        }

        if (request.active() != null) {
            entity.setActive(request.active());
        }

        var saved = repository.save(entity);
        var after = mapper.toResponse(saved);

        auditLogService.log(
            "AppUserEntity",
            saved.getId().toString(),
            "UPDATE",
            before,
            after
        );

        return after;
    }

    @Override
    @Transactional
    public void updatePassword(UUID id, UpdatePasswordRequest request) {
        var entity = getEntityOrThrow(id);
        entity.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        repository.save(entity);

        auditLogService.log(
            "AppUserEntity",
            entity.getId().toString(),
            "UPDATE_PASSWORD",
            null,
            null
        );
    }

    @Override
    @Transactional
    public void deactivate(UUID id) {
        var entity = getEntityOrThrow(id);
        entity.setActive(false);
        repository.save(entity);

        auditLogService.log(
            "AppUserEntity",
            entity.getId().toString(),
            "DEACTIVATE",
            null,
            null
        );
    }

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
