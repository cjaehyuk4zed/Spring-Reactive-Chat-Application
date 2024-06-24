package allofhealth.messenger.auth;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashMap;

/**
 * Jwt 인증을 위해 활용하는 메서드들
 * JwtServiceImpl.java에서 실제 로직이 구현되어 있다
 */

public interface JwtService {
    public boolean isTokenValid(String token, String clientIp, UserDetails userDetails);

    public String extractUsername(String token);

    public String generateToken(UserDetails userDetails);

    public <K, V> String generateToken(HashMap<K,V> extraClaims, UserDetails userDetails);

    public String generateRefreshToken(UserDetails userDetails);

    public <K, V> String generateRefreshToken(HashMap<K,V> extraClaims, UserDetails userDetails);

}
