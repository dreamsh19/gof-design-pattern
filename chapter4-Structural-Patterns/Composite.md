---
created: 2025-02-23 (일) 02:47:04
modified: 2025-02-24 (월) 20:39:28
---

## Intent

- 복합 객체 = 단일 객체의 모음
- 클라이언트가 복합 객체와 단일 객체를 uniform하게 처리할수 있도록 한다.
- 트리 구조의 part-whole hierarchy를 표현
	- part-whole hierarchy : 한 레벨의 객체는 다음 레벨의 객체로 구성된다. = tree 구조

## Motivation
- 그래픽 애플리케이션과 같은 케이스에서 복잡한 컴포넌트는 단순 primitive 컴포넌트를 조합해서 만들 수 있도록 한다.
- 이에 대한 구현 방법으로, 복잡한 컴포넌트는 primitive 컴포넌트들의 컨테이너와 같은 방법을 채택할 수 있으나, 이 방법의 한계는 복잡한 컴포넌트와 primitive 컴포넌트간 구분이 생긴다
	- 구분이 생기면 복잡한 컴포넌트를 조합해서 또 다른 복잡한 컴포넌트를 만들어야하는 케이스에 대한 대응이 어려워진다.
- Composite 패턴은 이러한 "재귀적인 조합에 대한 uniform한 처리"가 가능하도록 한다.
- Composite 패턴의 핵심은 복잡한 컴포넌트이면서 동시에 primitive 컴포넌트인 클래스를 만들어내는 것이다.
	- 사실 복잡한 컴포넌트라는 것도 구현하는 사람의 입장인 것이지, 클라이언트 입장에서는 Composite인지 primitive인지 구분할 수도, 구분할 필요도 없다.

## Applicability
- part-whole hierarchy를 표현하고자 할때.
- 클라이언트가 primitive 객체와 composite 객체를 uniform하게 처리하게 하고자 할때.

## Structure

