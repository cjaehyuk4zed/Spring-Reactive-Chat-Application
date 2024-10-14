package allofhealth.messenger.config;


import allofhealth.messenger.chat.Chat;
import allofhealth.messenger.redis.ConnectedUser;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Bean
    ReactiveRedisConnectionFactory redisConnectionFactory(){
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(host, port);
        lettuceConnectionFactory.start();
        return lettuceConnectionFactory;
    }

    /*
    * ReactiveRedisTemplate is a high-level abstraction for interacting with Redis
    * Note that using ReactiveRedisTemplate offers more functionality (like JPA does)
    * compared to RedisReactiveCommands
     */
    @Bean
    ReactiveRedisTemplate<String, ConnectedUser> redisOperations(){
        Jackson2JsonRedisSerializer<ConnectedUser> serializer = new Jackson2JsonRedisSerializer<>(ConnectedUser.class);

        // Jackson JSON serializers for objects to ensure "ConnectedUser" can be serialized/deserialized properly
        RedisSerializationContext.RedisSerializationContextBuilder<String, ConnectedUser> builder =
                RedisSerializationContext.newSerializationContext(new StringRedisSerializer());

        RedisSerializationContext<String, ConnectedUser> context = builder.value(serializer).build();

        return new ReactiveRedisTemplate<>(new LettuceConnectionFactory(), context);
    }

}
