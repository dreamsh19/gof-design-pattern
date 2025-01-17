---
created: 2025-01-12 (일) 19:46:45
modified: 2025-01-17 (금) 23:43:13
---

# 0. Intro

- 이 책에서 다루는 디자인 패턴은 경험을 학습하여 반복적인 문제에 대해 매번 rediscover 하지 않도록 하는게 목적이다.
- 결국 많은 문제들은 유형화되어있다.
- 같은 문제를 만났을 때, 같은 고민을 반복하지 않고 정답 패턴을 적용하여 빠른 설계를 하는 것을 목적으로 한다.

# 1. What is a Design Pattern?

- 이 책에서 디자인 패턴을 "인터페이스화"하고, 디자인패턴 인터페이스는 아래 네 개의 요소를 갖는다
	- name, problem, solution, consequence
	- 각 패턴은 concrete class 역할
- name : 클래스명
- problem : 동기. 해결하고자 하는 문제
- solution : 요소와, 관계, 책임, 협력 관계(실제 구현에 관련된 내용)
- consequence : 장점 및 trade-off

>  The design patterns in this book are descriptions of communicating objects and classes that are customized to solve a general design problem in a particular context

## 2. Design Patterns in Smalltalk MVC

- 스몰토크 MVC에서 볼 수 있는 디자인 패턴 활용 사례 소개
	- MVC로 분리한 것 자체가 역할과 책임에 따라 클래스를 분리한 것.
- 다양한 패턴이 적용됐지만, MVC에서 쓰이는 주요 패턴은 3가지 : Observer 패턴, Composite 패턴, Strategy 패턴

