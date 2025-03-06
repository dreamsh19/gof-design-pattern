---
created: 2025-03-06 (목) 20:04:33
modified: 2025-03-06 (목) 22:36:49
---

## Intent

- 서브클래싱보다 유연하게 동적으로 기능을 확장 가능하게 한다.

## Also Known As

Wrapper

## Motivation

- 클래스 단위가 아니라 개별 인스턴스에 기능을 확장하고 싶을때가 있다.
	- 기능 확장은 상속을 통해 가능하나, 상속은 기능 확장이 static하게 결정된다.
		- 예를 들면, border를 추가하고 싶을때 GUI component에 border를 추가하고 싶은 경우, Border를 상속한 Component 형태로 구현한다.
		- 이때 문제는 Component가 border가 필요할수도, 필요하지 않을수도 있으나, 런타임에 이걸 선택할 수가 없다. (정적으로 Border를 상속해버렸으므로)
	- 이런 경우, Component를 감싼 다른 component를 정의하면 된다 = Decorator
- Decorator는 자신이 감싸고 있는 Component를 구현하기 때문에, 클라이언트 입장에서는 decorator가 있든없든 동일하게 Component로서 처리가 가능하다.(transparent)
	- 이러한 transparency 덕분에 재귀적으로 무한한 nested 구조를 가져갈 수 있다.
		- 확장 기능에 확장 기능을 계속 추가할수 있는 형태.
