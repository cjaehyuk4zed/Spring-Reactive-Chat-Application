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
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

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
        log.info("JwtFilter : JWT token received, userId = " + userId);

//        // Validate the JWT token
//        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null){
//            // userDetails will hold an instance of User_Auth (DB table entity which implements the UserDetails interface)
//            UserDetails userDetails = (UserDetails) this.userDetailsService.findByUsername(userId);
//            log.info("JwtFilter ONE : Got UserDetalis");
//
//            // If JWT token is valid, configure Spring Security to set auth
//            if(jwtService.isTokenValid(accessToken, clientIp, userDetails)){
//                // Potentially add credentials later???
//                log.info("JwtFilter TWO : Validated JWT Token");
//                log.info("JwtFilter : Authorities are " + userDetails.getAuthorities());
//                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken( userId, null, userDetails.getAuthorities());
//                authToken.setDetails(exchange.getRequest());
//                // Note that `ReactiveSecurityContextHolder` still uses the `SecurityContext` class
//                // Creates an empty SecurityContext if none exists and set the auth
//                ReactiveSecurityContextHolder.withAuthentication(authToken);
//                log.info("JwtFilter : SecurityContext is : " + ReactiveSecurityContextHolder.getContext());
//            }
//
//        }
//
//        log.info("JwtFilter : Auth Header existed, but did not pass the filters");
//        return loginRedirect(exchange);

        return this.userDetailsService.findByUsername(userId)
                .flatMap(userDetails -> {
                    log.info("JwtFilter ONE : Got UserDetails");

                    if (jwtService.isTokenValid(accessToken, clientIp, userDetails)) {
                        log.info("JwtFilter TWO : Validated JWT Token");
                        log.info("JwtFilter : Authorities are " + userDetails.getAuthorities());

                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userId, null, userDetails.getAuthorities());
                        authToken.setDetails(exchange.getRequest());

                        SecurityContext context = new SecurityContextImpl(authToken);
                        return chain.filter(exchange)
                                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context)));
                    } else {
                        return loginRedirect(exchange);
                    }
                }).switchIfEmpty(loginRedirect(exchange));

    }

    private Mono<Void> loginRedirect(ServerWebExchange exchange){
        String redirectUri = UriComponentsBuilder.fromUriString(LOGIN_REDIRECT_URI)
                .queryParam("redirect", exchange.getRequest().getURI())
                .build().toString();

        exchange.getResponse().setStatusCode(HttpStatus.FOUND);
        exchange.getResponse().getHeaders().set(HttpHeaders.LOCATION, redirectUri);
        return exchange.getResponse().setComplete();
    }
}
