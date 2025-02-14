---
created: 2025-02-14 (금) 00:33:11
modified: 2025-02-14 (금) 16:05:59
---

## Intent

클래스가 단 하나의 인스턴스만 갖도록 보장하고, 단일 인스턴스에 대한 전역 접근을 제공한다.

## Motivation

- 단 하나만 존재해야하는 클래스들이 있다.
	- 여러개가 존재하면 오히려 문제가 되는 경우
	- 예를 들면, 프린터 스풀러, 회계 시스템, A/D 변환기
- 클래스에서 자체적으로 단일 인스턴스만 사용할 수 있도록 만든다. 그것이 싱글톤 패턴의 핵심

## Applicability
- 단일 인스턴스만 존재하고, 그 인스턴스에 모든 사용자가 접근가능 해야할때,
- 단일 인스턴스가 서브클래싱을 통해 확장가능해야할때

## Structure

![image](https://github.com/user-attachments/assets/159d6978-b089-4d64-9993-696e926b0800)

## Participants
- Singleton : Instance 메서드 선언
	- Instance 메서드 : 단일 인스턴스를 리턴하는 클래스 레벨(static) 메서드
	- 단일 인스턴스 생성 책임을 가짐.
- Client : 클라이언트는 Singleton.Instance 메서드를 통해서만 인스턴스에 접근함

## Consequences
- 단일 인스턴스에 대해서만 접근 제공
- 네임스페이스 오염 방지
	- 싱글턴 인스턴스는 시스템 입장에서는 일반 인스턴스와 다르지 않다.
	- 즉, 시스템 입장에서는 전역적인 무언가가 아니기 때문에 불필요한 전역 변수 생성을 막으면서 전역 변수처럼 사용할 수 있게한다.
- 꼭 하나가 아니더라도 인스턴스 개수를 조절할 수 있다.
	- 핵심은 1개라는 개수가 아니고, 개수를 "조절할수 있음"에 있다.
- 클래스(static) 연산 대비 유연하다.
	- 서브클래싱을 통해 확장 가능
	- 클래스 연산은 오버라이딩이 안되고,
	- 한 개를 초과하는 디자인은 어려운 한계가 있다.(정말 Singleton만 가능)

## Implementation

구현간 이슈

1. 단일 인스턴스를 보장
	```c++
	class Singleton {
	
	public:
		static Singleton* Instance();
	
	protected: // 외부에서의 생성 제어
		Singleton(); 
	
	private:
		static Singleton* _instance;
	}
	
	Singleton* Singleton::_instance = 0;
	
	Singleton* Singleton::Instance () {
		if (_instance == 0) {
			_instance = new Singleton;
		}
		return _instance;
	}
	```
	- 위와 같은 lazy initialize 방식의 구현하는 이유
		- 단순히 global 또는 static 인스턴스로 생성하여 자동 초기화하면 안되는 이유.
		- 그 인스턴스가 유일하다는 것을 보장할 수 없음.
		- 싱글턴 인스턴스 생성에 필요한 정보가 자동 초기화 시점에 없거나 제공되지 않을 수 있다.(예를 들어, 런타임에 설정이 결정되는 경우)
		- 글로벌 객체는 초기화 순서를 지정할 수 없기 때문에 의존성이 존재할 수가 없다.
		- 그리고 사용되지 않아도 eager하게 생성됨.
2. 싱글톤 클래스의 서브클래싱
	- 서브클래싱이 중요한게 아니라, 단일 인스턴스를 생성하는 게 중요함.
	- 한가지 구현 방법.
		- 컴파일 타임 혹은 링크 타임에 instance 구현을 결정지어버리는것.
		- 당연하게도 런타임에서의 변경이 안되기 때문에 유연성이 떨어짐.
		- if-else 방식으로 가능한 구현체를 모두 나열하여 런타임에 선택하는 방식도 있으나, 모든 구현체를 나열해야한다는 한계가 있음.
		- 더 나은 방법은 싱글턴들의 저장소를 활용하는 방법
	- 싱글턴 저장소
		```c++
		class Singleton {
			public:
				static void Register(const char* name, Singleton*);
				static Singleton* Instance();
			protected:
				static Singleton* Lookup(const char* name);
			private:
				static Singleton* _instance;
				static List<NameSingletonPair>* _registry;
		};
		
		Singleton* Singleton::Instance () {
			if (_instance ==0) {
				const char* singletonName = getenv("SINGLETON"); // user or environment supplies this at startup
				_instance = Lookup(singletonName);
			}
			return _instance;
		}

		MySingleton::MySingleton() { // 생성 시점에 스스로 등록
		// . . .
			Singleton::Register("MySingleton", this);
		}
		```
		- 싱글턴 저장소는 싱글턴 인스턴스의 등록(및 저장), 질의를 지원한다.
		- Instance 메서드는 싱글턴 저장소에 질의하는 형태로 구현하고,
		- 싱글턴 구현체 클래스는 생성 시점에 싱글턴 저장소에 자신의 단일 인스턴스를 등록한다.
		- 이 방법은 모든 가능한 구현체를 알고 있어야하는 부담으로부터 자유로워진다.
			- 자기 스스로만 등록하면 되므로
			- 생성자가 아예 호출이 안되는 문제는?
				- static 객체로 하나를 정의해두면, 자동초기화되면서 생성자가 호출된다.
			- 이때 Singleton 클래스의 책임은 정확히는 객체의 생성보다는 "단일 객체에 대한 접근 권한 제공"의 책임.
			- 다만, 싱글턴 인스턴스가 사용이 안돼도 모두 사전에 생성 및 등록이 되어야한다는 한계점이 있음.

## Sample Code
## Known Uses
글로벌한 preferences, 설정들

## Related Patterns
- Abstract Factory, Builder, Prototype 패턴 등이 싱글톤 패턴으로 구현될 수 있다.
