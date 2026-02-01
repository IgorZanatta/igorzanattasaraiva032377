package br.gov.mt.seplag.igorzannattasaraiva032377.service.regional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import br.gov.mt.seplag.igorzannattasaraiva032377.client.argus.ArgusRegionalClient;
import br.gov.mt.seplag.igorzannattasaraiva032377.client.argus.dto.ArgusRegionalDTO;
import br.gov.mt.seplag.igorzannattasaraiva032377.dto.regional.response.RegionalResponseDTO;
import br.gov.mt.seplag.igorzannattasaraiva032377.entity.regional.RegionalEntity;
import br.gov.mt.seplag.igorzannattasaraiva032377.repository.regional.RegionalRepository;

@ExtendWith(MockitoExtension.class)
class RegionalServiceTest {

    @Mock
    private RegionalRepository regionalRepository;

    @Mock
    private ArgusRegionalClient argusRegionalClient;

    @InjectMocks
    private RegionalService regionalService;

    @Test
    void sincronizar_shouldInactivateLocalsMissingInExternalList() {
        ArgusRegionalDTO dto1 = new ArgusRegionalDTO();
        dto1.setId(1);
        dto1.setNome("Regional 1");

        when(argusRegionalClient.buscarRegionais()).thenReturn(Collections.singletonList(dto1));

        RegionalEntity activeToKeep = RegionalEntity.builder()
            .id(UUID.randomUUID())
                .externalId(1)
                .nome("Regional 1")
                .ativo(true)
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build();

        RegionalEntity activeToInactivate = RegionalEntity.builder()
            .id(UUID.randomUUID())
                .externalId(2)
                .nome("Regional 2")
                .ativo(true)
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build();

        when(regionalRepository.findAll()).thenReturn(Arrays.asList(activeToKeep, activeToInactivate));

        regionalService.sincronizar();

        assertTrue(activeToKeep.getAtivo());
        assertFalse(activeToInactivate.getAtivo());
        assertNotNull(activeToInactivate.getUpdatedAt());

        verify(regionalRepository).findAll();
        verify(regionalRepository, never()).findByExternalIdAndAtivoTrue(2);
    }

    @Test
    void sincronizar_shouldCreateNewWhenNotExistsActive() {
        ArgusRegionalDTO dto = new ArgusRegionalDTO();
        dto.setId(10);
        dto.setNome("Nova Regional");

        when(argusRegionalClient.buscarRegionais()).thenReturn(Collections.singletonList(dto));
        when(regionalRepository.findAll()).thenReturn(Collections.emptyList());
        when(regionalRepository.findByExternalIdAndAtivoTrue(10)).thenReturn(Optional.empty());

        regionalService.sincronizar();

        ArgumentCaptor<RegionalEntity> captor = ArgumentCaptor.forClass(RegionalEntity.class);
        verify(regionalRepository).save(captor.capture());
        RegionalEntity saved = captor.getValue();

        assertEquals(10, saved.getExternalId());
        assertEquals("Nova Regional", saved.getNome());
        assertTrue(saved.getAtivo());
        assertNotNull(saved.getCreatedAt());
        assertNotNull(saved.getUpdatedAt());
    }

    @Test
    void sincronizar_shouldDeactivateOldAndCreateNewWhenNameChanged() {
        ArgusRegionalDTO dto = new ArgusRegionalDTO();
        dto.setId(5);
        dto.setNome("Nome Atualizado");

        when(argusRegionalClient.buscarRegionais()).thenReturn(Collections.singletonList(dto));

        RegionalEntity existing = RegionalEntity.builder()
            .id(UUID.randomUUID())
                .externalId(5)
                .nome("Nome Antigo")
                .ativo(true)
                .createdAt(LocalDateTime.now().minusDays(2))
                .updatedAt(LocalDateTime.now().minusDays(2))
                .build();

        when(regionalRepository.findAll()).thenReturn(Collections.singletonList(existing));
        when(regionalRepository.findByExternalIdAndAtivoTrue(5)).thenReturn(Optional.of(existing));

        regionalService.sincronizar();

        assertFalse(existing.getAtivo());
        assertNotNull(existing.getUpdatedAt());

        ArgumentCaptor<RegionalEntity> captor = ArgumentCaptor.forClass(RegionalEntity.class);
        verify(regionalRepository).save(captor.capture());
        RegionalEntity created = captor.getValue();

        assertEquals(5, created.getExternalId());
        assertEquals("Nome Atualizado", created.getNome());
        assertTrue(created.getAtivo());
    }

    @Test
    void sincronizar_shouldWrapClientExceptionInIllegalState() {
        when(argusRegionalClient.buscarRegionais()).thenThrow(new RuntimeException("Falha externa"));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> regionalService.sincronizar());
        assertTrue(ex.getMessage().contains("Falha ao sincronizar"));
    }

    @Test
    void listarAtivas_shouldMapEntitiesToResponseDTOs() {
        RegionalEntity e1 = RegionalEntity.builder()
            .id(UUID.randomUUID())
                .externalId(10)
                .nome("R1")
                .ativo(true)
                .build();

        RegionalEntity e2 = RegionalEntity.builder()
            .id(UUID.randomUUID())
                .externalId(20)
                .nome("R2")
                .ativo(true)
                .build();

        when(regionalRepository.findAllByAtivoTrue()).thenReturn(List.of(e1, e2));

        List<RegionalResponseDTO> result = regionalService.listarAtivas();

        assertEquals(2, result.size());
        RegionalResponseDTO first = result.get(0);
        assertEquals(e1.getId(), first.getId());
        assertEquals(e1.getExternalId(), first.getExternalId());
        assertEquals(e1.getNome(), first.getNome());
        assertEquals(e1.getAtivo(), first.getAtivo());
    }
}
