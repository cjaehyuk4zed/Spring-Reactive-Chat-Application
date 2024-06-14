package allofhealth.messenger.config;

import allofhealth.messenger.auth.JwtAuthenticationFilter;
import allofhealth.messenger.auth.MessengerReactiveUserDetailsService;
import allofhealth.messenger.auth.UserAuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;

@Configuration
@RequiredArgsConstructor
public class SecurityBeansConfig {

    private final UserAuthRepository userAuthRepository;
    private final MessengerReactiveUserDetailsService userDetailsService;

    /**
     * Note that Reactive Spring Security does not have authentication providers
     * (unlike the non-reactive Spring Security which has an `AuthenticationManager` and an `AuthenticationProvider` class).
     * Instead, the `ReactiveAuthenticationManager` directly implements the authentication logic.
     */
    @Bean
    public ReactiveAuthenticationManager authenticationManager() {
        UserDetailsRepositoryReactiveAuthenticationManager authenticationManager =
                new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
        return authenticationManager;
    }

}
