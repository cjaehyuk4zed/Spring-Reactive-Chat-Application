package allofhealth.messenger.chat;

import allofhealth.messenger.auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.context.Context;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@RestController // 데이터 리턴 서버
@RequestMapping("/api/v1/chat")
public class ChatController {

    private final ChatRepository chatRepository;
    private final ChatService chatService;
    private final AuthService authService;


    /**
     * Added a simple hashing (of sorts) to generate a unique roomNum attribute for the chat.
     * This is required to get all data between the sender and receiver.
     * Because from A's perspective, B is the receiver. But from B's perspective, A is the receiver.
     * MongoDB will save messages between [sender = A, receiver = B] and [sender = B, receiver = A]
     * but won't be able to query them together for one chatroom, because
     * a query for [sender = A, receiver = B] will not return messages where [sender = B, receiver = A]
     * @param receiver
     * @param exchange
     * @return
     */
    @Operation(summary = "해당 상대방과 채팅 연결하기")
    @GetMapping(value = "/receiver/{receiver}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Chat> getMsgByReceiver(@PathVariable(name = "receiver") String receiver, ServerWebExchange exchange){
        log.info("ChatController GET /receiver/{receiver}");

        return authService.getMonoUserPrincipalOrThrow()
                .flatMapMany(sender -> {
                    return chatService.getMonoRoomNumBySenderReceiver(sender, receiver)
                            .flatMapMany(roomNum -> {
                                log.info("ChatController getMsgByReceiver roomNum : {}", roomNum);
                                return chatRepository.mFindByRoomNum(roomNum)
                                        .subscribeOn(Schedulers.boundedElastic())
                                        .doOnNext(result -> log.info("ChatController getMsgByReceiver doOnNext : {}", result))
                                        .doOnError(error -> log.info("ChatController getMsgByReceiver doOnError : {}", error.getMessage()));
                            });
                });

/**
 * Below is the method to use if you wish to directly input the SecurityContext into the return method
 * However, as the above method suggests, the ReactiveSecurityContextHolder automatically inserts
 * the SecurityContext into the subscription
 */
//        return ReactiveSecurityContextHolder.getContext()
//                .flatMapMany(securityContext -> {
//                    String sender = securityContext.getAuthentication().getPrincipal().toString(); // Assuming sender is the username
//
//                    // Save the current SecurityContext into Reactor's Context
//                    return chatService.getMonoRoomNumBySenderReceiver(sender, receiver)
//                            .flatMapMany(roomNum -> {
//                                log.info("ChatController getMsgByReceiver roomNum : {}", roomNum);
//                                return chatRepository.mFindByRoomNum(roomNum)
//                                        .subscribeOn(Schedulers.boundedElastic())
//                                        .contextWrite(Context.of(SecurityContext.class, securityContext)) // Propagate SecurityContext
//                                        .doOnNext(result -> log.info("ChatController getMsgByReceiver doOnNext : {}", result))
//                                        .doOnError(error -> log.error("ChatController getMsgByReceiver doOnError : {}", error.getMessage()));
//                            });
//                });
    }

    @Operation(summary = "해당 상대방에서 메시지 전송")
    @PostMapping("/message")
    public Mono<Chat> setMsgByReceiver(@RequestBody Chat.Request chatRequest){
        log.info("ChatController POST /message");

        return authService.getMonoUserPrincipalOrThrow()
                .flatMap(sender -> {
                    // Hashing String to "long" variable type, using Java's String.hashCode() method
                    long roomNum = chatService.getRoomNumBySenderReceiver(sender, chatRequest.getReceiver());

                    Chat chat = new Chat.ChatBuilder()
                            .msg(chatRequest.getMsg())
                            .sender(sender)
                            .receiver(chatRequest.getReceiver())
                            .roomNum(roomNum)
                            .createdAt(LocalDateTime.now())
                            .build();

                    return chatRepository.save(chat);
                })
                .doOnNext(result -> log.info("ChatController setMsgByReceiver doOnNext : {}", result))
                .doOnError(error -> log.info("ChatController setMsgByReceiver doOnError : {}", error.getMessage()));
    }

//    @CrossOrigin
    @Operation(summary = "채팅방 번호로 채팅 접속하기")
    @GetMapping(value = "/roomNum/{roomNum}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Chat> findByRoomNum(@PathVariable Long roomNum) {
        log.info("ChatController findByRoomNum");

        return chatRepository.mFindByRoomNum(roomNum)
                .subscribeOn(Schedulers.boundedElastic())
                .doOnNext(result -> log.info("ChatController findByRoomNum doOnNext : {}", result))
                .doOnError(error -> log.info("ChatController findByRoomNum doOnError : {}", error.getMessage()));
    }
}