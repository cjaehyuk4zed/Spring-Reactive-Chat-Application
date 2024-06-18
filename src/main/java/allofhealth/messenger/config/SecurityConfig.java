package allofhealth.messenger.config;

import allofhealth.messenger.auth.JwtAuthenticationFilter;
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
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;

import static allofhealth.messenger.constants.AuthHeaderConstants.*;
import static allofhealth.messenger.constants.DirectoryMapConstants.*;

@Configuration
@RequiredArgsConstructor
@EnableWebFluxSecurity
@Slf4j
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ReactiveAuthenticationManager authenticationManager;

//    @Bean
//    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
//    log.info("SecurityConfig SecurityWebFilterChain : ____________________________________________");
//        http.csrf(csrf -> csrf.disable());
//        return http.build();
//    }




    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
    log.info("SecurityConfig SecurityWebFilterChain : ____________________________________________");
        http.csrf(csrf -> csrf.disable())
                .cors(corsSpec -> corsSpec.disable());

        http.httpBasic(httpBasicSpec -> httpBasicSpec.disable())
                .formLogin(formLoginSpec -> formLoginSpec.disable());

        http.authorizeExchange(exchange -> exchange
                        .pathMatchers("/api/**").authenticated()
                        .anyExchange().permitAll())
                .addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .authenticationManager(authenticationManager)
                .securityContextRepository(new WebSessionServerSecurityContextRepository())
                .exceptionHandling(exceptionHandling ->
                         exceptionHandling.authenticationEntryPoint(new RedirectServerAuthenticationEntryPoint(LOGIN_REDIRECT_URI)))
                .logout(logoutSpec -> logoutSpec.disable());
        log.info("SecurityConfig finish SecurityWebFilterChain");
        return http.build();
    }






//
//    @Bean
//    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
//                                                         JwtAuthenticationFilter jwtAuthenticationFilter,
//                                                         ServerSecurityContextRepository securityContextRepository) {
//        log.info("SecurityConfig SecurityWebFilterChain : ____________________________________________");
//        http.csrf(csrf -> csrf.disable())
//                .authorizeExchange(exchange -> exchange
//                        .pathMatchers("/api/v1/chat/message").authenticated()
//                        .pathMatchers("/api/**").authenticated()
//                        .anyExchange().denyAll())
//                .exceptionHandling(exceptionHandling ->
//                        exceptionHandling.authenticationEntryPoint(new RedirectServerAuthenticationEntryPoint("/login")))
//                .addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION);
//
//        return http.build();
//    }
}