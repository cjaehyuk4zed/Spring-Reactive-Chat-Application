package allofhealth.messenger.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Permissions {
    POST_CREATE("post:create"),
    POST_READ("post:read"),
    POST_UPDATE("post:update"),
    POST_DELETE_MINE("post:delete_mine"),
    POST_DELETE_OTHERS("post:delete_others"),
    GIVE_AUTH_MAIN("give_auth:main"),
    GIVE_AUTH_SUB("give_auth:sub"),
    LOGGED_OUT("give_auth:logged_out");

    @Getter
    private final String permission;
}
