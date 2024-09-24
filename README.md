# Chat Application using Spring Reactive and MongoDB


## Messenger Chat : 
현재 개발 중인 플랫폼에서 사용하게 될 메신저 채팅 어플의 프로토타입이다.
메인 플랫폼의 로그인 시스템과 아직 연동되어 있지 않은 상태다.
현재 MongoDB 무료 버전의 용량 한계가 있다고 하여, Apache Kafka로 이전 작업 중이지만 이는 미완성이다.

### 데이터베이스
MongoDB에 채팅 기록을 저장한다.
이때 MongoDB 데이터베이스 종류는 *Capped Collection*을 사용해야 한다.
Capped Collection을 사용해야, MongoDB의 *Tailable Cursor* 를 통해서
새로 저장되는 데이터를 data stream으로 연속적으로 송수신 할 수 있다.

### 벡엔드 기술 스택
- Java Spring
- Spring Reactive
- Spring WebFlux
- Spring Security (OAuth2 Client)

### 백엔드 구조
Spring Reactive의 *Flux* 데이터 타입을 통해서 연속적인 data stream을 송신한다.
이러한 연속적인 data stream을 MongoDB와 송수신 하기 위하여 *@Tailable* 어노테이션을 함께 사용한다.

### 테스트

***벡엔드 테스트*** : ChatApplication.java 를 실행하면 백엔드가 실행되며, 
아직 프런트엔드와 연동되어 있지 않으므로 테스트는 Postman으로 진행한다

***메시지 전송 테스트*** : Healthcare Platform에서 로그인하여 JWT토큰을 발급받는다.
HTTP Bearer Authentication에 토큰 값은 입력 후, 메시지 전송 API를 호출한다 (POST method).

이후 아래와 같은 형식으로 Postman의 Body에 JSON 형식으로 입력하여 전송한다.
Sender 정보는 JWT토큰에서 읽어서 전송되며, 객체ID는 MongoDB에서 자동 생성된다 (보안성 강화).
```json
{
    "receiver": "UserB",
    "message": "Hello, UserB"
}
```

***메시지 수신 테스트*** : Healthcare Platform에서 로그인하여 JWT토큰을 발급받는다.
HTTP Bearer Authentication에 토큰 값은 입력 후, 메시지 수신 API를 호출한다 (GET Method).
메시지 수신은 SSE(Server-Sent Events)를 통해 연속적인 데이터 스트림을 수신한다.


## Naver Cloud DB for MongoDB
네이버 클라우드에서 MongoDB 서버와 저장소를 생성하여 통신할 수 있다.
resources/application.yml 파일에서 알맞는 연결 설정을 생성하고
Postman에서 위와 똑같은 과정을 송신/수신 테스트를 진행하면 된다.

