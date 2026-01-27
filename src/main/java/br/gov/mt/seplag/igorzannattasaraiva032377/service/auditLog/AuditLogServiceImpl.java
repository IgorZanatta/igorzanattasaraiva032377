package br.gov.mt.seplag.igorzannattasaraiva032377.service.auditLog;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.gov.mt.seplag.igorzannattasaraiva032377.entity.auditLog.AuditLogEntity;
import br.gov.mt.seplag.igorzannattasaraiva032377.entity.user.AppUserEntity;
import br.gov.mt.seplag.igorzannattasaraiva032377.repository.auditLog.AuditLogRepository;
import br.gov.mt.seplag.igorzannattasaraiva032377.repository.user.AppUserRepository;
import br.gov.mt.seplag.igorzannattasaraiva032377.service.user.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final AppUserRepository appUserRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void log(String entityName, String entityId, String action, Object oldData, Object newData) {
        try {
            var builder = AuditLogEntity.builder()
                    .entityName(entityName)
                    .entityId(entityId)
                    .action(action)
                    .oldData(toJson(oldData))
                    .newData(toJson(newData));

            // usuário autenticado
            getCurrentUser().ifPresent(builder::performedBy);

            // dados da requisição (IP, User-Agent)
            HttpServletRequest request = currentRequest();
            if (request != null) {
                builder.ipAddress(request.getRemoteAddr());
                builder.userAgent(request.getHeader("User-Agent"));
            }

            auditLogRepository.save(builder.build());
        } catch (Exception e) {
            // Auditoria nunca deve quebrar o fluxo principal
            log.warn("Falha ao registrar auditoria para {} {}: {}", entityName, entityId, e.getMessage(), e);
        }
    }

    private String toJson(Object data) throws JsonProcessingException {
        if (data == null) {
            return null;
        }
        return objectMapper.writeValueAsString(data);
    }

    private Optional<AppUserEntity> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetailsImpl userDetails)) {
            return Optional.empty();
        }

        return appUserRepository.findById(userDetails.getId());
    }

    private HttpServletRequest currentRequest() {
        var attributes = RequestContextHolder.getRequestAttributes();
        if (attributes instanceof ServletRequestAttributes servletAttributes) {
            return servletAttributes.getRequest();
        }
        return null;
    }
}
