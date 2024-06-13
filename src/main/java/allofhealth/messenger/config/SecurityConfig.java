package allofhealth.messenger.config;

import allofhealth.messenger.auth.MessengerReactiveUserDetailsService;
import allofhealth.messenger.auth.UserAuthRepository;
import allofhealth.messenger.constants.DirectoryMapConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.web.server.SecurityWebFilterChain;

import static allofhealth.messenger.constants.DirectoryMapConstants.*;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    /**
     * Note that a ReactiveSecurityContextRepository is not needed, as we are using JWT tokens that are stateless
     * This means the SecurityContext is constructed on each HTTP request
     */

    private final UserAuthRepository userAuthRepository;

    @Bean
    public ReactiveUserDetailsService userDetailsService(){
        return new MessengerReactiveUserDetailsService();
    }

    /**
     * Note that Reactive Spring Security DOES NOT have authentication providers
     * (unlike the non-reactive Spring Security which has an `AuthenticationManager` and a `AuthenticationProvider` class)
     * Instead, the `ReactiveAuthenticationManager` directly implements the authentication logic
     * @return
     */
    @Bean
    public ReactiveAuthenticationManager authenticationManager(){
        UserDetailsRepositoryReactiveAuthenticationManager authenticationManager =
                new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService());
        return authenticationManager;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http){
        http.csrf((csrf) -> csrf.disable())
                .authorizeExchange(exchange -> exchange
                        .pathMatchers(CHAT_CONTROLLER)).

    }
}
