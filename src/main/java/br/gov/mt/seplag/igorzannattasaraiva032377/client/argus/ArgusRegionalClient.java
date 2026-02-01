package br.gov.mt.seplag.igorzannattasaraiva032377.client.argus;

import br.gov.mt.seplag.igorzannattasaraiva032377.client.argus.dto.ArgusRegionalDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ArgusRegionalClient {

    private final WebClient.Builder webClientBuilder;

    public List<ArgusRegionalDTO> buscarRegionais() {
        WebClient client = webClientBuilder.build();

        List<ArgusRegionalDTO> resposta = client.get()
                .uri("https://integrador-argus-api.geia.vip/v1/regionais")
                .retrieve()
                .bodyToFlux(ArgusRegionalDTO.class)
                .collectList()
                .block();

        return resposta != null ? resposta : List.of();
    }
}