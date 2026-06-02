# Model 패키지 설명

이 패키지는 택배 보관함 시스템의 데이터와 핵심 상태 규칙을 담당한다. View는 화면 표시와 입력만 담당하고, Controller는 사용자의 요청 흐름을 제어하며, Model은 택배함과 택배 데이터의 무결성을 관리한다.

## 클래스별 책임

- `Locker`: 모든 택배함의 공통 상태와 동작을 정의하는 추상 클래스
- `SmallLocker`, `MediumLocker`, `LargeLocker`: 크기별 택배함 구현 클래스
- `LockerSize`: 택배함 크기를 문자열이 아닌 enum으로 관리하기 위한 타입
- `Package`: 택배 한 건의 송장번호, 수령인, 보관 시각, 인증코드, 상태를 관리하는 클래스
- `PackageStatus`: 택배 상태를 `STORED`, `EXPIRED`, `PICKED_UP` 중 하나로 제한하는 enum
- `User`: 사용자 정보와 보관 내역을 관리하는 클래스
- `LockerRepository`: 전체 택배함 목록과 파일 저장/불러오기를 담당하는 저장소 클래스

## 모델 설계 포인트

### 1. 캡슐화와 데이터 무결성

각 Model 클래스의 필드는 `private` 또는 `final`로 선언하여 외부에서 직접 수정하지 못하게 했다. 인증코드는 getter로 제공하지 않고 `verifyAuthCode()`를 통해 일치 여부만 반환한다. 또한 `PackageStatus` enum을 사용하여 한 택배가 동시에 만료와 수령 완료 상태가 되는 모순을 방지했다.

### 2. 상속과 다형성

`Locker`를 추상 클래스로 두고 `SmallLocker`, `MediumLocker`, `LargeLocker`가 이를 상속한다. 각 하위 클래스는 `getSizeDescription()`을 오버라이딩하여 크기별 설명을 다형적으로 제공한다.

### 3. 컬렉션과 제네릭

`LockerRepository`는 `Map<String, Locker>`로 전체 택배함을 관리한다. 조회 결과는 `List<Locker>`로 반환하며, 내부 컬렉션 자체를 직접 노출하지 않고 새 리스트를 만들어 반환한다.

### 4. 파일 I/O

`LockerRepository`는 `ObjectOutputStream`과 `ObjectInputStream`을 이용해 택배함 상태를 `lockers.dat`에 저장하고 불러온다. 파일이 없거나 손상된 경우 기본 택배함 데이터로 복구한다.

### 5. 멀티스레드 동기화

보관, 수령, 관리자 처리, 만료 감지 스레드가 같은 `LockerRepository`에 접근할 수 있으므로 주요 public 메서드는 `synchronized`로 선언했다. 이를 통해 동시에 같은 택배함 상태가 변경되는 문제를 줄인다.

### 6. 결합도 완화

Controller는 파일 저장 방식이나 내부 컬렉션 구조를 알 필요 없이 `LockerRepository`의 메서드만 호출한다. 따라서 저장 방식이 바뀌어도 Controller의 수정 범위를 줄일 수 있다.
