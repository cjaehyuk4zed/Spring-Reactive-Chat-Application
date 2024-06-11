package allofhealth.messenger;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;
import reactor.core.publisher.Flux;

// Mongo specific org.springframework.data.repository.Repository interface with reactive support.
public interface ChatRepository extends ReactiveMongoRepository<Chat, String> {

    // MongoDB will save messages between [sender = A, receiver = B] and [sender = B, receiver = A]
    // but won't be able to query them together for one chatroom, because
    // a query for [sender = A, receiver = B] will not return messages where [sender = B, receiver = A]
    // Therefore, a distinct roomNum is needed for each chatroom between two users,
    // and the chatroom must be queried by this roomNum id.
    @Tailable
    @Query("{ roomNum: ?0 }")
    Flux<Chat> mFindByRoomNum(Long roomNum);
}