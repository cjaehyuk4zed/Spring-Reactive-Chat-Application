package allofhealth.messenger.chat;

import allofhealth.messenger.auth.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@RestController // 데이터 리턴 서버
@RequestMapping("/api/v1/chat")
public class ChatController {

    private final ChatRepository chatRepository;
    private final ChatService chatService;
    private final AuthService authService;

    // Added a simple hashing (of sorts) to generate a unique roomNum attribute for the chat.
    // This is required to get all data between the sender and receiver.
    // Because from A's perspective, B is the receiver. But from B's perspective, A is the receiver.
    // MongoDB will save messages between [sender = A, receiver = B] and [sender = B, receiver = A]
    // but won't be able to query them together for one chatroom, because
    // a query for [sender = A, receiver = B] will not return messages where [sender = B, receiver = A]
//    @CrossOrigin
    @GetMapping(value = "/receiver/{receiver}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Chat> getMsgByReceiver(@PathVariable(name = "receiver") String receiver){
        log.info("ChatController GET /receiver/{receiver}");

        String sender = authService.getUserPrincipalOrThrow();

        long roomNum = chatService.getRoomNumBySenderReceiver(sender, receiver);

        log.info("ChatController RoomNum : {}", roomNum);
        return chatRepository.mFindByRoomNum(roomNum)
                .subscribeOn(Schedulers.boundedElastic());
    }

//    @CrossOrigin
    @PostMapping("/message")
    public Mono<Chat> setMsgByReceiver(@RequestBody Chat.Request chatRequest){
        log.info("ChatController POST /message");

        String sender = authService.getUserPrincipalOrThrow();
        // Hashing String to "long" variable type, using Java's String.hashCode() method
        long roomNum = chatService.getRoomNumBySenderReceiver(sender, chatRequest.getReceiver());

        Chat chat = new Chat.ChatBuilder()
                .msg(chatRequest.getMsg())
                .sender(sender)
                .receiver(chatRequest.getReceiver())
                .roomNum(roomNum)
                .createdAt(LocalDateTime.now())
                .build();

        log.info("ChatController Chat message : {}", chat);
        Mono<Chat> savedMessage = chatRepository.save(chat);
        return savedMessage; // Object를 리턴하면 Spring이 자동으로 JSON 변환 (MessageConverter)
    }

    @CrossOrigin
    @GetMapping(value = "/roomNum/{roomNum}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Chat> findByRoomNum(@PathVariable Long roomNum) {
        return chatRepository.mFindByRoomNum(roomNum)
                .subscribeOn(Schedulers.boundedElastic());
    }


    /**
     * Old APIs which does not involve login
     */
//    @CrossOrigin
//    @GetMapping(value = "/sender/{sender}/receiver/{receiver}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    public Flux<Chat> getMsgBySenderAndReceiverRoomNum(@PathVariable String sender, @PathVariable String receiver) {
//
//        // Hashing String to "long" variable type, using Java's String.hashCode() method
//        long roomNum = chatService.getRoomNumBySenderReceiver(sender, receiver);
//
//        return chatRepository.mFindByRoomNum(roomNum)
//                .subscribeOn(Schedulers.boundedElastic());
//    }

//    @CrossOrigin
//    @PostMapping("/message")
//    public Mono<Chat> setMsgBySenderAndReceiver(@RequestBody Chat chat){
//        chat.setCreatedAt(LocalDateTime.now());
//
//        // Hashing String to "long" variable type, using Java's String.hashCode() method
//        long roomNum = chatService.getRoomNumBySenderReceiver(chat.getSender(), chat.getReceiver());
//
//        chat.setRoomNum(roomNum);
//        Mono<Chat> savedMessage = chatRepository.save(chat);
//        return savedMessage; // Object를 리턴하면 Spring이 자동으로 JSON 변환 (MessageConverter)
//    }
}