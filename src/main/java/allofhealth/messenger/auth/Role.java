package allofhealth.messenger.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static allofhealth.messenger.auth.Permissions.*;

@RequiredArgsConstructor
public enum Role {

    // 기업형 사용자와 일반 사용자 구분 추가
    // Spring Security의 default prefix로 ROLE_ prefix가 붙게 된다. 따라서 hasAnyAuthority 메서드를 사용한다.

    ADMIN_MAIN(Set.of(POST_CREATE, POST_READ, POST_UPDATE, POST_DELETE_MINE, POST_DELETE_OTHERS, GIVE_AUTH_MAIN, GIVE_AUTH_SUB)),
    ADMIN_SUB(Set.of(POST_CREATE, POST_READ, POST_UPDATE, POST_DELETE_MINE, POST_DELETE_OTHERS, GIVE_AUTH_SUB)),
    USER_ENTERPRISE(Set.of(POST_CREATE, POST_READ, POST_UPDATE, POST_DELETE_MINE)),
    USER_INDIVIDUAL(Set.of(POST_CREATE, POST_READ, POST_UPDATE, POST_DELETE_MINE)),
    VISITOR(Set.of(POST_READ)),
    GUEST(Set.of()),
    USER_LOGGED_OUT(Set.of(LOGGED_OUT));

    @Getter
    private final Set<Permissions> permissions;

    public List<SimpleGrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = getPermissions().stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission())).collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }
}
