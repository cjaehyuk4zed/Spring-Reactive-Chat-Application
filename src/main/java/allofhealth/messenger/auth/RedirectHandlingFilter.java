package allofhealth.messenger.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class RedirectHandlingFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return chain.filter(exchange)
                .then(Mono.defer(() -> {
                    log.info("RedirectHandlingFilter initiated");
                    HttpStatusCode status = exchange.getResponse().getStatusCode();
                    if (status != null && status.is3xxRedirection()) {
                        String location = exchange.getResponse().getHeaders().getFirst(HttpHeaders.LOCATION);
                        log.info("RedirectHandlingFilter : redirect location : {}", location);
                        if (location != null) {
                            // Handle redirection logic, e.g., create a new request to 'location'
                            // Use WebClient or initiate a new request
                            return WebClient.create()
                                    .get()
                                    .uri(location)
                                    .exchangeToMono(response -> {
                                        // Process the redirected response
                                        return response.bodyToMono(String.class)
                                                .flatMap(body -> {
                                                    // Process body or return as needed
                                                    return exchange.getResponse().writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(body.getBytes())));
                                                });
                                    });
                        }
                    }
                    return Mono.empty();
                }))
                .doOnNext(exch -> log.info("RedirectHandlingFilter doOnNext : {}", exch));
    }
}
