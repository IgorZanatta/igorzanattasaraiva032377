package br.gov.mt.seplag.igorzannattasaraiva032377.controller.genre;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
}
