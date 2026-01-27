package br.gov.mt.seplag.igorzannattasaraiva032377.controller.auth;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.gov.mt.seplag.igorzannattasaraiva032377.dto.auth.JwtResponse;
import br.gov.mt.seplag.igorzannattasaraiva032377.dto.auth.LoginRequest;
import br.gov.mt.seplag.igorzannattasaraiva032377.dto.auth.RefreshRequest;
import br.gov.mt.seplag.igorzannattasaraiva032377.exception.BadRequestException;
import br.gov.mt.seplag.igorzannattasaraiva032377.exception.UnauthorizedException;
import br.gov.mt.seplag.igorzannattasaraiva032377.security.jwt.JwtUtils;
import br.gov.mt.seplag.igorzannattasaraiva032377.security.jwt.TokenBlacklist;
import br.gov.mt.seplag.igorzannattasaraiva032377.service.user.AppUserService;
import br.gov.mt.seplag.igorzannattasaraiva032377.service.user.UserDetailServiceImpl;
import br.gov.mt.seplag.igorzannattasaraiva032377.service.user.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final AppUserService appUserService;
    private final TokenBlacklist tokenBlacklist;
    private final UserDetailServiceImpl userDetailService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest request) {
        var normalizedEmail = request.email().trim().toLowerCase();
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(normalizedEmail, request.password()));

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String access = jwtUtils.generateAccessToken(userDetails);
        String refresh = jwtUtils.generateRefreshToken(userDetails);

        appUserService.recordLogin(userDetails.getId(), LocalDateTime.now());

        var response = new JwtResponse(access, refresh, userDetails.getId(), userDetails.getName(), userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refresh(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody(required = false) RefreshRequest request) {

        String refreshToken = null;
        if (request != null && StringUtils.hasText(request.refreshToken())) {
            refreshToken = request.refreshToken().trim();
        }
        // Se não veio no body, tenta pegar do header Authorization (Bearer <refreshToken>)
        if (!StringUtils.hasText(refreshToken) && authHeader != null && authHeader.startsWith("Bearer ")) {
            refreshToken = authHeader.substring("Bearer ".length()).trim();
        }

        if (!StringUtils.hasText(refreshToken)) {
            throw new BadRequestException("Refresh token é obrigatório");
        }

        if (!jwtUtils.validateJwtToken(refreshToken) || tokenBlacklist.isBlacklisted(refreshToken)) {
            throw new UnauthorizedException("Refresh token inválido");
        }

        if (!"refresh".equalsIgnoreCase(jwtUtils.getTokenType(refreshToken))) {
            throw new UnauthorizedException("Tipo de token inválido");
        }

        var username = jwtUtils.getUsernameFromToken(refreshToken);
        var userDetails = (UserDetailsImpl) userDetailService.loadUserByUsername(username);

        String newAccess = jwtUtils.generateAccessToken(userDetails);
        String newRefresh = jwtUtils.generateRefreshToken(userDetails);

        // opcional: blacklisting do refresh anterior (rotation)
        var exp = jwtUtils.getExpiration(refreshToken);
        if (exp != null) {
            tokenBlacklist.blacklist(refreshToken, exp.getTime());
        }

        var response = new JwtResponse(newAccess, newRefresh, userDetails.getId(), userDetails.getName(), userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader(value = "Authorization", required = false) String authHeader,
                   @RequestBody(required = false) RefreshRequest request) {
        // blacklista refresh se enviado (body ou nada)
        if (request != null && StringUtils.hasText(request.refreshToken())) {
            var refreshToken = request.refreshToken().trim();
            if (jwtUtils.validateJwtToken(refreshToken)) {
                var exp = jwtUtils.getExpiration(refreshToken);
                if (exp != null) {
                    tokenBlacklist.blacklist(refreshToken, exp.getTime());
                }
            }
        }

        // blacklista access se enviado no header
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            var access = authHeader.substring("Bearer ".length()).trim();
            if (jwtUtils.validateJwtToken(access)) {
                var exp = jwtUtils.getExpiration(access);
                if (exp != null) {
                    tokenBlacklist.blacklist(access, exp.getTime());
                }
            }
        }

        return ResponseEntity.noContent().build();
    }
}
