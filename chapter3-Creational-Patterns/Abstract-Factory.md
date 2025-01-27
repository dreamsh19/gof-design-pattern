---
created: 2025-01-27 (월) 04:37:36
modified: 2025-01-27 (월) 15:50:23
---

## Intent

> interface for creating families of related or dependent objects

관련된 객체의 "군"을 만들기 위한 인터페이스

## Also Known As

키트(Kit)
- 키트 안에 관련된 부품들이 모두 세트처럼 포함되어 있다.
- 그리고 다른 걸 만들 때는 다른 키트로 바꾸기만 하면 된다.
	- (이때 키트 안에 구성품이 어떤 것인지는 관심사가 아니다. 그것이 키트의 목적이므로)
- 그리고 A키트 안의 제품이 B키트 안의 제품과 섞일 일이 없다.

## Motivation

- 위젯을 만드는 WidgetFactory 추상 클래스를 통해, 추상화된 위젯을 만들 수 있다.
- 사용자는 내부적인 룩앤필 구현체(Motif인지 PM인지)가 뭔지 알 필요가 없다.
- 그리고 WidgetFactory를 통해 만들도록 하면, 구현체간의 의존성을 강제할 수 있다.
	- WidgetFactory가 없었다면, 클라이언트 쪽에서는 Motif 버튼 + PM scrollbar 의 조합을 실수로 만들 수 있다.
	- 그러나 WidgetFactory를 통해 만들게 되면 이 조합은 실수로 만들어질 수 없다.

## Applicability

언제 사용하면 좋은가
1. 객체가 어떻게 생성되고, 구성되고 표현되는지를 시스템으로부터 분리하고 싶을때
2. 시스템이 객체의 군들 중 하나로 구성되어야할 때. (그리고 다른 객체의 군으로 대체 가능성이 있을때 )
3. 관련된 객체의 군이 같이 사용될 때. 그런데 같이 군끼리 같이 사용해야한다는 제약사항을 강제하고 싶을때.
4. 객체를 라이브러리를 통해 제공하고자 하나, 구현체가 아닌 인터페이스만을 노출시키고 싶을때

## Structure

![Image](https://github.com/user-attachments/assets/a514e3b5-565a-474b-987f-51d6b5511371)

## Participants

- Abstract Factory : Abstract Product들을 만들어낸다.
- Concrete Factory : Abstract Factory의 구현체
- Abstract Product : Abstract Factory가 만들어내는 추상 객체
- Concrete Product : Concrete Factory가 만들어내는 Abstract Product의 실제 구현체
- Client : AbstractFactory와 Abstract Product만 바라본다.

## Collaborations

- 일반적으로, Concrete Factory 중 하나의 싱글톤 인스턴스만 런타임에 생성된다.

## Consequences

### 장점
- Motivation + applicability 파트와 같은 이야기.
- 클라이언트로부터 제품군들의 구체 클래스를 숨길 수 있다.
	- 생성되는 제품군에 대한 컨트롤을 할 수 있다.
- 제품"군"에 대한 대체가 용이해진다.
	- ConcreteFactory만 갈아 끼우면, 생성되는 제품군을 일괄 변경할 수 있다.
- 하나의 군에 속한 제품군들끼리의 의존을 강제한다.
	- 이를 통해 제품군의 일관성을 확보할 수 있다.

### 단점
- 새로운 제품을 제공하기 어렵다.
	- 추상 팩토리는 생성할 수 있는 제품군이 픽스되어 있다.
	- 생성해야하는 새로운 제품이 추가되면, 추상 팩토리와 구체 팩토리 모두에 변경이 필요하다.
- 이에 대한 해결책?

## Implementation


추상 팩토리 구현 기법
1. 팩토리는 싱글톤
	- 애플리케이션은 일반적으로 하나의 제품군에 대해 하나의 Concrete Factory만 필요하다.
2. Creating products
	- 각 프로덕트에 대한 팩토리 메서드 패턴.
	- 추상 팩토리는 팩토리 메서드를 인터페이스로서 제공하고, 구체 팩토리는 이걸 구현한다.
		- 이것의 한계는 두 제품군이 조금만 달라도, 새로운 구체 팩토리가 필요하다.
			- 에를 들어 제품하나만 다르고 나머지는 다 같아도.
	- 위와 같은 문제는 prototype 패턴 기반의 구현으로 해결할 수 있다.
		- 새로운 제품군마다 새로운 구체 팩토리를 만들어야하는 부담을 제거해준다.
		- 결국 클래스를 만드는게 아니고, 제품군을 파라미터로 가질 수 있는 팩토리를 정의하고,
		- 팩토리의 인스턴스가 제품군을 만들어낸다.
		- 그러나 이런 구현은 클래스를 매개변수로 사용할 수 있는(클래스를 1급 객체로 취급하는) 언어에서만 사용이 가능하다.
3. 확장 가능한 팩토리 정의
	- 아래 내용은 확장성 측면에서는 좋으나, 실제로 쓸 일은 없을 것 같음. 추상 팩토리의 패턴의 목적을 가져가지 못함.
	- 추상 팩토리의 기본적인 구현은 제품마다 팩토리 메서드를 갖는다.
		- 이것의 문제는 새로운 제품이 추가되면 인터페이스와 구현 클래스 모두에 새로운 메서드가 추가되어야한다.
	- 이걸 해결하기 위해서는 생성될 제품 종류를 함수의 인자로 받으면 된다.
	- 그럼 추상 팩토리는 Make()라는 하나의 함수만 가지면 된다.
	- 단점은 less safe하고, 결정적으로 클라이언트에서는 생성되는 제품을 구별할 수가 없다. (다운캐스팅이 필요하다.)
		- 추상 팩토리의 본질에서 벗어남

## Sample Code


## Known Uses
- InterViews의 WidgetKit, DialogKit, LayoutKit
- ET++의 WindowSystem 추상 팩토리

## Related Patterns
- 구현 방법론 : Factory Method 또는 Prototype
- Singleton
