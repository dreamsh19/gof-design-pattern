---
created: 2025-04-12 (토) 16:06:34
modified: 2025-04-12 (토) 20:00:25
---

## Intent

요청의 발신자와 요청을 실제로 처리하는 수신자(이하 핸들러)의 결합을 막는다. (체이닝을 통해)
핸들러를 서로 체이닝하고, 특정 핸들러가 요청을 처리할 때까지 체인을 따라 포워딩한다.

## Motivation
- 버튼 -> 대화상자 -> 애플리케이션 의 도움말 메시지를 제공해야하는 시나리오
	- 버튼을 눌렀을 때 버튼의 도움말이 있으면 버튼의 도움말을 노출하고, 아니면 대화상자의 도움말을 노출하고.. and so on 형태의 시나리오.
	- 이때 most specific(버튼) -> most general(애플리케이션) 순으로 도움말을 제공하게 된다.
		- ![Image](https://github.com/user-attachments/assets/b2840198-4bef-4765-a876-07599ca61f27)
	- 최종적으로 도움말 메시지를 누가 제공하는지는 버튼 입장에서는 알지 못한다.(decoupling)
- Chain of responsibility 패턴은 클라이언트와 핸들러의 결합을 해제한다.
	- 여러개의 핸들러로 이어지는 체인을 만들고,
	- 체인을 구성하는 핸들러는 요청을 직접 처리하거나 successor에게 포워딩한다.
	- 그렇게 되면 체인 속 특정 핸들러가 요청을 처리할때까지 요청은 포워딩된다.
	- 이때 클라이언트는 실제로 어떤 핸들러가 요청을 최종적으로 처리했는지 알지 못한다. = implicit receiver
- 체인 속 객체(Handler)는 동일한 인터페이스를 공유해야한다.
	- 요청을 체인에 따라 포워딩하기 위해서
	- 그리고 체인을 구성하는 핸들러간 구별이 불가능하게 하기 위해서
	- 이때 동일한 인터페이스 = 요청을 처리하는 인터페이스 + successor 에 대한 참조

## Applicability
- 한 개 이상의 객체가 요청을 처리할 수 있고, 그 중 어떤 객체가 이 요청을 처리할 수 있을지 "사전에" 알 수 없을 때. (처리할 핸들러가 자동으로 결정되어야한다.)
- 요청을 핸들러에게 발행하고 싶은데, 명시적으로 핸들러를 지정하고 싶지 않을때.(혹은 지정할 수 없을때)
- 요청을 처리할 수 있는 핸들러의 체인이 동적으로 지정되어야 할때
	- 클라이언트는 체인의 중간부터도 요청을 발행할 수 있음.
	- 예를 들어 위 그림에서 버튼의 클라이언트는 aPrintButton에게 요청을 발행하여 aPrintButton -> aPrintDialog -> anApplication으로 이어지는 체인에 요청을 발행할 수도 있지만,
	- 대화상자의 클라이언트는 aSaveDialog -> anApplication으로 이어지는 체인에 요청을 발행할 수도 있다.
- (개인적인 생각)
	- 동등한 레벨 간의 단순 체이닝보다는 구체적->일반적(혹은 자식->부모) 형태로 전파되는 형태의 체인에 적합해보임.
	- 동등한 레벨 간의 단순 체이닝은 자신의 다음이 누구인지 알고 있는 것 자체가 부자연스러운 느낌..

## Structure
![Image](https://github.com/user-attachments/assets/4ba59686-8a31-42cb-8fce-b0574bb89085)

## Participants
- Handler
	- 요청을 처리하기 위한 인터페이스를 정의한다.
	- (optional) successor에 대한 참조를 갖는다.
- ConcreteHandler
	- 자신이 처리할 수 있는(responsible한) 요청을 직접 처리한다.
	- 처리할 수 없는 경우 successor로 포워딩한다.
- Client
	- 체인의 ConcreteHandler에게 요청을 발행한다.

## Consequences
- 객체간 결합 감소(서로에 대해 알 필요가 없다.)
	- 1. 클라이언트와 (실제로 요청을 처리하는) 핸들러는 서로를 알지 못한다.
	- 2. 체인을 구성하는 핸들러도 체인의 전체 구성에 대해 알지 못한다.
		- 자신의 단일 successor만 알면 된다.
- 체인을 런타임에 동적으로 구성할 수 있어 유연하다.
- 처리가 꼭 보장되진 않는다.
	- 요청이 처리되지 않은 채로 체인의 마지막에 도달할 수도 있고,
	- 체인을 잘못 구성한 경우 무한 루프 등의 문제가 발생할 수도 있다.

## Implementation
구현간 이슈
1. successor 체인의 구현
	- successor만을 위한 명시적인 참조를 두는 방법.
	- 이미 successor에 해당하는 객체가 있다면, 그 객체에 대한 참조를 successor로서 재사용하는 것.
		- 예를 들면 부모에 대한 참조를 가지고 있는 트리 노드의 경우 부모에 대한 기존 참조를 재사용.
2. successor 연결
	- 핸들러는 기본적으로 successor에 대한 참조를 갖고 있어야한다.(명시적이든, 기존껄 재사용하든)
	- 이때 successor에 대한 참조를 갖고 있다면, 디폴트 구현을 제공할 수 있다.
	- 디폴트 구현 : 아무것도 하지 않고, successor에게 포워딩만 하기.(존재한다면)
	- 디폴트 구현 예시
		```c++
		class HelpHandler {
		public:
			HelpHandler(HelpHandler* s) : _successor(s) { }
			virtual void HandleHelp();
		private:
			HelpHandler* _successor;
		};
		
		void HelpHandler::HandleHelp () {
			if (_successor) {
				_successor->HandleHelp();
			}
		}
		```
3. 요청의 표현
	- 요청은 단일 종류의 요청일수도 있지만, 여러가지 종류의 요청을 포괄할 수도 있다.
	- 그리고 요청의 종류별로 분기를 태우는 방식.
		- 핸들러마다 관심 있는 요청 종류만 처리하고, 관심없는 건 포워딩하는 등의 시나리오가 가능.

## Sample Code

```c++
typedef int Topic;
const Topic NO_HELP_TOPIC = -1;

class HelpHandler { // Handler 인터페이스
public:
	HelpHandler(HelpHandler* = 0, Topic = NO_HELP_TOPIC);
	virtual bool HasHelp();
	virtual void SetHandler(HelpHandler*, Topic);
	virtual void HandleHelp();
private:
	HelpHandler* _successor;
	Topic _topic;
};

HelpHandler::HelpHandler (
	HelpHandler* h, Topic t
) : _successor(h), _topic(t) { }

void HelpHandler::HandleHelp () { // 디폴트 구현
	if (_successor != 0) {
		_successor->HandleHelp();
}

bool HelpHandler::HasHelp () {
	return _topic != NO_HELP_TOPIC;
}
```

```c++
class Widget : public HelpHandler { // ConcreteHandler (기존 참조를 successor로 재사용)
protected:
	Widget(Widget* parent, Topic t = NO_HELP_TOPIC);
private:
	Widget* _parent;
};

Widget::Widget (Widget* w, Topic t) : HelpHandler(w, t) {
	_parent = w;
}
```

```c++
class Button : public Widget { // ConcreteHandler
public:
	Button(Widget* d, Topic t = NO_HELP_TOPIC);
	virtual void HandleHelp();
};

Button::Button (Widget* h, Topic t) : Widget(h, t) { }

void Button::HandleHelp () {
	if (HasHelp()) {
		// offer help on the button
	} else {
		HelpHandler::HandleHelp();
	}
}
```

```c++
class Application : public HelpHandler { // ConcreteHandler (End of chain)
public:
	Application(Topic t) : HelpHandler(0, t) { } // successor 없음 
	virtual void HandleHelp();
} 

void Application::HandleHelp () {  // 포워딩하지 않음.
	// show a list of help topics
};
```

```c++
// entry point 코드
const Topic PRINT_TOPIC = 1
const Topic PAPER_ORIENTATION_TOPIC = 2;
const Topic APPLICATION_TOPIC = 3;

Application* application = new Application(APPLICATION_TOPIC);
Dialog* dialog = new Dialog(application, PRINT_TOPIC);
Button* button = new Button(dialog, PAPER_ORIENTATION_TOPIC);

button->HandleHelp(); // 클라이언트 : 요청 발행
```

## Related Patterns
- Composite
	- Composite 패턴의 자식 노드(specific) -> 부모 노드(general)로 이어지는 시나리오에서 부모 노드는 자식 노드의 successor 형태로 활용될 수 있다.
