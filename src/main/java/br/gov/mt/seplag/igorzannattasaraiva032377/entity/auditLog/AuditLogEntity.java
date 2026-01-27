package br.gov.mt.seplag.igorzannattasaraiva032377.entity.auditLog;

import br.gov.mt.seplag.igorzannattasaraiva032377.entity.user.AppUserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "audit_log")
public class AuditLogEntity {

    @Id
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "entity_name", length = 255, nullable = false)
    private String entityName;

    @Column(name = "entity_id", length = 255, nullable = false)
    private String entityId;

    @Column(name = "action", length = 50, nullable = false)
    private String action;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "old_data", columnDefinition = "jsonb")
    private String oldData;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "new_data", columnDefinition = "jsonb")
    private String newData;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performed_by")
    private AppUserEntity performedBy;

    @Column(name = "performed_at", nullable = false)
    private LocalDateTime performedAt;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @PrePersist
    void prePersist() {
        if (this.performedAt == null) this.performedAt = LocalDateTime.now();
    }
}
