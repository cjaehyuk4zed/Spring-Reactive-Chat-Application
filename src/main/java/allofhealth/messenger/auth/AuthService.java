package allofhealth.messenger.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    public boolean isPrincipal(){
        Authentication auth = getAuthentication();
        String principal = auth.getPrincipal().toString();
        if(principal == null || principal.equals("anonymousUser")){
            return false;
        }
        return true;
    }

    public boolean isCurrentUser(String userId){
        String principal = getUserPrincipalOrThrow();
        log.info("AuthService isCurrentUser : principal : {}", principal);
        if(userId.equals(principal)){
            return true;
        }
        return false;
    }

    public String getUserPrincipalOrThrow() throws AccessDeniedException {
        Authentication auth = getAuthentication();
        String principal = auth.getPrincipal().toString();
        if(principal == null || principal.equals("anonymousUser")){
            throw new AccessDeniedException("User login cannot be verified");
        }
        return principal;
    }

    private Authentication getAuthentication(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth;
    }

    public boolean isAdmin(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream().map(s -> s.toString())
                .anyMatch(s -> s.equals("ROLE_" + Role.ADMIN_MAIN) || s.equals("ROLE_" + Role.ADMIN_SUB));
        return isAdmin;
    }

}
