package allofhealth.messenger.chat;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.TreeSet;

@Data
@Builder
@Document(collection = "chatroom") // ALWAYS CHECK IF THIS MATCHES THE COLLECTION NAME
// New collection for keeping track of participants in a chat room
public class ChatRoom {
    @Id
    private Long id; // Chatroom Number
//    private String chatRoomName; // Chatroom name
    private TreeSet<String> users; // TreeSet to keep users in order
}
