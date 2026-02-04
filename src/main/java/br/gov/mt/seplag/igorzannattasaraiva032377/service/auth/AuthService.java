package br.gov.mt.seplag.igorzannattasaraiva032377.service.auth;

import br.gov.mt.seplag.igorzannattasaraiva032377.dto.auth.JwtResponse;
import br.gov.mt.seplag.igorzannattasaraiva032377.dto.auth.LoginRequest;

public interface AuthService {

    /**
     * Autentica usuário com email e senha, retornando tokens JWT.
     *
     * @param request credenciais de login
     * @return resposta contendo access token, refresh token e dados do usuário
     */
    JwtResponse login(LoginRequest request);

    /**
     * Renova access token usando um refresh token válido.
     *
     * @param refreshToken refresh token JWT
     * @return resposta contendo novos access e refresh tokens
     */
    JwtResponse refreshToken(String refreshToken);

    /**
     * Invalida tokens adicionando-os à blacklist.
     *
     * @param accessToken access token a ser invalidado (opcional)
     * @param refreshToken refresh token a ser invalidado (opcional)
     */
    void logout(String accessToken, String refreshToken);
}
