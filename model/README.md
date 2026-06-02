# Model 파트 코드리뷰 정리

## 1. Model의 역할
`model` 패키지는 화면이나 버튼 처리와 분리되어 택배함 시스템의 데이터와 핵심 상태를 관리한다.
Controller는 사용자의 요청을 해석한 뒤 Model 객체의 메서드를 호출하고, View는 결과만 표시한다.

## 2. 클래스별 책임
- `Locker`: 모든 택배함의 공통 상태와 동작을 정의하는 추상 클래스
- `SmallLocker`, `MediumLocker`, `LargeLocker`: 크기별 택배함 구현 클래스
- `Package`: 택배 한 건의 정보, 인증코드 검증, 만료/수령 상태 관리
- `User`: 사용자 정보와 보관 내역 관리
- `LockerRepository`: 전체 택배함 컬렉션 관리 및 파일 저장/불러오기 담당

## 3. OOP 기준 반영
### 캡슐화
모든 주요 필드는 `private`으로 선언했다. 특히 인증코드는 getter를 제공하지 않고 `verifyAuthCode()`로만 검증한다.

### 상속과 다형성
`Locker`를 추상 클래스로 두고, 소형/중형/대형 택배함이 이를 상속한다. 각 하위 클래스는 `getSizeDescription()`을 오버라이딩한다.

### 컬렉션/제네릭
`LockerRepository`는 `Map<String, Locker>`로 전체 택배함을 관리하고, 조회 결과는 `List<Locker>`로 반환한다.

### 파일 I/O
`LockerRepository`가 `ObjectInputStream`, `ObjectOutputStream`을 사용해 `lockers.dat` 파일에 택배함 상태를 저장하고 복구한다.

### 멀티스레드 동기화
여러 Controller와 `ExpiryMonitor`가 같은 Repository에 접근할 수 있으므로 Repository의 public 메서드를 `synchronized`로 선언했다.

## 4. 코드리뷰에서 설명할 수 있는 설계 이유
- View가 직접 데이터를 수정하지 않도록 Model이 상태 변경 규칙을 가진다.
- 이미 사용 중인 택배함에는 새 택배를 넣지 못하게 하여 데이터 무결성을 보장한다.
- 인증코드는 직접 노출하지 않고 검증 결과만 제공하여 정보 은닉을 지킨다.
- 파일 읽기에 실패해도 기본 택배함 목록으로 복구해 프로그램이 계속 동작하도록 했다.
