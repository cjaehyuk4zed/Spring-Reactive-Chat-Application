package allofhealth.messenger.constants;

import static allofhealth.messenger.constants.DirectoryMapConstants.*;

public class AuthHeaderConstants {

    // Prevent instantiation of this class
    private AuthHeaderConstants(){}

    public static final String USER_NOT_FOUND = "User not found";

    //HTTP Bearer Authentication - Originally for OAuth2, but is widely used for JWT as well
    public static final String BEARER = "Bearer";

    public static final String LOGIN_REDIRECT_URI = PLATFORM_SERVER_IP + AUTH_CONTROLLER + "/login";
}
