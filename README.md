# Chat Application using Spring Reactive and MongoDB

## Messenger Chat : 
SSE (Server Sent Events) 통신 방식을 활용한 메신저 채팅 어플의 프로토타입이다.
채팅을 하기 위해서는 우선 DB에 로그인을 하여 JWT Access Token을 발급 받아야 한다.

### 데이터베이스
MongoDB에 채팅 기록을 저장한다.
이때 MongoDB 데이터베이스 종류는 *Capped Collection*을 사용해야 한다.
Capped Collection을 사용해야, MongoDB의 *Tailable Cursor* 를 통해서
새로 저장되는 데이터를 data stream으로 연속적으로 송수신 할 수 있다.

### 벡엔드 기술 스택
- Java Spring
- R2DBC (Reactive Relational Database Connectivity)
- Spring Reactive
- Spring WebFlux
- Spring Security

### 백엔드 구조
Spring Reactive의 *Flux* 데이터 타입을 통해서 연속적인 data stream을 송신한다.
이러한 연속적인 data stream을 MongoDB와 송수신 하기 위하여 *@Tailable* 어노테이션을 함께 사용한다.
Reactive Streams를 활용하는 속에서, 정적 데이터인 MySQL DB와 소통하여
사용자 로그인/패스워드를 검증하고 JWT 토큰을 받기 위해 JPA 대신 R2DBC 를 활용하게 되었다. 

### 상수 Config 설정
해당되는 상수에 변경사항 발생시, 아래 파일들과 platform앱의 상수까지 함께 변경해줘야 한다.
- AuthHeaderConstants.java : 로그인 redirect_uri 링크 관리
- DirectoryMapConstants.java : server_addr 및 controller API endpoint 관리
- application.properties : JWT secret key, duration

### 테스트

***벡엔드 테스트*** : ChatApplication.java 를 실행하면 백엔드가 실행되며, 프런트엔드가 미완성 상태이므로
Postman을 활용하여 직접 JWT Access Token을 삽입하고 URI에 접속하여 테스트 한다.

***프런트엔드 테스트 (미완성)*** : 테스트용 프런트엔드 "messenger-test"를 vscode에서 열고,
해당 프로젝트에서 'Open with Live Server'옵션으로 프런트엔드를 실행시키면 된다.

***메시지 전송 테스트*** : 프런트엔드가 미완성 상태이므로, URI에 로그인 아이디와 상대방 아이디를 직접 삽입하여 전송한다.
최종 계획으로는, 테스트용 프런트엔드에서 prompt로 "로그인 아이디" 와 "상대방 아이디"를 물어볼 것이다.
예를 들어, 로그인 아이디 = UserA , 상대방 아이디 = UserB 로 실행한다.


***메시지 수신 테스트*** : 프런트엔드가 미완성 상태이므로, URI에 로그인 아이디와 상대방 아이디를 직접 삽입하여 전송한다.
최종 계획으로는, 추가로 프런트엔드 Live Server를 하나 더 실행시킨다.
이번엔 로그인 아이디와 상대방 아이디를 바꿔서 설정하면, 전송/수신 테스트가 가능하다.
예를 들어, 이번엔 로그인 아이디 = UserB , 상대방 아이디 = UserA 로 실행한다.
