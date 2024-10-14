//package allofhealth.messenger.redis;
//
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.redis.core.ReactiveRedisTemplate;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//
//@SpringBootTest
//class RedisServiceTest {
//
//    private RedisService redisService;
//    private ConnectedUser connectedUser;
//
//    @BeforeAll
//    static void setUpAll() {
//
//    }
//
//    @BeforeEach
//    void setUp() {
//        connectedUser = ConnectedUser.builder().userId("testuser").roomNum(123L).build();
//    }
//
//    @Test
//    void saveConnectedUser() {
//        redisService.saveConnectedUser(connectedUser);
//        assertEquals(true, redisService.isUserConnected("testuser", 123L));
//    }
//
//    @Test
//    void getConnectedUserCount() {
//        redisService.saveConnectedUser(connectedUser);
//        assertEquals(1, redisService.getConnectedUserCount(123L));
//    }
//
//    @Test
//    void removeConnectedUser() {
//        redisService.saveConnectedUser(connectedUser);
//        assertNotEquals(0, redisService.getConnectedUserCount(123L));
//
//        redisService.removeConnectedUser(connectedUser);
//        assertEquals(0, redisService.getConnectedUserCount(123L));
//    }
//
//    @Test
//    void isUserConnected() {
//        redisService.saveConnectedUser(connectedUser);
//        assertEquals(true, redisService.isUserConnected("testuser", 123L));
//    }
//
//    @Test
//    void removeAllUsersFromRoom() {
//        redisService.saveConnectedUser(connectedUser);
//        redisService.saveConnectedUser(new ConnectedUser("testuser2", 123L));
//        assertEquals(2, redisService.getConnectedUserCount(123L));
//
//        redisService.removeAllUsersFromRoom(123L);
//        assertEquals(0, redisService.getConnectedUserCount(123L));
//    }
//}