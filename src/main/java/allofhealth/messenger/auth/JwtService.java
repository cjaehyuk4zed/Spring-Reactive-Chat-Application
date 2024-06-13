package allofhealth.messenger.auth;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashMap;

public interface JwtService {
    public boolean isTokenValid(String token, String clientIp, UserDetails userDetails);

    public String extractUsername(String token);

    public String generateToken(UserDetails userDetails);

    public <K, V> String generateToken(HashMap<K,V> extraClaims, UserDetails userDetails);

    public String generateRefreshToken(UserDetails userDetails);

    public <K, V> String generateRefreshToken(HashMap<K,V> extraClaims, UserDetails userDetails);

}
