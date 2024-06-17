package allofhealth.messenger.config;

import allofhealth.messenger.chat.Chat;
import com.mongodb.reactivestreams.client.MongoCollection;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import reactor.core.publisher.Mono;

/**
 * MongoDB Configuration
 * Configures a "capped collection" database on MongoDB, needed for using the @Tailable annotation
 */

@Configuration
@EnableReactiveMongoRepositories(basePackages = "allofhealth.messenger")
@RequiredArgsConstructor
public class CollectionConfig {

    private final ReactiveMongoTemplate reactiveMongoTemplate;

    @Bean
    public Mono<MongoCollection<Document>> initializeCappedCollection() {
        return reactiveMongoTemplate.collectionExists(Chat.class)
                .flatMap(exists -> {
                    if (!exists) {
                        CollectionOptions options = CollectionOptions.empty().capped().size(1048576); // Set your desired size
                        return reactiveMongoTemplate.createCollection(Chat.class, options);
                    }
                    return Mono.empty();
                });
    }
    // Size of 1048576 bytes = 1MB
}
