package allofhealth.messenger.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisService {

    private final ReactiveRedisTemplate<String, ConnectedUser> redisTemplate;

    /*
    * A Redis set is an unordered collection of unique strings (members)
    * ":" is used as coding convention when using Redis
     */

    // Save a ConnectedUser to Redis
    // This method saves a ConnectedUser in Redis under the key room:<roomNum>.
    // It stores the ConnectedUser as part of a Redis Set.
    public Mono<Boolean> saveConnectedUser(ConnectedUser connectedUser) {
        String key = "room:" + connectedUser.getRoomNum();
        return redisTemplate.opsForSet()
                .add(key, connectedUser)
                .map(result -> result > 0);
    }

    // Get all users connected to a specific room
    // This method returns the number of users connected to a specific room.
    // It uses the Redis SCARD command via ReactiveRedisTemplate.
    public Mono<Long> getConnectedUserCount(Long roomNum) {
        log.info("RedisService getConnectedUserCount roomNum : {}", roomNum);
        String key = "room:" + roomNum;
        return redisTemplate.opsForSet().size(key);
    }

    // Remove a user from a room
    // This method removes a user from the Redis set representing the room they were connected to.
    public Mono<Long> removeConnectedUser(ConnectedUser connectedUser) {
        String key = "room:" + connectedUser.getRoomNum();
        return redisTemplate.opsForSet().remove(key, connectedUser);
    }

    // Check if a user is connected in a room
    // This method checks if a specific user is connected to the room.
    // It uses the Redis SISMEMBER command via ReactiveRedisTemplate.
    public Mono<Boolean> isUserConnected(String userId, Long roomNum) {
        String key = "room:" + roomNum;
        return redisTemplate.opsForSet()
                .isMember(key, new ConnectedUser(userId, roomNum));
    }

    // Remove all users from a room
    // This method deletes the entire room key from Redis, effectively removing all users from the room.
    public Mono<Boolean> removeAllUsersFromRoom(Long roomNum) {
        String key = "room:" + roomNum;
        return redisTemplate.delete(key).map(deleted -> deleted > 0);
    }

}
