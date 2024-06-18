package allofhealth.messenger.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static allofhealth.messenger.constants.DirectoryMapConstants.*;

/**
 * Any edits made here should be reflected in the platform app as well!
 */


@Slf4j
@Service
public class JwtServiceImpl implements JwtService{

    // secretKey exists in Base64 encoded format - decode using getSignInKey() method before usage
    // Edit values in application.properties
    @Value("${spring.security.jwt.secret-key}")
    private String secretKey;

    @Value("${spring.security.jwt.expiration}")
    private Long jwtExpiration;

    @Value("${spring.security.jwt.refresh-token.expiration}")
    private Long refreshExpiration;

    // Note that UserDetails instances will actually contain an object of UserInfoDetails (a.k.a custom implementation of UserDetails)
    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSignInKey()).build().parseClaimsJws(token).getBody();
    }

    // Decode secretKey into regular String before usage
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public String generateToken(UserDetails userDetails){ return generateToken(new HashMap<>(), userDetails); }

    @Override
    public <K, V> String generateToken(HashMap<K,V> extraClaims, UserDetails userDetails) {
        return buildToken((Map<String, Object>) extraClaims, userDetails, jwtExpiration);
    }

    @Override
    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails, refreshExpiration);
    }

    @Override
    public <K, V> String generateRefreshToken(HashMap<K,V> extraClaims, UserDetails userDetails) {
        return buildToken((Map<String, Object>) extraClaims, userDetails, jwtExpiration);
    }

    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        return Jwts.builder().setClaims(extraClaims).setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256).compact();
    }

//    @Override
//    public boolean isTokenValid(String token, UserDetails userDetails){
//        final String username = extractUsername(token);
//        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
//    }

    /**
     * Token validation method!! Uses the JWT ISSUER and SUBJECT claims along with the Username
     * JWT SUBJECT is set as the Username `setSubject(userDetails.getUsername())` in the buildToken() method above
     * @param token
     * @param clientIp
     * @param userDetails
     * @return
     */
    @Override
    public boolean isTokenValid(String token, String clientIp, UserDetails userDetails){
        final String username = extractUsername(token);
        log.info("JwtServiceImpl isTokenValid : \n\t - Subject (Username) : {} \n\t - UserDetails Username : {} \n\t - Audience : {} \n\t - Issuer : {}", username, userDetails.getUsername(), extractClaim(token, Claims::getAudience), extractClaim(token, Claims::getIssuer));
//        log.info("UserDetails Username : " + userDetails.getUsername());
//        log.info("Subject : " + extractClaim(token, Claims::getAudience));
//        log.info("Issuer : " + extractClaim(token, Claims::getIssuer));
        return (username.equals(userDetails.getUsername())) && extractClaim(token, Claims::getAudience).equals(PLATFORM_SERVER_IP_ADDR)
                && extractClaim(token, Claims::getIssuer).equals(PLATFORM_SERVER_SOCKET_ADDR) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token){return extractExpiration(token).before(new Date());}

    private Date extractExpiration(String token) { return extractClaim(token, Claims::getExpiration);}

}