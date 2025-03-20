---
created: 2025-03-18 (화) 20:24:33
modified: 2025-03-18 (화) 22:50:11
---

## Intent

공유를 통해 많은 수의 객체를 효율적으로 지원한다.

## Also Known As

Pool

## Motivation

- 문서 에디터 프로그램을 생각할때, 모든 요소를 객체로서 표현하지만 문자 하나하나를 객체로 사용하진 않는다.
- 이유는 비용
- Flyweight 패턴은 이러한 비용 문제 없이, 문자 하나하나를 객체처럼 사용할 수 있게 한다.
- Flyweight는 동시에 참조가 가능한 공유 객체이다.
	- 사용되는 문맥에 독립적이다(상태를 갖고 있지 않다)
		- 사용되는 문맥에 대한 가정이 들어가면 안된다.
		- 따라서 공유되지 않은 것과 동일하게 사용이 가능해야한다.
	- flyweight와 관련된 상태는 정확히는 두개로 분류할 수 있다.
		- intrinsic state: flyweight 자체적인 state.
			- 문맥에 의존적이지 않아야함
		- extrinsic state : flyweight가 사용되는 문맥에 대한 state.
			- 문맥마다 다르기 때문에, 문맥끼리 공유되면 안 된다.
			- 보통 이 extrinsic state는 클라이언트로부터 넘겨받음.(flyweight가 직접 갖고 있지 않음)
