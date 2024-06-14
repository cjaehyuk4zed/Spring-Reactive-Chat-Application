package allofhealth.messenger.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MessengerReactiveUserDetailsService implements ReactiveUserDetailsService {

    /**
     * Note that a ReactiveSecurityContextRepository is not needed, as we are using JWT tokens that are stateless.
     * This means the SecurityContext is constructed on each HTTP request.
     */

    private final UserAuthRepository userAuthRepository;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return Mono.justOrEmpty(userAuthRepository.findById(username))
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found for username: " + username)))
                .map(userAuth -> (UserDetails) userAuth);
    }
}