- Decorator는 내부 component에 요청을 포워딩하고, 포워딩하기 전/후 작업을 추가할 수 있다.
- ![image](https://github.com/user-attachments/assets/ec699b5f-501f-4123-a59c-604d1090c304)
	- VisualComponent에 스크롤을 추가하는 ScrollDecorator
	- VisualComponent에 border를 추가하는 BorderDecorator
	- VisualComponent는 스크롤이 있을수도, 없을수도 있고, border도 마찬가지로 총 2 * 2 = 4 가지 조합이 가능
		- 서브클래싱이었다면 4개의 클래스가 필요하나, Decorator는 확장 기능 개수에 따라 선형적으로 증가

## Applicability

- 개별 인스턴스에 기능을 확장하고 싶을때
	- 다른 인스턴스들에 영향을 주지 않으면서
	- 그리고 그 확장 기능을 넣었다뺐다 할 가능성이 있는 경우
- 서브클래싱에 의한 확장이 현실적으로 어려울때
	- 예를 들면, 서로 독립적인 확장 기능의 개수가 많을때 (서브클래싱의 경우 모든 조합에 대한 클래스를 모두 만들어주어야함. 클래스 폭발 문제)
	- 혹은, 클래스 정의가 비공개거나, 서브클래싱에 활용할 수 없을때. (클래스를 상속할 수 없을 때 등등, java의 final class)

## Structure

![image](https://github.com/user-attachments/assets/7c6ef18b-61a1-461a-b475-ba42ce0e47e7)

## Participants
- Component
	- 행동을 정의하는 탑레벨 인터페이스
	- (Decorator에 의해 기능이 추가될 수 있는)
- ConcreteComponent
	- 행동을 구현한 구현체.
- Decorator
	- Component에 대한 참조를 내부적으로 가지고 있고,
	- 내부 Component에 요청을 포워딩하고, 포워딩 전/후로 기능을 확장.
- ConcreteDecorator
	- Component에 확장할 기능을 구현

## Consequences
### 장점
1. 정적인 상속보다 기능을 확장하기에 유연하다
	- 확장 기능을 런타임에 넣었다빼기가 쉽게 가능하다.
		- (상속은 사전에 확장 기능을 가진 클래스를 정의해두어야 하고, 확장 기능 조합만큼의 클래스가 필요함)
	- 기능 여러개를 넣는 것도 가능하고, 기능 한개를 여러 번 넣는 것도 가능하다. (본질적으로 Decorator를 여러번 추가하는 관점에서 둘은 다르지 않음.)
2. 클래스가 기능을 확장함에 따라 한 클래스 내에 기능이 점점 많아지는 것을 방지한다.
	- 클래스 기반 확장은 해당 확장을 사용하지 않는 인스턴스가 있어도 그 기능을 메모리에 올려야한다.
		- 예를 들면, border가 필요없는 VisualComponent 인스턴스도 border 관련 기능을 갖고 있어야한다.
	- 그리고 관련 없는 기능들이 하위 클래스들에게 전부 노출되는 문제도 있다.
		- 예를 들면, BorderedScrollableTextView 인 경우 ScrollableTextView를 상속할텐데, BorderedScrollableTextView는 ScrollableTextView에 Border를 추가하는 것만을 위한 클래스인데(즉, Scroll과는 무관해도되지만) Scroll 관련 기능에 접근할 수 있게 된다.
	- 그러나, Decorator는 필요할때만 기능을 추가할 수 있다. 서브웨이 재료넣듯이 (pay-as-you-go)
	- 그리고 확장 기능을 기존 객체로부터 독립적으로 추가할 수 있다. (원래 객체를 수정하지 않으므로)

### 단점
1. Decorator는 내부적으로 갖고 있는 Component와 메모리상에서 아예 다른 객체이다.
	- Object identity : 객체가 메모리에서 고유한 존재임을 식별하는 개념
	- 따라서, 메모리 상에서 동일한 객체임을 활용하는 연산(자바의 == 연산 등)을 하면 안된다.
	- (상속의 경우에는 이런 문제는 없다.)
2. 수많은 작은 객체들.
	- 비슷해보이는 수많은 작은 객체들의 생성될 수 있다.
	- 객체들은 클래스간의 관계가 아니고, 조합을 어떻게 하냐에 따라 달라진다.
	- 따라서 커스터마이징에는 유리할 수 있으나, 처음보는 사람이 보기에는 파악하기 어려울 수 있다.

## Implementation

1. 인터페이스 부합(conformance)
	- Decorator는 자신이 감싸고 있는 Component와 동일한 인터페이스를 구현해야한다.(구별이 불가능해야한다.)
2. 추상 Decorator 생략
	- 확장 기능이 하나 뿐일때는 굳이 추상 Decorator 레이어를 굳이 추가할 필요는 없음.
	- 바로 ConcreteDecorator를 구현해도됨
3. Component 클래스를 가볍게 유지하라.
	- 최상위 Component 컴포넌트에는 인터페이스(기능)만 정의하는데 집중하고 데이터를 저장하지 않는게 좋음.
		- 데이터를 저장하게 되면 decorator 개수만큼 비례하여 메모리 사용량이 늘어나는 문제가 있음.
	- 그리고 기능을 너무 많이 넣게 되면 Decorator가 불필요한 기능을 신경써야할 가능성이 높아지므로, 최소한의 기능을 유지하는 게 좋다.
4. Strategy 패턴과의 비교
	- 겉을 바꾸기(Decorator) vs 속을 바꾸기(Strategy)
	- Decorator처럼 확장 기능별로 추가/제거하고자 하는 경우 Strategy 패턴을 적용해볼 수 있다.
		- Component 내부에 한 개또는 여러 개의 Strategy에 대한 참조를 가지고, 각 기능에 해당하는 Strategy에 포워딩하는 형태.
		- Component 자체가 무거운 경우 Decorator를 적용하기 어렵고, 이때는 Strategy를 적용하는 게 적합하다.
	- Component와의 의존 관점
		- Decorator 패턴은 Component 밖에서 기능을 변경하기 때문에 Component는 Decorator의 존재를 알 필요도 없고 알수도 없다.
		- 그러나, Strategy 패턴은 내부에서 변경하므로, Component가 가능한 확장(Strategy)에 대해 그 존재를 알고 있어야한다.
			- Strategy 패턴은 수정시 Component 의 수정이 필요하다. (Component 내부에 Strategy의 API를 참조하므로)
	- Strategy 패턴은 Component와 동일한 인터페이스를 구현할 필요가 없다. (=가벼워질 수 있다.)

## Sample Code

```c++
class VisualComponent {
public:
	VisualComponent();
	
	virtual void Draw();
	virtual void Resize();
};
```

```c++
class Decorator : public VisualComponent {
public:
	Decorator(VisualComponent*);
	
	virtual void Draw();
	virtual void Resize();

private:
	VisualComponent* _component;
};

void Decorator::Draw () {
	_component->Draw();
}

void Decorator::Resize () {
	_component->Resize();
}
```

```c++
class BorderDecorator : public Decorator {
public:
	BorderDecorator(VisualComponent*, int borderWidth);

	virtual void Draw();
private:
	void DrawBorder(int);
private:
	int _width;
};

void BorderDecorator::Draw () {
	Decorator::Draw();
	DrawBorder(_width);
}
```

## Known Uses
- 디버깅 목적으로 전후로 로그 추가하는 경우.
- Stream
	- stream은 근본적으로 객체를 바이트의 나열로 전환하는 방법임.
	- 그런데, 스트림 종류에 따라 압축 알고리즘이 적용될수도 있고, 결과셋이 ascii 코드로 한정될 수도 있음.(그리고 각각의 on-off 에 대한 시나리오도 있을 수 있음)
	- 이러한 문제를 decorator로 해결
	- ![image](https://github.com/user-attachments/assets/e941966e-1571-4c62-bcb2-ef3fe119280e)

## Related Patterns
- Adapter
	- Decorator와 달리, Adapter는 동일한 인터페이스를 구현하지 않는다. 아예 새로운 인터페이스를 제공
- Composite
	- Decorator를 하나의 component만 가진 Composite으로 볼 수도 있다.
	- 그러나, Decorator의 목적은 "확장 기능을 추가"하는데 있고, Composite은 객체의 aggregation에 그 목적이 있다.
- Strategy
	- [Implementation](#Implementation) 파트의 Strategy 패턴과의 비교 참고