- flyweight은 객체로 표현하기에 그 수가 너무 많을때를 위한 디자인.
	- 예를 들면, 각 알파벳 letter.
		- intrinsic state : character code (flyweight을 나타내는 고유한 state)
		- extrinsic state : 포맷팅을 어떻게 할것인지, 폰트를 어떻게 할것 인지 등등.
	- 논리적으로는 각각의 알파벳도 동일한 객체 레벨로 처리가 가능하도록 하면서, 물리적으로는 하나의 객체를 공유하게 한다.
		- ![image](https://github.com/user-attachments/assets/560340a5-e39d-4cab-98d9-4c99e6413521)
		- 문자의 개수는 매우 한정적이지만, 문서에 들어갈 문자 개수는 무궁무진하다. 이런 경우에 flyweight가 아주 적합하다.

## Applicability
- flyweight 패턴의 사용되는 상황에 따라 그 효과가 다름
- 아래에 **모두** 해당하는 경우 사용하는 것이 좋다.
	- 애플리케이션이 많은 수의 객체를 사용할 때
	- 단순히 개수가 많아서 저장 비용이 클 때
	- 대부분의 객체 상태가 외부로부터 전달받을 수 있을때(extrinsic)
	- extrinsic 상태를 제외하면 소수의 공유 객체만으로 다수의 객체를 표현이 가능할때
	- 애플리케이션이 객체 동등성 비교 로직이 필요하지 않을때
		- flyweight 객체는 개념적으로는 다른 객체이지만 물리적으로는 공유 객체이기 때문에 동등성 비교가 true가 된다.

## Structure

![image](https://github.com/user-attachments/assets/72b1132a-fd48-45e5-be7b-3e6b409da313)

## Participants
- Flyweight
	- 공유 객체와 비공유 객체를 아우르는 최상위 인터페이스
	- "extrinsic state를 주입받아서 동작할 수 있는 무언가"를 정의하는 인터페이스
- ConcreteFlyweight
	- 사실상 본 챕터에서 다루는 공유 객체
	- intrinsic state을 내부적으로 저장하고 저장된 state는 모두 intrinsic state 여야함.(문맥에 독립적)
- UnsharedConcreteFlyweight
	- Flyweight 최상위 인터페이스 자체는 extrinsic state를 주입받아서 동작할 수 있는 무언가 일뿐이므로, 공유를 강제할 필요는 없음.
	- 일반적인 활용 예시 : UnsharedConcreteFlyweight 로 계층구조를 가다가 특정 레벨 이하의 객체(개수가 많아지므로)를 ConcreteFlyweight로 구현
- FlyweightFactory
	- Flyweight 객체를 생성하고 관리함.
		- 공유에 대한 관리 책임.
	- Client의 조회 요청에 적절한 Flyweight 객체를 리턴함.
- Client
	- Flyweight 를 위한 extrinsic state를 계산하거나 저장하여 전달함.
	- ConcreteFlyweight를 직접 생성해서는 안되고, 반드시 FlyweightFactory로 부터 조회해야함.

## Consequences
- 내부 상태로 저장하던 extrinsic state에 대한 연산을 추가적으로 하게 되면서 런타임 코스트가 증가할 수 있으나, 그 대신 공간 절약으로 상쇄할 수 있음
- 공간 감소량 결정 요소
	- 공유를 통해 줄어드는 객체의 수
		- 인스턴스가 더 많이 공유될 수록, 공간이 더 절약 된다.
	- 객체가 갖고 있는 intrinsic state의 크기
		- 공유 상태(intrinsic state)가 클수록 공간이 더 절약 된다.
	- extrinsic state가 계산이 가능한지 혹은 저장되어야 하는지 여부
		- extrinsic state가 계산이 가능하면 공간이 더 절약 된다.

## Implementation
구현간 이슈
1. extrinsic state의 분리
	- Flyweight 패턴은 extrinsic state를 잘 구별해서 공유 객체로부터 분리하는 것이 중요하다.
	- extrinsic state의 종류가 매우 다양하면, 공유 객체로 얻을 수 있는 이점이 별로 없음.
		- 위 character 객체의 예에서, extrinsic state는 폰트 같은 정보인데, 폰트의 종류는 문서 안에서는 하나 또는 여러개여봤자 다양하지 않다.
	- 이상적으로는 계산가능한 것(저장 대신)이 가장 좋다.
2. 공유 객체의 관리
	- 생성과 조회에 대한 제어
		- 클라이언트가 공유 객체를 직접 생성해서는 안된다.
		- FlyweightFactory 가 생성과 조회에 대한 제어를 하고, 클라이언트는 FlyweightFactory 만을 바라본다.
	- garbage collection 관점
		- 공유 객체도 garbage collection 등을 위해 참조 여부를 관리하면서 사용하지 않는 경우 메모리 확보를 위해 제거할 필요가 있다.
		- 그런데, 공유 객체의 수가 고정적이고 그 수가 적다면, 아예 관리하지 않고 영원히 존재하게 할 수도 있다.

## Sample Code

```c++
class Glyph { // 최상위 Flyweight 클래스
public:
	virtual ~Glyph();
	
	virtual void Draw(Window*, GlyphContext&); // GlyphContext : extrinsic state
	
	virtual void SetFont(Font*, GlyphContextk);
	virtual Font* GetFont(GlyphContext&);

	virtual void First(GlyphContext&);
	virtual void Next(GlyphContext&);
	virtual bool IsDone(GlyphContext&);
	virtual Glyph* Current(GlyphContextk);
	
	virtual void Insert(Glyph*, GlyphContext&);
	virtual void Remove(GlyphContextk);

protected:
	Glyph();
};
```

```c++
class GlyphContext { // extrinsic state
public:
	GlyphContext();
	virtual ~GlyphContext();

	virtual void Next(int step = 1);
	virtual void Insert(int quantity = 1);

	virtual Font* GetFont{);
	virtual void SetFont(Font*, int span = 1);

private:
	int _index;
	BTree* _fonts;
};
```


```c++
class Character : public Glyph { // ConcreteFlyweight
public:
	Character(char);

	virtual void Draw(Window*, GlyphContext&);

private:
	char _charcode; // intrinsic state

};
```

```c++
const int NCHARCODES = 128;

class GlyphFactory { // FlyweightFactory
public:
	GlyphFactory();
	virtual ~GlyphFactory () ;
	
	virtual Character* CreateCharacter(char); // ConcreteFlyweight 생성
	
	virtual Row* CreateRow(); // UnsharedConcreteFlyweight 생성
// ...

private:
	Character* _character[NCHARCODES]; // ConcreteFlyweight 저장소 
};

Character* GlyphFactory::CreateCharacter (char c) { // 실제 공유 객체 조회 및 생성 로직 
	if (!_character[c]) {
		_character[c] = new Character(c);
	}
	return _character[c];
}

Row* GlyphFactory::CreateRow () { // 비공유 객체 생성 지원
	return new Row;
}
```

## Known Uses
- [Java의 Integer도 Pool이 있다](https://dreamsh19.github.io/java/Java%EC%9D%98-Integer%EB%8F%84-pool%EC%9D%B4-%EC%9E%88%EB%8B%A4/)

## Related Patterns
- Composite 패턴
	- 종종 composite 패턴과 함께 논리적 계층 구조를 구현할때 사용된다.
	- 리프 노드를 공유할 때 Flyweight 패턴을 적용한다.
- State, Strategy 패턴 : 주로 flyweight로 구현된다.
