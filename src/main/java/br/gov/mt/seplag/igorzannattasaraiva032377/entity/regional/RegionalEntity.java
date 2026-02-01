package br.gov.mt.seplag.igorzannattasaraiva032377.entity.regional;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "regional")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegionalEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "external_id", nullable = false)
    private Integer externalId;

    @Column(nullable = false, length = 200)
    private String nome;

    @Column(nullable = false)
    private Boolean ativo;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