![image](https://github.com/user-attachments/assets/5e437637-c05b-4fc8-9343-a5b9dac077fb)

![image](https://github.com/user-attachments/assets/4a289b30-e49b-4166-81ad-5619bdff3249)

## Participants
- Component
	- 조합가능한 객체에 대한 인터페이스 제공(트리를 구성하는 노드)
	- 자식 노드에 대한 CRUD 인터페이스 정의
- Leaf (primitive)
	- 자식이 없는 Component
- Composite
	- 자식이 있는 Component
	- 자식에 대한 CRUD 연산을 구현
- Client
	- Component의 인터페이스를 호출하여 통신

## Consequences
- primitive와 composite으로 구성된 (재귀적인) 클래스 계층구조를 정의한다.
- 하지만 클라이언트는 이 둘을 동일하게 취급한다. (구분을 할수도, 할 필요도 없다)
- Composite을 구성할때, 특정 component만 자식이 될 수 있도록 컴파일 타임(의 타입 체킹)에 제한할 수 없다. (Component 간 타입에 대한 구분이 불가능하므로)

## Implementation
구현간 이슈
1. (명시적인) 부모에 대한 참조를 포함할 것인가
	- 포함한다면 데이터 일관성을 확보하는 게 필수적이다. (자식의 부모와 부모의 자식이 불일치가 발생하지 않도록)
2. 공유 component
	- component를 공유하지 않아야할 이유는 딱히 없으나,
	- 부모가 하나여야하는 경우 구현이 어려워진다.
	- 해결책은 부모를 여러개 둘 수 있는 것으로 해결할 수 있으나, 부모로 전파가 필요한 경우 어떤 부모를 선택할지 모호함에 대한 문제가 발생한다.
3. Component 인터페이스의 거대화
	- 자녀의 CRUD 관련된 연산은 사실 primitive 입장에서는 필요없는 연산인데도, Component 인터페이스에 포함 되어있음.
	- 이는 단일책임원칙에 위배됨.
	- 자녀의 CRUD에 대해서는 다음 항목에서 논의
4. 자녀의 CRUD 연산을 정의할 위치
	- Component라는 공통 인터페이스에 둘 것인가 vs Composite 한정으로 둘 것인가.
	- trade-off가 있다.
		- Component 에 정의하는 경우
			- 투명성을 제공한다는 장점. (모든 component가 uniform 할 수 있게 된다. )
			- 그러나 안정성이 떨어진다. (Primitive 객체에 자식을 추가/제거하는 것을 막을수 없다.)
				- Primitive 객체에 자식을 추가하는 경우 do nothing 하는 것보다는 명시적으로 실패하는 것(예외를 던진다던가)이 보통 좋다.
		- Composite 에 정의하는 경우
			- 안정성을 보장한다. (Primitive 객체에 자식을 추가/제거하는 것이 컴파일 타임에 이미 불가능하다. )
			- 투명성이 떨어진다. (Composite과 Primitive가 이미 타입상 구분이 생겨버렸기 때문에 )
			- 이 경우에 Composite과 primitive를 구분하기 위해 unsafe한 다운 캐스팅(Component->Composite or Primitive) 이 필요할 수 있는데, 이를 해결하기 위한 방법으로 Component에 `getComposite()` 연산을 제공하는 방법이 있다.
				- 디폴트 구현은 null을 리턴하고, Composite은 자신을 리턴하는 식.
			- 다만, 다운캐스팅에 비해서 타입 안정성을 보장한다는 정도일뿐이지, 결국 if 분기를 이용해야한다는 사실은 변하지 않고, 그 사실은 본질적으로 Composite과 Primitive가 uniform 하지 않다(구별을 해야한다)는 사실에서 비롯된다.
	- 개인적인 생각
		- Composite 클래스가 자식에 대한 CRUD를 꼭 지원해야하는가?
			- 책에서는 보다 범용적인 Composite 패턴을 다루기 때문에 그런 것 같음.
			- 동적으로 자식을 추가/제거하는 경우에는 필요하겠지만,
			- Composite의 자식이 최초에 생성될때 고정되는 불변형 Composite이라면, 굳이 자식에 대한 CRUD 연산을 퍼블릭하게 지원할 필요가 없음.
5. Component 안에 자식들에 대한 참조를 둘 것인가.
	- Primitive는 자식이 있을 일이 없는데 공간 낭비가 될 수 있다.
6. 자식들간 순서
	- 많은 경우 자식간 순서를 정의한다.
	- 순서가 중요하다면, 자식의 CRUD 상황에서 이를 잘 고려해야한다.
7. 캐싱을 통한 성능 향상
	- 매번 자식의 연산을 호출하지 않고, 최종결과물을 캐싱해두어서 성능상 이점을 취할 수 있다.
8. 더이상 사용하지 않게된 Component 자원에 대한 정리
	- 일반적으로, Composite이 삭제될때, 자식들도 같이 삭제해야한다.
	- 단, 자식이 shared 객체인 경우는 예외
9. 자식들을 저장할때 니즈에 맞는 자료구조를 선택하라.

## Sample Code

```c++
class Equipment {
public:
	virtual ~Equipment();

	const char* Name() { return _name; }

	virtual Watt Power();
	virtual Currency NetPrice();
	virtual Currency DiscountPrice();

	virtual void Add(Equipment*);
	virtual void Remove(Equipment*);
	virtual Iterator<Equipment*>* Createlterator();
protected:
	Equipment(const char*);
private:
	const char* _name;
};
```

```c++
class FloppyDisk : public Equipment { // Primitive 클래스
public:
	FloppyDisk(const char*);
	virtual ~FloppyDisk();

	virtual Watt Power();
	virtual Currency NetPrice();
	virtual Currency DiscountPrice();
};
```


```c++
class CompositeEquipment : public Equipment { // Composite  클래스
public:
	virtual ~CompositeEquipment();
	
	virtual Watt Power();
	virtual Currency NetPrice();
	virtual Currency DiscountPrice();

	virtual void Add(Equipment*);
	virtual void Remove(Equipment*);
	virtual Iterator<Equipment*>* Createlterator();

protected:
	CompositeEquipment(const char*);
private:
	List<Equipment*> _equipment;
};

Currency CompositeEquipment::NetPrice () {
	Iterator<Equipment*>* i = Createlterator();
	Currency total = 0;
	for (i->First(); !i->IsDone(); i->Next()) {
		total += i->Current!tem()->NetPrice();
	}
	delete i;
	return total;
}
```

```c++
class Chassis : public CompositeEquipment { // Composite 구현체
public:
	Chassis(const char*);
	virtual ~Chassis();
	
	virtual Watt Power();
	virtual Currency NetPrice();
	virtual Currency DiscountPrice();
};
```

## Known Uses
- 2장의 Glyph 예시
- MVC 모델에서의 View (subview를 포함할수도 있도록 되어있음 = composite)
- 금융 포트폴리오
	- 포트폴리오의 구성요소는 개별 자산이 될수도 있지만, 또 다른 포트폴리오가 될 수 있다.

## Related Patterns
- Chain of Responsitiblity : composite 패턴에서 parent를 계속 따라가는 연산(traverse 등)인 경우 chain of responsibility에 해당함.
- Decorator : 종종 Composite 패턴과 함께 사용된다. Decorator 클래스도 동일하게 공통 인터페이스인 Component를 구현하는 방식으로.
- Flyweight : 공유 component 사용하는 경우
- Iterator : Composite 내부의 자식들을 순회하는 경우
- Visitor : Composite과 Primitive 사이에 훝어져 있을 수 있는 연산을 localize 한다.
