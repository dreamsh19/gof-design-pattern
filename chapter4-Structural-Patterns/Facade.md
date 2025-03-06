---
created: 2025-03-06 (목) 22:41:19
modified: 2025-03-07 (금) 00:36:05
---

## Intent

- 서브시스템의 인터페이스들을 통합한 하나의 상위 레벨 인터페이스(진입점)를 제공한다.
- 이를 통해 서브시스템의 사용을 용이하게 한다.

## Motivation
- 서브시스템에 대한 참조와 소통을 최소화하기 위한 목적
- 서브시스템들 여러개로 구현된 컴파일러를 떠올려보자.
	- 대부분의 애플리케이션은 단순히 컴파일 API만 사용하고 싶을뿐 로우 레벨(parser, codeGenerator 등)의 인터페이스는 필요하지 않고, 오히려 복잡도만 증가시킴.
	- 이러한 대부분의 애플리케이션을 위해 컴파일 API만 제공하는 facade를 만들어, 단일의 심플한 인터페이스(혹은 진입점)를 제공할 수 있다.
	- 그리고 대부분의 애플리케이션 외에 서브시스템의 로우 레벨 인터페이스까지 활용이 필요한 경우에는 여전히 로우 레벨 인터페이스를 사용할 수 있다.
- **결국, 복잡한 로우 레벨의 서브시스템 인터페이스가 필요하지 않은 "대부분"의 경우를 위해 Facade를 제공하여 복잡도를 낮추는데 그 목적이 있음**

## Applicability
- 복잡한 서브시스템의 로우 레벨 인터페이스 전부가 아니라 단일의 심플한 기능만 필요한 대부분의 클라이언트를 위해 제공
	- 복잡한 서브시스템의 로우 레벨 인터페이스가 필요한 클라이언트의 경우에는 facade 대신 그 내부 인터페이스를 사용하면 됨.
- 서브시스템과 클라이언트를 분리하여, 서브시스템의 독립성 및 이식성을 확보하고자 할때.
- 서브시스템의 레이어를 나누고 싶을때
	- 각 레이어의 레벨마다 facade를 둠으로써, 각 레벨간에는 단일 facade에만 의존하고, 복잡한 서브시스템에 대한 의존을 분리한다.

## Structure
![image](https://github.com/user-attachments/assets/35fb3f4a-3808-4b26-978a-d7a96d1e12d5)

## Participants
- Facade
	- 클라이언트 요청에 필요한 서브시스템 클래스를 알고 있어, 요청에 적절한 객체로 포워딩한다.
	- 클라이언트는 Facade를 통해서만 서브시스템과 소통함.
- subsystem classes
	- 각 서브시스템을 구성하는 클래스.
	- Facade로부터 전달된 연산을 수행함.
		- 이 경우 subsystem과 클라이언트는 직접 소통하지 않음.
	- Facade의 존재를 모름.
		- Facade에서만 subsystem을 참조함.
		- Facade 없이도 독립적으로 동작해야함.

## Consequences
- 클라이언트가 의존하는 서브시스템 수를 줄인다. 그에 따라 사용이 용이해진다.
- 클라이언트와 서브시스템 결합도를 낮춘다.
	- 클라이언트의 변경 없이 독립적으로 서브시스템 변경이 가능하다.(컴파일 의존성 제거)
	- 플랫폼 이식성도 좋아진다.(재컴파일 필요한 범위가 줄어들기 때문에)
- 클라이언트가 서브시스템을 직접 참조하는 것을 막지는 않는다.
	- 클라이언트가 사용의 편의성(facade 참조)과 커스터마이징(서브시스템 직접 참조) 사이에서 선택할 수 있다.

## Implementation
- 클라이언트-서브시스템간 결합도를 더 줄이는 방법(퍼사드에 대한 다형성을 확보하는 방법)
	- facade를 추상 클래스로 정의하고, 서브시스템별로 facade 구현체를 만드는 방법.
	- 혹은, facade를 서브시스템 객체를 조합하여 만들고, 서브시스템 객체를 갈아끼우는 등으로 다형성 확보.
- 서브시스템의 로우 레벨 인터페이스를 오픈할것(public)인가 숨길것(private)인가
	- 서브시스템 = 클래스 와 유사하며,
	- 클래스 내 public/private 의 특징과 그 궤를 같이함.

## Sample Code
```c++
class Compiler {
public:
	Compiler();

	virtual void Compile(istream&, BytecodeStream&);
};

void Compiler::Compile (
	istream& input, BytecodeStreamk output
) {
	Scanner scanner(input);
	ProgramNodeBuilder builder;
	Parser parser;
	
	parser.Parse(scanner, builder);
	RISCCodeGenerator generator(output);
	ProgramNode* parseTree = builder.GetRootNode();
	parseTree->Traverse(generator);
}
```

- Facade의 구현이 서브시스템 객체를 주입받는 형태가 아니고, 특정 구현체로 확정되어 하드코딩되어있음.
- CodeGenerator 등의 서브시스템을 주입가능하게 해서 Compiler의 유연한 확장을 지원할 수도 있지만,
- 가장 일반적인 케이스에 대한 단순한 진입점을 제공한다는 Facade의 본래 취지에 오히려 부합함.

## Related Patterns
- Abstract Factory
	- 퍼사드 내 subsystem 객체들을 만들때 추상 팩토리를 활용할 수 있다.
- Mediator
	- 기존 클래스의 기능을 추상화한다는 점에서 유사하다.
	- 다만, Mediator는 colleague 객체들간의 소통을 추상화하고, 종종 중앙화를 하기도 한다. 그리고 colleague 객체는 Mediator의 존재를 알고 Mediator와 직접 소통을 한다.
	- Facade는 새로운 기능을 추가하지 않고, 단순히 사용성을 개선할 뿐이다. 그리고 서브시스템은 facade의 존재를 모른다.
- Singleton
	- Facade 객체는 보통 싱글톤이면 충분한 경우가 많다.
