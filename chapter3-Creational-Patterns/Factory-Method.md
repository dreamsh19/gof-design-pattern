---
created: 2025-02-09 (일) 17:46:23
modified: 2025-02-13 (목) 21:42:13
---

## Intent

- 객체를 생성하기 위한 인터페이스를 정의
- 하지만, 생성되는 구체적인 구현체에 대한 결정은 서브클래스에서 이루어지도록 함.

## Also Known As
- Virtual Constructor

## Motivation
- 애플리케이션의 메인 로직은 추상적이어야하고, 추상 클래스간(구체 클래스간이 아닌)의 상호작용만을 다룬다.
- 이때, 추상클래스 객체를 생성할 필요가 있을 수 있는데, 이 경우에 팩토리 메서드 패턴이 해법이 될 수 있다.
	- 추상클래스는 직접 인스턴스화할 수 없기 때문에,
	- 추상클래스 객체를 만드는 메서드 자체를 인터페이스로 기능화
		- 각 구현체는 구현체에 해당하는 객체를 리턴하도록 구현.
- Document을 다루는 Application에 대한 프레임워크를 떠올려보자.
	- Application도, Document도 추상클래스이다.
	- 프레임워크 제작자 관점에서는 이 Application이 어떤 application인지도 알수 없기 때문에, Document 역시 어떤 종류의 Document일지 예상할 수 없다.
	- 프레임워크는 단순히 application에서 document가 언제 생성되는지만 알 뿐(이것은 application의 로직 흐름이니까), 어떤 종류의 document가 생성되는지는 알 수 없다.
	- 여기서 로직 흐름이란 이런 형태
		- ![image](https://github.com/user-attachments/assets/6670d87d-d1fe-4cc6-9ca8-eae1896882de)
	- 팩토리 메서드 패턴을 통해 프레임워크와 구체적인 구현체 생성에 대한 로직을 분리할 수 있다.
		- 위의 예시에서 어디에도 document의 구현체에 대한 의존성은 없다.

## Applicability
- 클라이언트가 자신이 생성해야하는 구체적인 구현체를 예측할 수 없을때
- 생성되는 객체의 구체적인 구현에 대한 책임을 분리하고 싶을때
- 정보를 localize할 수 있다.
	- ConcreteProduct는 ConcreteCreator만 알고 있으면 된다.

## Structure
![Image](https://github.com/user-attachments/assets/06793411-c732-4597-8bbf-f3635d419bcb)

## Participants
- Product : 생성을 요하는 객체의 추상화
- ConcreteProduct : Product의 실제 구현.
- Creator : 팩토리 메서드를 선언한다.
- ConcreteCreator : 팩토리 메서드를 구현하고, ConcreteProduct를 리턴한다.

## Consequences

- 잠재적 단점 : Product가 한 종류밖에 안될때에도 Creator 추상 클래스에 대한 ConcreteCreator 서브클래스를 구현해야할 수 있다.
- 부가적인 결과
	1. 서브클래스에게 객체 생성 로직을 오버라이딩할 수 있도록 함.
		- 직접 생성의 경우 오버라이딩에 한계가 있다
	2. 병렬 클래스 계층구조를 연결(parallel class hierarchy) 할수 있도록 함.
		- 아래 예시에서 Figure와 Manipulator는 parallel class hierarchy를 가진다.
			- (참고) Manipulator는 Figure의 기능 중에서 user interaction과 조작 상태를 전담하는역할을 별도 객체로 분리한 것.
		- ![image](https://github.com/user-attachments/assets/b0c77bef-bb8d-42a9-b97c-cd5bfceb3a40)
		- 아래와 같은 평행 구조.
			- Figure -> Manipulator
			- |---- TextFigure -> TextManipulator ----
			- |---- LineFigure -> LineManipulator ----
			- |---- AFigure -> AManipulator ----
			- (Manipulator는 Figure가 늘어날때마다 같이 늘어나게됨.)
		- 이러한 구조에서 팩토리 메서드 패턴은 정보를 localize한다.
			- TextFigure은 TextManipulator만 알고 있다. LineManipulator는 알 필요가 없다.
				- 혹은 TextManipulator가 구현체로서 필요하다는 건 TextFigure만 알고 있으면 된다.(Localized knowledge의 의미)
			- LineFigure도 vice versa.

## Implementation

1. 주요 구현 방법 2가지
	1. 팩토리 메서드를 abstract로 정의하여 디폴트 구현을 제공하지 않음.
		- 서브클래스에게 구현을 강제한다.
		- 예측할 수 없는 인스턴스를 만들어야하는 딜레마를 가진다.
	2. 팩토리 메서드를 정의하고 디폴트 구현을 제공함
		- 객체의 생성을 별도 연산으로 분리함으로써, 서브클래스가 오버라이드 가능하게 함.
		- 이 방법의 주목적은 객체 생성의 "유연성"을 제공하기 위함임. (서브클래스에게 오버라이딩할 수 있는 구멍을 뚫어주는 것)
	- 추상 클래스에 디폴트 구현을 제공하는 방식은 가능은 하나 잘 사용하지 않음.
2. 파라미터를 갖는 팩토리 메서드
	- 생성될 객체의 종류를 파라미터로 넘기는 방법.
	- 확장성 확보: Create 함수에 새로운 id 값을 추가할수도 있고, 아예 Create 함수를 오버라이딩 할 수도 있다.
		```c++
		class Creator {
		public:
			virtual Product* Create(Productld);
		};
		
		Product* Creator::Create (Productld id) {
			if (id == MINE) return new MyProduct;
			if (id == YOURS) return new YourProduct;
			// repeat for remaining products...
			// MyProduct와 YourProduct는 Product의 구현체
			return 0;
		}
		```
3. 언어별 구현
	- 언어별로 생성자를 런타임에 동적으로 사용할 수 있는 언어인 경우에 대한 활용 방법
	- 클래스 자체를 파라미터로 넘기고, 런타임에 결정된 클래스의 생성자를 호출하는 방식
4. 템플릿(제네릭) 활용
	- 팩토리 메서드는 앞서 언급했듯이, 프로덕트마다 서브클래스가 하나씩 늘어나는 문제가 있음.
	- 제네릭을 활용하면, 하나의 클래스로 서브클래싱 효과를 내서 위 문제를 해결할 수 있음.
		```c++
		template <class TheProduct>
		class StandardCreator: public Creator {
		public:
			virtual Product* CreateProduct();
		};
		
		template <class TheProduct>
		Product* StandardCreator<TheProduct>::CreateProduct () {
			return new TheProduct;
		}
		
		// ...
		StandardCreator<MyProduct> myCreator;
		```

5. 네이밍 컨벤션
	- 팩토리 메서드 이름을 makeClass와 같이 "make"의 의미를 담아 이름 짓는게 중요함.

## Sample Code
- 추상 팩토리 예제 코드 참고.

## Known Uses
- 라이브러리와 프레임워크에 많이 사용됨
- MVC 모델에서 Controller는 View에 대한 팩토리 메서드를 제공.
	- Controller를 오버라이드하여 다양한 View를 create.

## Related Patterns
- Abstract Factory 패턴 : 팩토리 메서드 패턴을 활용하여 구현한다.
- Template Method 패턴 : 템플릿 메서드 안에서 호출된다.
- Prototype 패턴 : Prototype 패턴은 서브클래싱이 필요하지 않지만, Product에 대한 초기화가 필요한 반면, 팩토리 메서드 패턴은 이런 연산이 필요하지 않다.