### Obeserver 패턴
![Image](https://github.com/user-attachments/assets/c944afbe-7957-4ac5-a464-9b68fca04616)

- 한가지 데이터(모델)에 대한 다양한 뷰
- 그리고, 모델의 변경을 view는 구독하고, 모델은 변경을 통지한다. (1:N)

### Composite 패턴
- 뷰를 중첩 : 중첩된 뷰와 단일 뷰는 동일한 인터페이스로 조작이 가능하게 한다.

### Strategy 패턴
- 뷰와 사용자 인터렉션을 분리한다.
- 사용자 인터렉션은 컨트롤러에서 관리한다.
	- 팝업 메뉴를 클릭한다던가, 키보드를 이용한다던가
- 뷰에서는 컨트롤러를 갈아끼우면서 사용자 인터랙션에 대응한다. (뷰의 변경 없이)

### 그외
- [ ] Factory Method 패턴 : 뷰에 대한 기본 컨트롤러 클래스 추가.
- [ ] Decorator 패턴 : 뷰에 스크롤을 추가

# 3. Describing Design Patterns

아래와 같이 일관된 구조로 디자인 패턴을 기술할 예정이다.
- 이름
- 분류
- 의도
- 다른 이름
- 동기(시나리오)
- 활용성
- 구조
- 참여자
- 협력 방법
- 결과
- 구현
- 에제코드
- 사용 예
- 연관 패턴

# 4. The Catalog of Design Patterns

23개 패턴에 대한 인덱스

# 5. Organizing the Catalog

- 목적(3가지- creational, structural, behavioral)과 범위(2가지 - class, object)에 따라 분류
- ![Image](https://github.com/user-attachments/assets/95e1cad6-fe63-40c3-8479-d0c771f80e65)

- class vs object 차이
	- class는 컴파일 타임에 정적으로 결정되는 클래스 상속에 대한 패턴
	- object는 런타임에 동적으로 결정되는 객체에 대한 패턴.
	- 결국, 디자인 패턴이 설계의 유연함을 추구하므로, object에 관련된 패턴이 대부분임.
- 패턴은 서로 연관 관계가 있는 경우가 있음.
	- Composite 패턴은 주로 iterator 패턴, visitor 패턴과 함께 쓰임.
	- Prototype 패턴은 Abstract Factory 패턴의 대안임.
	- 의도가 달라도 결과적으로 구조는 유사할 수 있음 - Composite 패턴과 Decorator 패턴

# 6. How Design Patterns Solve Design Problems

디자인 패턴이 문제를 해결하는 여러가지 예시

## Finding Appropriate Objects

- 객체(object)는 결국 데이터+메서드 임.
- api( request) 는 객체의 내부 상태를 변경하는 유일한 방법이며, 외부와의 유일한 소통 창구
	- 그것이 캡슐화의 의미
- 객체를 범위를 결정하는 것이 가장 어려운 일임.
	- 쪼개는 기준이 상황마다 다 다르기 때문에
	- 그리고 실제 세계와 물리적인 대상과 대응되지 않는 객체들도 분명히 존재해야함. (알고리즘 등)
- 디자인 패턴은 이런 어려운 점에 대한 인지가 가능하게 해줌.

## Determining Object Granularity

- 객체의 크기는 제각각이다.
	- 애플리케이션 전체도 결국 하나의 객체이고,
	- 이를 구성하는 아주 작은 단위의 부품도 결국 하나의 객체이다.
- 이렇게 제각각인 객체의 크기를 결정하는데 디자인 패턴을 이용할 수 있다.
	- Facade 패턴, Flyweight 패턴, Abstract Factory 패턴, Builder 패턴, Visitor 패턴, Command 패턴

## Specifying Object Interfaces

- 객체의 연산은 곧 시그니처이다.(인풋, 아웃풋, 이름)
- 인터페이스 = 시그니처의 집합
- 이러한 인터페이스의 명세 방법
- Type : 인터페이스와 동치(인터페이스가 조금 더 추상화된 개념이다 정도)
	- 객체가 Window 타입이다 = Window가 갖고 있는 시그니처를 모두 구현하고 있다
	- SubType extends SuperType
		- 이때 SubType은 SuperType의 기능을 확장한다.
	- 인터페이스는 객체를 외부에서 컨트롤할 수 있는 유일한 수단이다.

- 객체와 인터페이스는 M:N 관게임.
	- 객체가 여러개 인터페이스를 구현할 수 있고,
	- 여러개의 객체가 하나의 인터페이스를 구현할 수 있다. : 다형성
- 다형성과 동적 바인딩
	- 동적 바인딩 = 인터페이스에 따른 구현체를 런타임에 결정하는 것
	- 다형성 = 대체 가능성 = 동일한 인터페이스의 구현을 다른 구현으로 대체할 수 있게 해줌.
		- 대체 가능하다는 건, 유연하다는 것을 의미한다.
	- 객체간 결합도가 사라지고, 인터페이스간 결합도만 있음.
- 디자인 패턴은 이러한 인터페이스의 "시그니쳐"를 정의하는데 도움을 줌.
	- 인터페이스가 가져야하는 중요한 점이 무엇이고, 어떤 데이터를 주고받아야하는 지 등
	- Memento 패턴 : 객체의 상태를 어떻게 저장하고 캡슐화하는지에 대한 정의함.
- 디자인 패턴은 인터페이스간 "관련성"도 정의하는데 도움을 줌
	- Decorator, Proxy, Visitor 패턴

## Specifying Object Implementations

- 객체의 구현은 "클래스"에서 정의한다.
- 객체는 클래스의 "인스턴스"이다.
- 클래스 및 클래스간 관계(Instantiate, 상속, 구현)의 표기법에 대한 내용

### Class vs Interface Inheritance

- 클래스 : 객체의 상태와 구현에 대한 정의
- 인터페이스(타입) : 객체가 외부에 지원하는 요청(API)의 집합.
- 언어에 따라 둘을 명확하게 구분하지 않는 경우도 있다.
	- 그렇다고 해서 클래스와 인터페이스를 구분하는 개념이 사라지진 않고,
	- 그냥 언어로 표현하는 단계에서 그걸 구분하냐 마느냐의 차이이다.
- 클래스 상속 vs 인터페이스 상속
	- 클래스 상속 : 내부 구현을 "공유"하는 메커니즘
		- 목적 또한 구현의 "재사용"에 있음. 확장의 목적이 아님.
	- 인터페이스 상속(서브타이핑) : 서브타입이 수퍼타입을 "대체 가능한가"에 대한 정의
		- 구현을 공유할 필요가 전혀 없음
- 언어에서는 이 두 개념을 구분하지 않는 경우가 많음.
- 클래스 상속은 인터페이스 상속의 부분집합이다.
	- 클래스 상속도 결국 부모에서 지원하는 인터페이스를 모두 구현하고 있기 때문에 부모가 지원하는 인터페이스로서 당연히 대체가 가능하다.
	- 결국 클래스 상속은 다형성의 구현 방법 중에 하나이다.
		- 부모의 구현을 재사용하는 것에 특화된.
	- 그러나 클래스 상속은 부모의 구현을 재사용한다는 제한, 혹은 결합도를 가지고 있기 때문에 다형성의 장점을 완전히 가져갈 수가 없다.

### Programming to an Interface, not an Implementation

- 클라이언트는 인터페이스가 필요하지, 그 구현이 궁금하거나 필요한게 아님
- 따라서 인터페이스를 기반으로 프로그래밍하는게 바람직함.

## Putting Reuse Mechanisms to Work

### Inheritance vs Composition

- 조합이 상속보다 낫다.
- 클래스 상속 = 화이트박스 재사용
- 조합 = 블랙박스 재사용
	- 조합을 위해서는 인터페이스 정의가 전제되어야함.
- 상속
	- 컴파일 시점에 정적을 정의된다.
	- 런타임에 부모클래스의 구현을 변경할 수가 없다.
	- 서브클래스는 부모 클래스의 구현을 모두 알고 있다.
		- 그래서 캡슐화를 파괴한다는 의견도 있음.
		- 그리고 부모의 구현에도 영향을 받는다. 부모의 변경에 취약하다.
- 합성
	- 인터페이스에 따라 정의되므로, 구현 사이의 종속성이 없다.
	- 클래스의 캡슐화를 유지할 수 있다.
	- 그리고 클래스가 하나의 역할만 할 수 있게 한다. 단일 책임원칙
	- 결국 프로그램은 객체의 구현(컴파일 타임)이 아니라, 객체의 상호작용(런타임)에 의해서 결정되도록 한다.

### Delegation
- 위임 = 합성을 통해 상속을 달성할 수 있는 기법
- 위임은 상속을 완전히 대체 가능하다.

### Inheritance versus Parameterized Types


## Relating Run-Time and Compile-Time Structures




## Designing for Change

### Application Programs

### Toolkits

### Frameworks





# 7. How to Select a Design Pattern


![Image](https://github.com/user-attachments/assets/e0127bf5-33d3-4180-a11c-d7a7a9dec9a9)

# 8. How to Use a Design Pattern

- 디자인 패턴이 꼭 장점만 갖는 건 아니다.
- 디자인 패턴의 장점은 유연함을 제공하는 것이지만, 단점은 복잡하게 만들 수 있고 때로는 성능 이슈를 발생시킬 수 있다.
- 꼭 trade-off를 고려하자.
