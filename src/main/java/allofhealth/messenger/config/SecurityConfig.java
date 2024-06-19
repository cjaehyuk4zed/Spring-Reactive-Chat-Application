package allofhealth.messenger.config;

import allofhealth.messenger.auth.JwtAuthenticationFilter;
//import allofhealth.messenger.auth.RedirectHandlingFilter;
import allofhealth.messenger.auth.RedirectHandlingFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationEntryPoint;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;

import static allofhealth.messenger.constants.AuthHeaderConstants.*;

@Configuration
@RequiredArgsConstructor
@EnableWebFluxSecurity
@Slf4j
public class SecurityConfig {

    private static final String[] SWAGGER_WHITELIST = {
            // Swagger UI v3
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/favicon.ico",
    };


    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ReactiveAuthenticationManager authenticationManager;
    private final RedirectHandlingFilter redirectHandlingFilter;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        log.info("SecurityConfig SecurityWebFilterChain : ____________________________________________");

        http.csrf(csrf -> csrf.disable())
                .cors(corsSpec -> corsSpec.disable())
                .httpBasic(httpBasicSpec -> httpBasicSpec.disable())
                .formLogin(formLoginSpec -> formLoginSpec.disable());

        http.authorizeExchange(exchange -> exchange
                        .pathMatchers(SWAGGER_WHITELIST).permitAll()
                        .pathMatchers("/api/**").authenticated()
                        .anyExchange().permitAll())
                .addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .addFilterAfter(redirectHandlingFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .authenticationManager(authenticationManager)
                .securityContextRepository(new WebSessionServerSecurityContextRepository())
                .exceptionHandling(exceptionHandling ->
                         exceptionHandling.authenticationEntryPoint(new RedirectServerAuthenticationEntryPoint(LOGIN_REDIRECT_URI)))
                .logout(logoutSpec -> logoutSpec.disable());

        return http.build();
    }
}