# plus-week 📩
## 플러스 주차 개인 과제 🧑🏻‍💻
     
---------------
## 기능 구현 🖥
### 선행 조건

1. `application.properties` 환경변수 처리 되어 있는 데이터베이스 정보를 알맞게 수정
2. `spring.jpa.hibernate.ddl-auto` 옵션을 변경하여 테이블을 생성
   
     
### Lv 1. Transactional에 대한 이해

- 설명
    - `createReservation` 함수 40~43번째 유효성 검사 로직이 있다.
    - 데이터를 쉽게 생성하려면 해당 유효성 검사 코드를 주석처리 한다.
- 현재 조건
    - `createReservation` 함수는 Reservation, RentalLog 총 2번 저장을 수행한다.
    - `rentalLogService.save` 함수는 무조건 `RuntimeException`이 발생한다.
    - 그럼에도 Reservation 저장은 이루어진다.
- 개선
    - `createReservation` 함수 내에서 
        - @Transactional 선언으로 메서드 내 모든 작업이 하나의 트랜잭션으로 묶인다.
        - 에러 발생 시 트랜잭션 롤백, 정상 수행 시 트랜잭션 커밋.
        - All or Nothing 동작을 보장하기위해 @Transactional 어노테이션 사용


### Lv 2. 인가에 대한 이해

- `*AUTH_REQUIRED_PATH_PATTERNS*`: 사용하기 위해 인증이 필요한 API 이다.
- `*USER_ROLE_REQUIRED_PATH_PATTERNS*`: 사용하기 위해 USER 권한이 필요한 API 이다.
- `AuthInterceptor`: 로그인 여부를 확인하는 Interceptor 이다.
- `UserRoleInterceptor`: USER 권한을 확인하는 Interceptor 이다.
- 현재 조건
    - `/admins` 시작하는 URL을 로그인 한 사용자는 모두가 요청할 수 있다.
- 개선
    - `/admins` 들어오는 요청은 ADMIN 권한을 만들어서 해당 권한이 아니면 요청할 수 없게 만든다.
         - `AdminRoleInterCeptor` 추가
         - Interceptor 는 order 값을 기준으로 실행 순서 결정
         - Ordered.HIGHEST_PRECEDENCE 는 가장 높은 우선순위(값이 낮음)를 의미 (0)
         - 값이 낮을수록 먼저 실행, 높을수록 나중에 실행 (+1 순서를 1만큼 미룸)
         - 실행 순서 - authInterceptor > adminRoleInterceptor > userRoleInterceptor


### Lv 3. N+1에 대한 이해

- 설명
    - 모든 예약을 조회하는 기능이다. 사용자와 물건에 대한 정보를 가져오기 위해 별도로 접근하고 있다.
- 현재 조건
    - 모든 예약을 조회할 때 연관된 테이블에 있는 정보를 가져오면서 N+1 문제가 발생한다.
- 개선
    - 동일한 데이터를 가져올 때 N+1 문제가 발생하지 않게 수정한다.
         - Fetch Join 을 사용해 reservation, user, item 데이터 한 번에 가져오기


### Lv 4. DB 접근 최소화

- 설명
    - 여러 사용자에 대한 신고 기능이다. 사용자가 가진 상태를 한번에 여러건을 변경할 수 있다.
- 현재 조건
    - 위 기능을 수행하기 위해 사용자를 하나씩 찾고 저장하고 있다.
    - 데이터가 작을 때는 문제가 되지 않지만 데이터가 많아지면 DB 접근도 함께 증가한다.
- 개선
    - DB 접근을 최소화하는 방향으로 수정한다.
         - 쿼리 한 번으로 모든 사용자 조회
         - saveAll 을 호출하지 않아도 @Transactional 에 의해 변경사항 반영
         - JPQL 을 사용하여 특정 Id 리스트에 해당하는 사용자 상태 'BLOCKED' 로 일괄 변경

    
### Lv 5. 동적 쿼리에 대한 이해

- 설명
    - `userId`, `itemId` 조건 데이터 존재 여부에 따라 동적으로 검색을 수행한다.
- 현재 조건
    - 데이터 존재 여부에 따라 다른 JPA가 각각 호출되고 있다.
- 개선
    - `QueryDSL`을 활용하여 동적 쿼리를 적용한다.
    - N+1 문제가 발생하지 않도록 한다.


### Lv 6. 필요한 부분만 갱신하기

- 설명
    - `Item` 엔티티 `status` 컬럼  `nullable = false`
- 현재 조건
    - `status`에 대한 값을 전달하지 않을 때 `null` 값이 들어간다.
        - `Column 'status' cannot be null` 에러가 발생한다.
- 개선
    - `DynamicInsert` 활용하여 데이터를 보내지 않은 경우 기본값이 입력되도록 수정한다.


### Lv 7. 리팩토링

- 현재 조건
    1. `updateReservationStatus` if-else 과다하게 사용 중
    2. 컨트롤러 응답 데이터 타입 `void`
    3. `findById`가 중복되어 사용 중
    4. 상태 값이 `String`으로 관리 중
- 개선
    1. 필요하지 않은 `else` 구문을 걷어낸다.
    2. 컨트롤러 응답 데이터 타입을 적절하게 변경한다.
    3. 재사용 비중이 높은 `findById` 함수들을 `default` 메소드로 선언한다.
    4. 상태 값을 명확하게 `enum`으로 관리한다.
    5. 첫번째 Transactional 문제를 해결했다면 `RentalLogService` save 함수 내 19~21번째 코드를 삭제하거나 주석처리하여 기능이 동작하도록 수정한다.
 

### Lv 8. 테스트 코드 

- [X]  **PasswordEncoder 단위 테스트**
    - `@Test` 를 사용해서 PasswordEncoder에 존재하는 메서드들에 대해서 “**단위 테스트”** 를 추가한다.
- [X]  **Item Entity Test 추가하기**
    - `@Test` 를 사용해서 Item Entity에 대한 “**단위 테스트”** 를 추가한다.
    - Item Entity에서 status 값이 `nullable = false` 이므로 해당 제약 조건이 동작하는지 테스트 한다.
     

------------
## 트러블 슈팅 🎯
[TIL](https://sooyeoneo.tistory.com/)
