package allofhealth.messenger;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatRepository chatRepository;

    // Hashing String to "long" variable type, using Java's String.hashCode() method
    public long getRoomNumBySenderReceiver(String sender, String receiver){

        String s1, s2;
        if(sender.compareTo(receiver) < 0){s1 = sender; s2 = receiver;}
        else {s1 = receiver; s2 = sender;}
        log.info("sender : {} | receiver : {}", sender, receiver);

        int maxLength = Math.max(sender.length(), receiver.length());
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i< maxLength; i++){
            if(i < s1.length()){ sb.append(s1.charAt(i));}
            if(i < s2.length()){ sb.append(s2.charAt(i));}
        }
        log.info(sb.toString());

        long roomNum = Math.abs(sb.toString().hashCode());
        log.info("roomNum : {}", roomNum);

        return roomNum;
    }

}
