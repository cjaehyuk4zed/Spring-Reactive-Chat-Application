package allofhealth.messenger.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessengerReactiveUserDetailsService implements ReactiveUserDetailsService {

    /**
     * Note that a ReactiveSecurityContextRepository is not needed, as we are using JWT tokens that are stateless.
     * This means the SecurityContext is constructed on each HTTP request.
     */

    private final UserAuthRepository userAuthRepository;

//    @Override
//    public Mono<UserDetails> findByUsername(String username) {
//        log.info("UserDetailsService : findByUsername : {}", username);
//        Mono<User_Auth> userMonoUsingWhen = userAuthRepository.findById(username);
////                .flatMap(userDetails -> {
////                    log.info("MessengerReactiveUserDetailsService flatMap userDetails : {}", userDetails);
////                    UserDetails newUser = User_Auth(user);
////                    return Mono.just(userDetails);
////                });
//        log.info("MessengerReactiveUserDetailsService Mono<User_Auth> : {}", userMonoUsingWhen);
//        Mono<UserDetails> monoUserDetails = userMonoUsingWhen.map(userDetails -> (UserDetails) userDetails);
//        log.info("MessengerReactiveUserDetailsService Mono<UserDetails> : {}", monoUserDetails);
//        return Mono.justOrEmpty(monoUserDetails)
//                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found for username: " + username)))
//                .map(monoUserDetails.)
//                .doOnNext(userDetails -> log.info("Found user : {}", userDetails))
//                .doOnError(error -> log.error("Error finding user : {} {}", error.getMessage(), error));
//    }

    /**
     *
     * @param username the username to look up
     * @return
     *
     * doOnNext method runs after the mapping is complete (i.e. after `Connection close succeed`).
     * It is used to trigger side effects such as logging or statistics.
     */
    @Override
    public Mono<UserDetails> findByUsername(String username) {
        log.info("MessengerReactiveUserDetailsService : findByUsername : {}", username);
        Mono<User_Auth> userMono = userAuthRepository.findById(username);
        log.info("MessengerReactiveUserDetailsService : Mono<User_Auth> : {}", userMono);

        return userMono.map(userAuth -> (UserDetails) userAuth)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("User not found for username: " + username)))
                .doOnNext(userDetails -> log.info("MessengerReactiveUserDetailsService : doOnNext Found user : {}", userDetails))
                .doOnError(error -> log.error("MessengerReactiveUserDetailsService : doOnError Error finding user : {} {}", error.getMessage(), error));
    }

}
