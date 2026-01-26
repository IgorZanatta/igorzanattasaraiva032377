package br.gov.mt.seplag.igorzannattasaraiva032377.security.jwt;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import br.gov.mt.seplag.igorzannattasaraiva032377.service.user.UserDetailServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AuthFilterToken extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(AuthFilterToken.class);

    private final JwtUtils jwtUtil;
    private final UserDetailServiceImpl userDetailService;
    private final TokenBlacklist tokenBlacklist;

    public AuthFilterToken(JwtUtils jwtUtil, UserDetailServiceImpl userDetailService, TokenBlacklist tokenBlacklist) {
        this.jwtUtil = jwtUtil;
        this.userDetailService = userDetailService;
        this.tokenBlacklist = tokenBlacklist;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = getToken(request);
            if (jwt != null && jwtUtil.validateJwtToken(jwt) && SecurityContextHolder.getContext().getAuthentication() == null) {
                if (!"access".equalsIgnoreCase(jwtUtil.getTokenType(jwt))) {
                    filterChain.doFilter(request, response);
                    return;
                }

                if (tokenBlacklist.isBlacklisted(jwt)) {
                    filterChain.doFilter(request, response);
                    return;
                }

                String username = jwtUtil.getUsernameFromToken(jwt);

                UserDetails userDetails = userDetailService.loadUserByUsername(username);
                var auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(auth);
            }

        } catch (Exception e) {
            log.error("Erro ao processar o token JWT", e);
        }

        filterChain.doFilter(request, response);
    }

    private String getToken(HttpServletRequest request) {
        String headerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(headerToken) && headerToken.startsWith("Bearer ")) {
            return headerToken.replace("Bearer ", "");
        }
        return null;
    }

}