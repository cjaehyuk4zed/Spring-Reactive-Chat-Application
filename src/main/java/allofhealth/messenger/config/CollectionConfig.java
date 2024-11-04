package allofhealth.messenger.config;

import allofhealth.messenger.chat.Chat;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
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

//    private final ReactiveMongoTemplate reactiveMongoTemplate;

    @Bean
    public MongoClient mongoClient() {
        String connectionString = "mongodb+srv://cjaehyuk4zed:ChatDB2024@chatdb.f9mra.mongodb.net/?retryWrites=true&w=majority&appName=ChatDB";
        ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .serverApi(serverApi)
                .build();

        // Create and return a reactive MongoClient
        return MongoClients.create(settings);
    }

    // ReactiveMongoTemplate using custom MongoClient
    @Bean
    public ReactiveMongoTemplate reactiveMongoTemplate(MongoClient mongoClient) {
        return new ReactiveMongoTemplate(mongoClient, "chatdb");
    }

    @Bean
    public Mono<MongoCollection<Document>> initializeCappedCollection(ReactiveMongoTemplate reactiveMongoTemplate) {
        return reactiveMongoTemplate.collectionExists(Chat.class)
                .flatMap(exists -> {
                    if (!exists) {
                        CollectionOptions options = CollectionOptions.empty().capped().size(209715200); // Set your desired size - currently 200mb
                        return reactiveMongoTemplate.createCollection(Chat.class, options);
                    }
                    return Mono.empty();
                });
    }
    // Size of 1048576 bytes = 1MB
}
