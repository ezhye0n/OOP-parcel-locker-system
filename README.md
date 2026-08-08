# OOP Parcel Locker System

> 2026학년도 1학기 객체지향프로그래밍 팀 프로젝트

객체지향을 적용한 **_무인 택배함**_ 소프트웨어 시뮬레이션 시스템.  
택배 보관·수령·만료 감지·관리자 현황 조회 기능을 **_MVC_** 패턴으로 구현.

---

## Features

- 택배 보관 및 6자리 인증코드 발급
- 인증코드로 택배 수령
- 보관 기간(3일) 초과 시 자동 만료 감지 (백그라운드 스레드)
- 관리자 전체 현황 조회 (JTable)
- 파일 I/O 기반 데이터 저장 및 복원

---

## Tech Stack

| 분류 | 기술 |
|------|------|
| Language | Java 17 |
| GUI | Java Swing |
| Architecture | MVC Pattern |
| Concurrency | Thread, Runnable, volatile, synchronized |
| Storage | Java Serialization (ObjectOutputStream) |
| Build | javac (CLI) |

---

## Team

| 파트 | 이름 | 역할 |
|------|------|------|
| Controller · Team Lead | 이지현 | DepositController, PickupController, AdminController, ExpiryMonitor, Main 구현 / 전체 뼈대 코드, 전체 통합·오류 테스트·수정, docs |
| View | 류채은 | MainMenuView, DepositView, PickupView, AdminView 구현 / Swing GUI 설계, JTable 관리자 현황판 구현 |
| Model | 안서희 | Locker 계층, Package, PackageStatus, LockerSize, User, LockerRepository 구현 / 파일 I/O 직렬화 설계, synchronized 동기화 구현 |

---

## Project Structure
```
oop-parcel-locker-system/
├── Main.java
├── controller/
│   ├── DepositController.java
│   ├── PickupController.java
│   ├── AdminController.java
│   └── ExpiryMonitor.java
├── view/
│   ├── MainMenuView.java
│   ├── DepositView.java
│   ├── PickupView.java
│   └── AdminView.java
└── model/
    ├── Locker.java
    ├── SmallLocker.java
    ├── MediumLocker.java
    ├── LargeLocker.java
    ├── LockerSize.java
    ├── Package.java
    ├── PackageStatus.java
    ├── User.java
    └── LockerRepository.java
```

---

## ▶ Getting Started

```bash
# 컴파일
javac -cp . model/*.java view/*.java controller/*.java Main.java

# 실행
java Main
```
