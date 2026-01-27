package br.gov.mt.seplag.igorzannattasaraiva032377.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import br.gov.mt.seplag.igorzannattasaraiva032377.security.jwt.AuthEntryPointJwt;
import br.gov.mt.seplag.igorzannattasaraiva032377.security.jwt.AuthFilterToken;
import br.gov.mt.seplag.igorzannattasaraiva032377.security.jwt.JwtUtils;
import br.gov.mt.seplag.igorzannattasaraiva032377.service.user.UserDetailServiceImpl;



@Configuration
@EnableMethodSecurity
public class WebSecurityConfig {

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserDetailServiceImpl userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public AuthFilterToken authFilterToken(JwtUtils jwtUtils, UserDetailServiceImpl userDetailsService, br.gov.mt.seplag.igorzannattasaraiva032377.security.jwt.TokenBlacklist tokenBlacklist) {
        return new AuthFilterToken(jwtUtils, userDetailsService, tokenBlacklist);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthFilterToken authFilterToken, DaoAuthenticationProvider authenticationProvider) throws Exception {

        http.cors(Customizer.withDefaults());
        http.csrf(csrf -> csrf.disable())
            .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("api/v1/auth/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/users").permitAll()
                .anyRequest().authenticated());

        http.authenticationProvider(authenticationProvider);
        http.addFilterBefore(authFilterToken, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}