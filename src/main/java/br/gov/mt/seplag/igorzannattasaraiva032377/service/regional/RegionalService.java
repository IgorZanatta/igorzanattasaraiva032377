package br.gov.mt.seplag.igorzannattasaraiva032377.service.regional;

import br.gov.mt.seplag.igorzannattasaraiva032377.client.argus.ArgusRegionalClient;
import br.gov.mt.seplag.igorzannattasaraiva032377.client.argus.dto.ArgusRegionalDTO;
import br.gov.mt.seplag.igorzannattasaraiva032377.dto.regional.response.RegionalResponseDTO;
import br.gov.mt.seplag.igorzannattasaraiva032377.entity.regional.RegionalEntity;
import br.gov.mt.seplag.igorzannattasaraiva032377.repository.regional.RegionalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegionalService {

    private final RegionalRepository repository;
    private final ArgusRegionalClient client;

    @Transactional
    public void sincronizar() {
        List<ArgusRegionalDTO> externas;
        try {
            List<ArgusRegionalDTO> resposta = client.buscarRegionais();
            externas = resposta != null ? resposta : Collections.emptyList();
        } catch (Exception e) {
            log.error("Erro ao buscar regionais no Argus", e);
            throw new IllegalStateException("Falha ao sincronizar regionais a partir do Argus", e);
        }

        Map<Integer, ArgusRegionalDTO> mapExternas =
                externas.stream().collect(Collectors.toMap(
                        ArgusRegionalDTO::getId, Function.identity()
                ));

        List<RegionalEntity> locais = repository.findAll();

        // 1️⃣ Ausente → inativar
        locais.stream()
                .filter(r -> r.getAtivo() && !mapExternas.containsKey(r.getExternalId()))
                .forEach(r -> {
                    r.setAtivo(false);
                    r.setUpdatedAt(LocalDateTime.now());
                });

        // 2️⃣ Novo ou alterado
        for (ArgusRegionalDTO dto : externas) {
            Optional<RegionalEntity> ativo =
                    repository.findByExternalIdAndAtivoTrue(dto.getId());

            if (ativo.isEmpty()) {
                criarNova(dto);
            } else if (!ativo.get().getNome().equals(dto.getNome())) {
                ativo.get().setAtivo(false);
                ativo.get().setUpdatedAt(LocalDateTime.now());
                criarNova(dto);
            }
        }
    }

    private void criarNova(ArgusRegionalDTO dto) {
        repository.save(
                RegionalEntity.builder()
                        .externalId(dto.getId())
                        .nome(dto.getNome())
                        .ativo(true)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build()
        );
    }

    @Transactional(readOnly = true)
    public List<RegionalResponseDTO> listarAtivas() {
        return repository.findAllByAtivoTrue().stream()
                .map(r -> new RegionalResponseDTO(r.getId(), r.getExternalId(), r.getNome(), r.getAtivo()))
                .toList();
    }
}
