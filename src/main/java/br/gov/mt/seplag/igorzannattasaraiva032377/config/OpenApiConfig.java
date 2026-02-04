package br.gov.mt.seplag.igorzannattasaraiva032377.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;


@OpenAPIDefinition(
    info = @Info(
        title = "API SEPLAG MT - Cadastro de Artistas e Álbuns",
        version = "1.0.0",
        description = """
            API REST profissional para gerenciamento de artistas, álbuns, gêneros musicais e capas de álbuns.
            
            **Funcionalidades principais:**
            - Cadastro e consulta de artistas (solo ou bandas)
            - Gerenciamento de álbuns com paginação
            - Upload e listagem de capas de álbuns (integração MinIO)
            - Cadastro de gêneros musicais
            - Consulta de regionais (integração externa)
            - Autenticação JWT com access e refresh tokens
            - Sistema de auditoria completo
            
            **Autenticação:**
            - Endpoints públicos: `/api/v1/auth/login` e `/api/v1/auth/refresh`
            - Endpoints protegidos: Requerem header `Authorization: Bearer {accessToken}`
            - Tokens JWT com expiração configurável
            """,
        contact = @Contact(
            name = "SEPLAG MT - Equipe de Desenvolvimento",
            email = "zanatta2014@outlook.com"
        )
    )
)
@SecurityScheme(
    name = "bearerAuth",
    description = """
        Autenticação via JSON Web Token (JWT).
        
        **Como usar:**
        1. Obtenha o token através do endpoint `/api/v1/auth/login`
        2. Inclua o token no header: `Authorization: Bearer {accessToken}`
        3. Renove o token usando `/api/v1/auth/refresh` com o refreshToken
        
        **Observações:**
        - Access token: curta duração, usado nas requisições
        - Refresh token: longa duração, usado apenas para renovação
        """,
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    in = SecuritySchemeIn.HEADER
)
@Configuration
public class OpenApiConfig {
}
