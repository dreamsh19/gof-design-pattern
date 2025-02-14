---
created: 2025-02-09 (일) 19:08:03
modified: 2025-02-14 (금) 17:50:53
---

## Intent

프로토타입 형태의 인스턴스를 복사하여 새로운 객체를 생성한다.

## Motivation

다양한 종류의 Graphic(Node, Staff)을 지원하고, 이 Graphic을 문서에 추가하는 GraphicTool을 생각해보자.
이때 GraphicTool은 Graphic을 받아서 Graphic.draw()를 호출할뿐이고, 달라지는 건 Graphic간의 draw()의 구현만 달라짐.
그럼, 프레임워크 제작자 입장에서, GraphicTool은 어떤 구체클래스 Graphic이 생길지 알 수가 없다.

같은 니즈로 이전 장에서 팩토리 메서드 패턴을 적용했었는데, 팩토리 메서드 패턴의 문제는 Graphic 구현체만큼 팩토리 클래스도 같이 늘어나는 문제가 있다.
(NoteGraphicTool, StaffGraphicTool 등으로 서브클래싱이 필요하다.)

결국 GraphicTool 클래스는 하나로 두고, GraphicTool의 "인스턴스"를 통해서 다양한 Graphic을 처리할 수는 없을까? 에 대한 해답으로
Prototype 패턴을 사용할 수 있다.

![image](https://github.com/user-attachments/assets/d1e3da19-1492-4cec-a3ae-fc476c1900b1)


팩토리 클래스의 서브클래싱을 막는 것 외에도,
생성되는 프로덕트인 Graphic 역시 서브클래싱이 아닌 서로 다른 인스턴스를 생성함으로써 클래스 수를 줄일 수 있다.
위의 예시에서 WholeNote 클래스, HalfNote 클래스 -> new Note("Whole"), new Note("Half") 와 같은 식.

위의 두가지를 모두 반영한 자바 코드 샘플

```java
class GraphicTool {

	private final Graphic prototype; 

	public GraphicTool(Graphic prototype){
		this.prototype = prototype;
	}

	public maniuplate(){
		this.prototype.clone().draw();
	};
}

// GraphicTool의 서브클래싱 없이 인스턴스로 다형성 구현
GraphicTool staffGraphicTool = new GraphicTool(new Staff()); 
GraphicTool wholeNoteGraphicTool = new GraphicTool(new Note("whole"));  // Note 클래스의 서브클래싱 없이 인스턴스로 다형성 구현
GraphicTool halfNoteGraphicTool = new GraphicTool(new Note("half"))

staffGraphicTool.manipulate();
halfNoteGraphicTool.manipulate();
wholeNoteGraphicTool.manipulate();
```

- 근데 꼭 clone()일 필요가 있나?
	- 팩토리 클래스의 서브클래싱을 막기 위한 목적이라면, create() 메서드를 지원하고, 그냥 스스로 factory가 될 수 있는 정도의 추상화로도 충분해보임.

## Applicability
- (모든 생성패턴과 동일하게 시스템이 생성되는 구체적인 product로부터 분리하고 싶을때)
- Product - ProductFactory의 쌍이 계속 병렬로 생기는 걸 방지하고 싶을때.
	- **Product가 스스로의 ProductFactory가 되는 셈.**
- 클래스의 인스턴스의 variation이 다양하지 않고, 몇 종류밖에 없을때.
	- 세 종류밖에 없다고 하면, 각 종류에 대한 프로토타입 인스턴스 3개만 만들어두고,
	- 그 인스턴스를 계속 복사해서 쓰면 된다.
	- 목적상 enum과 유사해보임?
		- 목적은 비슷하나, enum은 컴파일 타임에 종류가 결정된다는 것이 차이가 있음

## Structure
![image](https://github.com/user-attachments/assets/f3aba4dd-2f04-4952-8c0d-0029ffed0874)

## Participants
- Prototype : 스스로를 clone하는 인터페이스를 제공(Clonable)
- ConcretePrototype : clone() 구현체
- Client : Prototype.clone()을 호출하여 새로운 인스턴스를 생성.

## Consequences
- (다른 생성 패턴과 동일하게 구체적인 프로덕트를 숨긴다.)
- 높은 런타임 유연성을 제공한다.
- 프로토타입을 런타임에 추가/제거할 수 있다.
	- 이로써 다른 생성패턴 대비 훨씬 유연성을 확보한다.
	- 타생성패턴은 어쨋든 컴파일 시점에 결정된 클래스의 구현체간 선택에 있어서 유연한 것이지만,
	- 프로토타입 패턴은 클래스간이 아닌 인스턴스간 선택이 동적으로 가능해진다.
- 클래스 갯수를 줄여준다. (서브클래싱을 줄인다.)
	- 프로토타입 종류가 서브클래스가 제공하는 다형성을 대체하기 때문에.
		- 프로토타입 종류간 구분이 클래스간 구분 역할을 대체한다.
	- 팩토리 메서드 패턴의 parallel hierarchy 문제를 해결.
- 템플릿화할 수 있다.
- 어려운 점은 clone을 구현해야한다는 점.
	- clone()을 통해 생성하고자 하는 Product 인터페이스가 이미 존재하지만 clone()을 갖고 있지 않을때, clone()을 추가할 수가 없다.
	- 혹은 clonable하지 않도록 설계된 경우, 구현이 어려워진다.
		- 내부에 갖고 있는 객체가 deep copy를 지원하지 않거나,
		- 순환 참조가 발생하는 경우

## Implementation

구현간 이슈
1. 프로토타입 매니저(저장소)
	- 프로로타입 종류가 고정이 아니라 생기기도 하고, 없어지기도 한다면 프로토타입 매니저를 사용해라.
	- 프로토타입 매니저는
		- 내부적으로 프로토타입의 저장소(혹은 풀)를 갖고 있으면서
		- 지정된 키의 프로토타입의 추가/제거/조회를 지원한다.
2. clone()의 구현
	- 프로토타입 패턴의 가장 어려운 부분
	- 순환참조가 포함된 경우 까다롭다.
	- 그리고 언어 레벨에서 clone을 제공한다고 하더라도, 경우에 따라 deep copy가 필요할때도 있고, shallow copy가 필요할 때도 있다.
	- 따라서 어떤 걸 deep copy하고 어떤걸 shallow copy(공유)할지 먼저 결정해야한다.
3. 클론의 초기화
	- 단순 복제말고, 복제 이후에 일부(또는 전체)의 상태를 수정하고 싶은 니즈가 있을 수 있다.
	- 해당 객체가 사후 상태 변경 메소드(set...) 제공해야한다.

## Related Patterns
- Abstract Factory : 프로토타입 패턴과 서로 경쟁하는 패턴
	- 같이 사용될 수도 있음.
	- 예를 들면, 추상 팩토리가 고정된 프로토타입의 set을 저장하고 있다가 clone을 통해 제공하는 방식.
- Composite 패턴과 Decorator 패턴 사용 시에 프로토타입 패턴을 이용할 수 있음.
