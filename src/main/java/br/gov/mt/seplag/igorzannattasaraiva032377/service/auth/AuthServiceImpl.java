package br.gov.mt.seplag.igorzannattasaraiva032377.service.auth;

import java.time.LocalDateTime;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import br.gov.mt.seplag.igorzannattasaraiva032377.dto.auth.JwtResponse;
import br.gov.mt.seplag.igorzannattasaraiva032377.dto.auth.LoginRequest;
import br.gov.mt.seplag.igorzannattasaraiva032377.exception.BadRequestException;
import br.gov.mt.seplag.igorzannattasaraiva032377.exception.UnauthorizedException;
import br.gov.mt.seplag.igorzannattasaraiva032377.security.jwt.JwtUtils;
import br.gov.mt.seplag.igorzannattasaraiva032377.security.jwt.TokenBlacklist;
import br.gov.mt.seplag.igorzannattasaraiva032377.service.user.AppUserService;
import br.gov.mt.seplag.igorzannattasaraiva032377.service.user.UserDetailServiceImpl;
import br.gov.mt.seplag.igorzannattasaraiva032377.service.user.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final AppUserService appUserService;
    private final TokenBlacklist tokenBlacklist;
    private final UserDetailServiceImpl userDetailService;

    @Override
    public JwtResponse login(LoginRequest request) {
        log.debug("Iniciando processo de autenticação para email: {}", request.email());

        // Normaliza email para lowercase
        String normalizedEmail = request.email().trim().toLowerCase();

        // Autentica credenciais
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(normalizedEmail, request.password())
        );

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // Gera tokens JWT
        String accessToken = jwtUtils.generateAccessToken(userDetails);
        String refreshToken = jwtUtils.generateRefreshToken(userDetails);

        // Registra data/hora do login
        appUserService.recordLogin(userDetails.getId(), LocalDateTime.now());

        log.info("Usuário autenticado com sucesso: {} (ID: {})", userDetails.getUsername(), userDetails.getId());

        return new JwtResponse(
                accessToken,
                refreshToken,
                userDetails.getId(),
                userDetails.getName(),
                userDetails.getUsername()
        );
    }

    @Override
    public JwtResponse refreshToken(String refreshToken) {
        log.debug("Iniciando renovação de token");

        // Valida que o token foi fornecido
        if (!StringUtils.hasText(refreshToken)) {
            throw new BadRequestException("Refresh token é obrigatório");
        }

        // Valida o token JWT
        if (!jwtUtils.validateJwtToken(refreshToken)) {
            throw new UnauthorizedException("Refresh token inválido");
        }

        // Verifica se o token não está na blacklist
        if (tokenBlacklist.isBlacklisted(refreshToken)) {
            throw new UnauthorizedException("Refresh token foi invalidado");
        }

        // Confirma que é um refresh token
        String tokenType = jwtUtils.getTokenType(refreshToken);
        if (!"refresh".equalsIgnoreCase(tokenType)) {
            throw new UnauthorizedException("Tipo de token inválido. Esperado: refresh, recebido: " + tokenType);
        }

        // Carrega dados do usuário
        String username = jwtUtils.getUsernameFromToken(refreshToken);
        UserDetailsImpl userDetails = (UserDetailsImpl) userDetailService.loadUserByUsername(username);

        // Gera novos tokens
        String newAccessToken = jwtUtils.generateAccessToken(userDetails);
        String newRefreshToken = jwtUtils.generateRefreshToken(userDetails);

        // Blacklista o refresh token antigo (token rotation para maior segurança)
        var expiration = jwtUtils.getExpiration(refreshToken);
        if (expiration != null) {
            tokenBlacklist.blacklist(refreshToken, expiration.getTime());
            log.debug("Refresh token antigo adicionado à blacklist");
        }

        log.info("Token renovado com sucesso para usuário: {} (ID: {})", userDetails.getUsername(), userDetails.getId());

        return new JwtResponse(
                newAccessToken,
                newRefreshToken,
                userDetails.getId(),
                userDetails.getName(),
                userDetails.getUsername()
        );
    }

    @Override
    public void logout(String accessToken, String refreshToken) {
        log.debug("Iniciando processo de logout");

        int tokensBlacklisted = 0;

        // Blacklista refresh token se fornecido
        if (StringUtils.hasText(refreshToken)) {
            if (jwtUtils.validateJwtToken(refreshToken)) {
                var exp = jwtUtils.getExpiration(refreshToken);
                if (exp != null) {
                    tokenBlacklist.blacklist(refreshToken, exp.getTime());
                    tokensBlacklisted++;
                    log.debug("Refresh token adicionado à blacklist");
                }
            }
        }

        // Blacklista access token se fornecido
        if (StringUtils.hasText(accessToken)) {
            if (jwtUtils.validateJwtToken(accessToken)) {
                var exp = jwtUtils.getExpiration(accessToken);
                if (exp != null) {
                    tokenBlacklist.blacklist(accessToken, exp.getTime());
                    tokensBlacklisted++;
                    log.debug("Access token adicionado à blacklist");
                }
            }
        }

        log.info("Logout concluído. Tokens invalidados: {}", tokensBlacklisted);
    }
}
