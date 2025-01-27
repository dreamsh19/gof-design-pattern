---
created: 2025-01-27 (월) 04:37:36
modified: 2025-01-27 (월) 23:32:36
---

## Intent
복잡한 객체의 생성과 객체의 표현을 분리한다.
동일한 생성 프로세스로부터 다른 표현을 생성할 수 있도록 한다.

## Motivation

RTF token -> ASCIIText, TeXText, TextWidget... 으로의 변환이 필요하다

- TextConverter에게 이 변환 및 복잡한 객체(변환 결과물, 혹은 표현)의 생성의 책임을 위임하겠다. => Builder
- RTFReader는 RTF를 토큰으로 파싱하는 책임만 갖도록 한다. => Director

## Applicability
- 복잡한 객체를 생성하는 알고리즘과 그 객체를 만들때 필요한 부품 및 그 조립 방법으로부터 분리하고 싶을때
- 생성된 결과물(표현)이 모두 다르고 이를 모두 지원해야할때.

## Structure

![Image](https://github.com/user-attachments/assets/2eeeb79d-5f4b-4341-8312-cc59f22f2cc4)

![github-production-user-asset-6210df.s3.amazonaws.com/47855638/406881329-d0908271-bb60-40be-9b8e-e3d421a84063.png?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=AKIAVCODYLSA53PQK4ZA%2F20250127%2Fus-east-1%2Fs3%2Faws4\_request&X-Amz-Date=20250127T102518Z&X-Amz-Expires=300&X-Amz-Signature=3d048c099cdacd1b5b1ad008000470f98808c6d243c6006d11d29aeeba11672d&X-Amz-SignedHeaders=host](https://github.com/user-attachments/assets/d0908271-bb60-40be-9b8e-e3d421a84063)

## Participants

- Builder : Product 객체의 "parts"를 만드는 추상화된 인터페이스를 제공한다.
	- 중요한 것은 part, 즉 부분부분마다 채워넣는 인터페이스를 제공하는 것.
	- 그리고 builder의 목적은 공통의 인터페이스를 가지는 다양한 종류의 Product를 제공하는 것이 **아니다**
		- **부분적으로 조립가능한 인터페이스를 제공하는 것이 목적이다.**
		- 따라서 핵심은 "부분적인 조립가능함"에 있기 때문에 최종 결과를 제공하는 것은 추상화된 Builder에는 필요없다.
- ConcreteBuilder : Builder의 구현체
	- 실제 파츠들을 조립(assemble)하여 product를 생성한다.
	- 생성할 product를 내부적으로 상태로 가지고 있다. (그래야 조립이 가능하니까)
	- 조립이 완성된 최종 product를 제공하는 인터페이스를 제공한다.
- Director : 빌더를 주입받아서, 빌더를 통해 product를 생성하는 객체
- Product : ConcreteBuilder가 생성하는 객체
	- 빌더별로 Product가 꼭 공통의 인터페이스를 가질 필요는 없다.
	- (추상화된 Builder에는 Product를 제공하는 메서드가 없는 이유)

## Collaborations
![Image](https://github.com/user-attachments/assets/3292cd16-573a-46a7-8007-dd103e42d552)

## Consequences

### 장점
- 객체 생성에 대한 추상화된 인터페이스를 제공한다.
	- 빌더 패턴 한정이라기보다 생성 패턴의 공통된 특징
- 생성 및 표현을 분리한다
	- product의 내부 표현을 숨기기 때문에 클라이언트는 이를 알수도 알필요도 없다.
	- ConcreteBuilder가 특정 product에 대한 생성과 assemble에 대한 모든 책임을 가져간다.
	- 다른 director에서 builder를 재사용할 수도 있다.
	- 이것 역시 책임이 분리됐기 때문이지, 빌더 패턴만의 특징은 아닌 것 같음.
- 단계별로 생성이 가능하다.(생성 과정을 세밀하게 컨트롤이 가능하다)
	- 생성자는 원래 한 방에 객체를 생성한다.
	- 그런데 빌더 패턴은 step by step으로 생성이 가능하고, 이것을 director가 컨트롤할 수 있다.
		- 그리고 director가 최종 결과물을 가져올까지 조합은 계속 가능하다.
		- 예를 들어,
			- 부품중에 벽돌이 있다고 하면 기존 생성자에서는 벽돌을 두개 넣는다던가 하는 것이 Product 안의 로직으로 포함되어있어 director가 이걸 컨트롤할 수 없었으나,
			- 빌더는 "벽돌을 추가한다"라는 인터페이스를 제공함으로써, director가 벽돌을 두번 넣든 세번 넣든 자유롭게 제어가 가능함.
	- 따라서 빌더 패턴은 생성 "과정"을 투명하게 제공한다.
		> Builder interface reflects the process of constructing the product more than other creational patterns

## Implementation


- 조립에 필요한 인터페이스를 정의한다.
	- 빌더는 단계별로 제품을 생성한다.
	- 그리고 대부분의 경우에는 append만 하는 형태가 충분하다. (수정하거나 삭제하거나 하는 것보다)
	- 그러나, 단순 append 외에도 기존에 조립된 것들을 활용하는 형태가 필요할 수도 있다.
		- 트리에 대한 빌더를 떠올려보면, 자식 노드를 빌더를 통해 만든 다음, 만들어진 자식 노드를 부모 노드에 붙히는 등의 형태가 필요하다.
		- Maze 에시에서도, 문을 만들때는 기존에 만들어진 방을 빌더에 전달하여 만들어야한다.
- Product가 공통의 인터페이스를 갖지 않는 이유?
	- 빌더의 목적 자체가 일관된 product를 제공하는 것이 아니다.
	- 오히려 반대로, 서로 연관 없는 다양한 product를 제공하는 쪽에 가깝다.
	- 그리고 클라이언트는 생성하고자 하는 product가 명확하고, 그 product에 맞는 builder를 채택하기 위한 디자인 패턴이다.
	- (생성될 Product가 무엇인지 모르고자 하는 니즈가 전혀 아니다. product가 공통의 인터페이스를 갖는다는 건 이런 경우에 적합하다.)
- 의도적으로 빌더는 "비어있는" 구현을 디폴트로 제공한다.
	- 관심있는 것만 오버라이드하라는 의도.
	- 빌더 패턴의 특성상 필요한 부품만 갖다가 조립을 하면 되기 때문에, 불필요한 부품에 대한 구현을 강제할 필요가 없음.
		- 모든 빌더가 그 부품을 필요로 하지 않는다.

## Sample Code


```c++
class MazeBuilder {
public:
	virtual void BuildMaze () { } // 디폴트로 비어 있는 구현
	virtual void BuildRoom (int room) { }
	virtual void BuildDoor (int roomFrom, int roomto) { }
	virtual Maze* GetMaze () ( return 0; }
protected:
	MazeBuilder () ;
) ;
```

```c++
Maze* MazeGame::CreateMaze (MazeBuilder& builder) {
	builder.BuildMaze();
	builder.BuildRoom(1);
	buiIder.BuiIdRoom(2);
	builder.BuildDoor(1, 2);
	
	return builder.GetMaze();

}
```

- Maze 안에 builder의 구현을 넣어도 되지만, Maze와 빌더를 분리함으로써,
	- Maze를 작게 유지할 수 있고,
	- MazeBuilder의 다양성을 제공할 수 있다.

## Related Patterns

- Abstract Factory : 복잡한 객체 생성이라는 관점에서 유사하지만, 추상 팩토리 패턴은 유사군들의 생성에 대한 니즈.
	- 빌더 패턴은 복잡한 객체의 "부품별" 생성에 중점을 둔다.
	- 그리고 빌더 패턴은 하나씩 쌓아가다가 마지막에 결과물을 리턴하지만, 추상 팩토리는 즉시 리턴한다.
	- 추상 팩토리는 부품을 만드는 것이지만, 빌더 패턴은 최종 결과물을 만든다.
