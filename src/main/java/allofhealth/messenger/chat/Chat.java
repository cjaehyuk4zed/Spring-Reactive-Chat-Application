package allofhealth.messenger.chat;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@Document(collection = "chat") // ALWAYS CHECK IF THIS MATCHES THE COLLECTION NAME
public class Chat {
    @Id
    private String id;

    private String msg;
    private String sender; // 보내는 사람
    private String receiver; // 받는 사람
    private Long roomNum; // 방 번호
    private LocalDateTime createdAt;

    /**
     * Minimize the amount of data sent over the request
     * Hides the sender and roomNum information on request
     */
    @Data
    public static class Request{
        private String msg;
        private String receiver; // 받는 사람
    }
}