package allofhealth.messenger.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import static allofhealth.messenger.constants.AuthHeaderConstants.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements WebFilter {

    private final JwtService jwtService;
    private final ReactiveUserDetailsService userDetailsService;

    /**
     * Process the Web request and (optionally) delegate to the next
     * {@code WebFilter} through the given {@link WebFilterChain}.
     *
     * @param exchange the current server exchange
     * @param chain    provides a way to delegate to the next filter
     * @return {@code Mono<Void>} to indicate when request processing is complete
     *
     * the `Void` class is an uninstantiable placeholder class to hold a reference to the `Class` object
     * representing the Java keyword void.
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        log.info("___________________________________________________________________");
        log.info("Begin JWTFilter _____________________________________________");
        log.info("Requested URL : {}", exchange.getRequest().getURI());

        final String accessToken;
        final String userId;
        final String clientIp = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
        final String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

//         Check if JWT token is in "Bearer Authentication" format
        if(authHeader == null || !authHeader.startsWith(BEARER)){
            log.info("JwtFilter : Authorization header is either null or doesn't contain HTTP Bearer auth");
            return loginRedirect(exchange);
        }

        // Remove "Bearer " header and get the JWT token
        accessToken = authHeader.split(" ")[1].trim();
        userId = jwtService.extractUsername(accessToken);
        log.info("JwtFilter : JWT token received, userId = {}", userId);

        Mono<UserDetails> userDetailsMono = this.userDetailsService.findByUsername(userId);
        log.info("JWTFilter : userDetailsMono : {}", userDetailsMono);

        return userDetailsMono
                .flatMap(userDetails -> {
                    log.info("JWTFilter : flatMap initiated");
                    log.info("JWTFilter : flatMap userDetails : {} | {} | {}", userDetails, userDetails.getUsername(), userDetails.getPassword());

                    if (jwtService.isTokenValid(accessToken,  clientIp, userDetails)) {
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userId, null, userDetails.getAuthorities());
                        authToken.setDetails(exchange.getRequest());
                        log.info("JWT authToken : {}", authToken);

                        SecurityContext context = new SecurityContextImpl(authToken);
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        log.info("JWT Filter : Completed userDetailsMono flatMap");

                        return chain.filter(exchange)
                                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context)));
                    } else {
                        log.info("JWT Filter : Token invalid");
                        return loginRedirect(exchange);
                    }
                })
                .doOnNext(userDetails -> log.info("JWT Filter : Found user : {}", userDetails))
                .doOnError(error -> {
                    if (error instanceof ResponseStatusException) {
                        ResponseStatusException responseStatusException = (ResponseStatusException) error;
                        if (responseStatusException.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                            log.error("User not found for token : {}", error.getMessage());
                        } else {
                            log.error("Unexpected error during JWT authentication : {}", error.getMessage());
                        }
                    } else {
                        log.error("Unexpected error during JWT authentication : {}", error.getMessage());
                    }
                });
//                .switchIfEmpty(Mono.defer(() -> {
//                    log.info("JWT Filter : switchIfEmpty executed - No UserDetails found");
//                    return loginRedirect(exchange);
//                }));
    }

    private Mono<Void> loginRedirect(ServerWebExchange exchange){
        log.info("JWTFilter : loginRedirect loginRedirect");
        String redirectUri = UriComponentsBuilder.fromUriString(LOGIN_REDIRECT_URI)
                .queryParam("redirect", exchange.getRequest().getURI())
                .build().toString();

        exchange.getResponse().setStatusCode(HttpStatus.FOUND);
        exchange.getResponse().getHeaders().set(HttpHeaders.LOCATION, redirectUri);
        return exchange.getResponse().setComplete();
    }
}
