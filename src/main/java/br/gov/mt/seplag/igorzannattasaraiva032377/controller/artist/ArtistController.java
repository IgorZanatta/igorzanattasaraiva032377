package br.gov.mt.seplag.igorzannattasaraiva032377.controller.artist;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.gov.mt.seplag.igorzannattasaraiva032377.dto.artist.request.ArtistRequestDTO;
import br.gov.mt.seplag.igorzannattasaraiva032377.dto.artist.response.ArtistResponseDTO;
import br.gov.mt.seplag.igorzannattasaraiva032377.entity.artist.ArtistType;
import br.gov.mt.seplag.igorzannattasaraiva032377.service.artist.ArtistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/artists")
@RequiredArgsConstructor
@Tag(name = "Artists", description = "Operações para cadastro, listagem e atualização de artistas.")
public class ArtistController {

    private final ArtistService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Criar artista", description = "Cria um novo artista com nome, tipo (SOLO/BAND) e lista opcional de gêneros por nome.")
    public ArtistResponseDTO create(@RequestBody @Valid ArtistRequestDTO dto) {
        return service.create(dto);
    }

    @GetMapping
    @Operation(summary = "Listar artistas", description = "Lista artistas com filtros opcionais por nome (parcial), tipo e ordenação alfabética asc/desc.")
    public List<ArtistResponseDTO> findAll(
            @Parameter(description = "Filtro parcial por nome do artista (case-insensitive)")
            @RequestParam(required = false) String name,
            @Parameter(description = "Filtro por tipo de artista: SOLO ou BAND")
            @RequestParam(required = false) ArtistType type,
            @Parameter(description = "Direção da ordenação alfabética por nome: asc (padrão) ou desc")
            @RequestParam(required = false, defaultValue = "asc") String sort
    ) {
        return service.findAll(name, type, sort);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar artista", description = "Atualiza nome, tipo e conjunto de gêneros de um artista existente.")
    public ArtistResponseDTO update(
            @Parameter(description = "ID do artista a ser atualizado")
            @PathVariable UUID id,
            @RequestBody @Valid ArtistRequestDTO dto
    ) {
        return service.update(id, dto);
    }
}