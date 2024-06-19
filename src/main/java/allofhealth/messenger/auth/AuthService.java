package allofhealth.messenger.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
//
//    public boolean isPrincipal(){
//        Authentication auth = getAuthentication();
//        String principal = auth.getPrincipal().toString();
//        if(principal == null || principal.equals("anonymousUser")){
//            return false;
//        }
//        return true;
//    }
//
//    public boolean isCurrentUser(String userId){
//        String principal = getUserPrincipalOrThrow();
//        log.info("AuthService isCurrentUser : principal : {}", principal);
//        if(userId.equals(principal)){
//            return true;
//        }
//        return false;
//    }
//
//    public String getUserPrincipalOrThrow() throws AccessDeniedException {
//        Authentication auth = getAuthentication();
//        log.info("AuthService auth : {}", auth);
//        String principal = auth.getPrincipal().toString();
//        log.info("AuthService principal : {}", principal);
//        if(principal == null || principal.equals("anonymousUser")){
//            throw new AccessDeniedException("User login cannot be verified");
//        }
//        return principal;
//    }
//
//    private Authentication getAuthentication(){
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        log.info("AuthService SecurityContext : {}", SecurityContextHolder.getContext());
//        return auth;
//    }
//
//    public boolean isAdmin(){
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        boolean isAdmin = auth.getAuthorities().stream().map(s -> s.toString())
//                .anyMatch(s -> s.equals("ROLE_" + Role.ADMIN_MAIN) || s.equals("ROLE_" + Role.ADMIN_SUB));
//        return isAdmin;
//    }

    /**
     * Methods using the ReactiveSecurityContextHolder class
     * @return
     */

    public boolean isMonoPrincipal(){
        Mono<Authentication> auth = getMonoAuthentication();
        log.info("AuthService auth (from mono) : {}", auth);
        String principal = auth.map(Authentication::getName).toString();
        log.info("AuthService principal (from mono) : {}", principal);
        if(principal == null || principal.equals("anonymousUser")){
            return false;
        }
        return true;
    }

    public boolean isMonoCurrentUser(String userId){
        String principal = getUserPrincipalFromMonoOrThrow();
        log.info("AuthService isCurrentUser : principal : {}", principal);
        if(userId.equals(principal)){
            return true;
        }
        return false;
    }

    public String getUserPrincipalFromMonoOrThrow() throws AccessDeniedException {
        Mono<Authentication> auth = getMonoAuthentication();
        log.info("AuthService auth (from mono) : {}", auth);
        String principal = auth.map(Authentication::getName).toString();
        log.info("AuthService principal (from mono) : {}", principal);
        if(principal == null || principal.equals("anonymousUser")){
            throw new AccessDeniedException("User login cannot be verified");
        }
        return principal;
    }

    public Mono<String> getMonoUserPrincipalOrThrow() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getPrincipal)
                .map(Object::toString) // Ensure the principal is converted to a String
                .switchIfEmpty(Mono.error(new RuntimeException("No authenticated user found")));
    }



    private Mono<Authentication> getMonoAuthentication(){
        Mono<Authentication> auth = ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .doOnNext(authentication -> log.info("AuthService getMonoAuthentication doOnNext : Principal : {}", authentication.getPrincipal()))
                .doOnError(error -> {
                    if (error instanceof ResponseStatusException) {
                        ResponseStatusException responseStatusException = (ResponseStatusException) error;
                        if (responseStatusException.getStatusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR)) {
                            log.error("AuthService getMonoAuthentication : 500 InternalServerError : {}", error.getMessage());
                        } else {
                            log.error("AuthService getMonoAuthentication : Unexpected HTTP responseCode while getting SecurityContext : {}", error.getMessage());
                        }
                    } else {
                        log.error("AuthService getMonoAuthentication : Unexpected error while getting SecurityContext : {}", error.getMessage());
                    }
                })
                .switchIfEmpty(authenticationSwitchIfEmptyErrorLog());
        return auth;
    }

    public boolean isMonoAdmin(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream().map(s -> s.toString())
                .anyMatch(s -> s.equals("ROLE_" + Role.ADMIN_MAIN) || s.equals("ROLE_" + Role.ADMIN_SUB));
        return isAdmin;
    }

    private Mono<? extends Authentication> authenticationSwitchIfEmptyErrorLog(){
        log.info("AuthService : switchIfEmptyErrorLog");
        return Mono.error(new RuntimeException("AuthService : Mono.switchIfEmpty ERROR"));
    }
}
