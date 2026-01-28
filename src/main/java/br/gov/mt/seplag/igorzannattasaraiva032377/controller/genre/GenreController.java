package br.gov.mt.seplag.igorzannattasaraiva032377.controller.genre;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.gov.mt.seplag.igorzannattasaraiva032377.dto.genre.request.GenreRequestDTO;
import br.gov.mt.seplag.igorzannattasaraiva032377.dto.genre.response.GenreResponseDTO;
import br.gov.mt.seplag.igorzannattasaraiva032377.service.genre.GenreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/genres")
@RequiredArgsConstructor
public class GenreController {

    private final GenreService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GenreResponseDTO create(@Valid @RequestBody GenreRequestDTO dto) {
        return service.create(dto);
    }

    @GetMapping("/{id}")
    public GenreResponseDTO findById(@PathVariable UUID id) {
        return service.findById(id);
    }

    @GetMapping
    public List<GenreResponseDTO> findAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false, defaultValue = "asc") String sort
    ) {
        if (name != null) {
            return service.findByName(name, sort);
        }
        return service.findAll();
    }

    @PutMapping("/{id}")
    public GenreResponseDTO update(
            @PathVariable UUID id,
            @Valid @RequestBody GenreRequestDTO dto
    ) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate(@PathVariable UUID id) {
        service.deactivate(id);
    }
}
