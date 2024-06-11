Readme 생성


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

### 백엔드 구조
Spring Reactive의 *Flux* 데이터 타입을 통해서 연속적인 data stream을 송신한다.
이러한 연속적인 data stream을 MongoDB와 송수신 하기 위하여 *@Tailable* 어노테이션을 함께 사용한다.

### 테스트

***벡엔드 테스트*** : ChatApplication.java 를 실행하면 백엔드가 실행되며, 아직 연동되어 있지 않으므로
테스트는 간단한 별도의 프런트엔드 어플로 진행한다.

***프런트엔드 테스트*** : 테스트용 프런트엔드 "messenger-test"를 vscode에서 열고,
해당 프로젝트에서 'Open with Live Server'옵션으로 프런트엔드를 실행시키면 된다.

***메시지 전송 테스트*** : 테스트용 프런트엔드에서 prompt로 "로그인 아이디" 와 "상대방 아이디"를 물어볼 것이다.
예를 들어, 로그인 아이디 = UserA , 상대방 아이디 = UserB 로 실행한다.

***메시지 수신 테스트*** : 추가로 Live Server를 하나 더 실행시킨다.
이번엔 로그인 아이디와 상대방 아이디를 바꿔서 설정하면, 전송/수신 테스트가 가능하다.
예를 들어, 이번엔 로그인 아이디 = UserB , 상대방 아이디 = UserA 로 실행한다.
