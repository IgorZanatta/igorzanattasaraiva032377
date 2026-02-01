package br.gov.mt.seplag.igorzannattasaraiva032377.controller.regional;

import br.gov.mt.seplag.igorzannattasaraiva032377.dto.regional.response.RegionalResponseDTO;
import br.gov.mt.seplag.igorzannattasaraiva032377.service.regional.RegionalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/regionais")
@RequiredArgsConstructor
public class RegionalController {

    private final RegionalService service;

    @GetMapping
    public ResponseEntity<List<RegionalResponseDTO>> listar() {
        return ResponseEntity.ok(service.listarAtivas());
    }

    // opcional, mas recomendado
    @PostMapping("/sincronizar")
    public ResponseEntity<Void> sincronizar() {
        service.sincronizar();
        return ResponseEntity.noContent().build();
    }
}