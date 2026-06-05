# Model 패키지 설명

이 패키지는 무인 택배함 시스템의 데이터와 핵심 상태 규칙을 담당한다.
View는 화면 표시와 입력만, Controller는 요청 흐름 제어만 담당하며,
Model은 택배함과 택배 데이터의 무결성을 책임진다.

## 클래스별 책임

- `Locker`: 모든 택배함의 공통 상태·동작을 정의하는 추상 클래스 (Comparable 구현으로 칸 ID 정렬 제공)
- `SmallLocker`, `MediumLocker`, `LargeLocker`: 크기별 구현 클래스 (`getSizeDescription` 다형성)
- `LockerSize`: 택배함 크기를 문자열이 아닌 enum으로 관리하는 타입
- `Package`: 송장번호·수령인·보관 시각·인증코드·상태를 관리 (송장번호 기준 equals/hashCode)
- `PackageStatus`: 택배 상태를 STORED / EXPIRED / PICKED_UP 중 하나로 제한하는 enum
- `LockerRepository`: 전체 택배함 목록과 파일 저장/불러오기를 담당하는 저장소

## 설계 포인트

### 1. 캡슐화와 데이터 무결성
필드는 `private`/`final`로 보호한다. 인증코드는 getter 없이 `verifyAuthCode()`로 일치 여부만 반환한다.
`PackageStatus` enum으로 "만료이면서 동시에 수령 완료" 같은 모순 상태를 차단한다.
또한 사용 여부를 나타내던 `occupied` boolean을 제거하고 `assignedPackage`의 존재 여부로
파생시켜, 두 필드가 어긋나는 모순 가능성을 원천적으로 없앴다(단일 진실원천).

### 2. 상속과 다형성
`Locker`를 추상 클래스로 두고 세 하위 클래스가 상속한다. 각 하위 클래스는 `getSizeDescription()`을
오버라이딩하여 크기별 설명을 다형적으로 제공한다. 추후 크기별 정책(보관 일수 등)을 추가할 때
하위 클래스에만 동작을 더하면 되도록 확장 지점을 분리해 두었다.

### 3. 컬렉션과 제네릭
`LockerRepository`는 `Map<String, Locker>`로 전체 택배함을 관리하고 조회 결과는 `List<Locker>`로 반환한다.
정렬은 `Locker`가 `Comparable`을 구현하고 `Collections.sort()`로 칸 ID 순서를 보장한다(6·7주차).
내부 컬렉션을 그대로 노출하지 않고 항상 새 리스트를 만들어 반환한다.

### 4. 파일 I/O
강의 범위를 넘는 부분이라 객체 직렬화(`ObjectOutputStream`/`ObjectInputStream`)로 자습해 구현했다.
`lockers.dat`에 전체 택배함 상태를 저장하며, 파일이 없거나 손상되면 기본 데이터로 자동 복구한다.

### 5. 멀티스레드 동기화
보관/수령/관리자/만료 감지 스레드가 같은 `LockerRepository`를 공유하므로 상태를 바꾸는 public
메서드를 `synchronized`로 선언한다(11주차). 보관·수령은 Swing EDT 단일 스레드에서 직렬 처리되고
만료 감지 스레드도 synchronized 메서드만 호출하므로, 같은 칸 상태에 동시에 접근하지 않는다.

### 6. 결합도 완화
Controller는 파일 저장 방식이나 내부 컬렉션 구조를 알 필요 없이 `LockerRepository`의 메서드만 호출한다.
저장 방식이 바뀌어도 Controller 수정 범위를 줄일 수 있다.
