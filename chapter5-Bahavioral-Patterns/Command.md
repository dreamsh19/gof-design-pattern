---
created: 2025-04-12 (토) 20:03:14
modified: 2025-04-12 (토) 23:50:38
---

## Intent

요청 자체를 객체로 캡슐화하여 클라이언트는 Command 객체를 매개변수로 하여 여러가지 요청을 수행한다.

## Also Known As
action, transaction

## Motivation
- UI toolkit(혹은 프레임워크) 제작자의 관점에서 유저 입력에 대한 적절한 액션을 취하는 요소를 제공하고 싶다.
	- 이때, 프레임워크 입장에서는 구체적인 인풋과 그에 따른 액션이 무엇인지 알수가 없다.
		- 프레임워크를 이용한 애플리케이션 레벨에서 정의하기 때문에
- "요청을 발행할 수 있는(혹은 액션을 트리거할 수 있는) 인터페이스 자체"만 제공하고 싶을때.
	- 트리거가 핵심이고, 어떤 액션을 구체적으로 하는지는 Command 객체의 구현에 따른다.
- ![Image](https://github.com/user-attachments/assets/cd3e5ee7-c011-4ab9-9204-3ee0dbe9d661)
	- MenuItem은 실행될 액션에 대해서 알지 못한다. 단순히 command 객체를 주입받을뿐이고, 주입된 command 가 액션을 결정한다.
	- 그리고 Command 객체는 Composite 형태로 하위 Command의 sequence로 활용할 수도 있다. (MacroCommand)

## Applicability
- 콜백의 객체지향적 표현
	- 액션(=함수)을 매개변수화한 것
- 요청 발행과 실제 수행을 분리하고 싶을때
	- 시간적 분리 : Command 객체는 원본 요청과 별도의 생명주기를 가질 수 있다. 이는 큐잉(요청을 저장해뒀다가 이후에 실행)이 가능하게 한다.
	- 공간적 분리 : 요청은 세션에 종속적일 수 있지만, 실제 수행은 공간 독립적으로 수행될 수도 있다
- undo를 지원하고 싶을때.
	- Command 객체는 상태를 저장한다.
	- Command 가 unexecute() 를 지원하거나,
	- 커맨드의 히스토리를 관리할 수도 있다.
	- 이를 통해 무한대의 양방향(undo, redo) 상태 이동이 가능하다
- 상태 변화에 대한 로깅과 이를 통한 복구를 지원하고 싶을때.
	- Command 수행시(상태 변화 시) 이를 로깅하고, 복구 시점에 로깅된 커맨드 목록을 재수행함으로써 crash로부터 복구할 수 있다.
- 트랜잭션을 추상화하고 싶을때
	- 트랜잭션 = 상태 변경점의 집합을 캡슐화한 것.

## Structure
![Image](https://github.com/user-attachments/assets/5a393a0a-0d27-4a59-b74a-f396bec7af55)

## Participants
- Command
	- execute()에 대한 인터페이스 제공
- ConcreteCommand
	- Receiver의 action을 호출함으로써 execute()을 구현
- Client
	- ConcreteCommand 의 인스턴스를 생성한다. (그리고 적절한 receiver를 주입한다.)
- Invoker
	- 요청을 포워딩한다. 구체적으로는 Command의 execute() 함수를 호출한다.
- Receiver
	- 실제 수행에 대한 로직적인 책임을 가져간다.

## Collaborations
![Image](https://github.com/user-attachments/assets/6b5db955-b35d-4b7a-8674-a498d7e2de6f)

1. 클라이언트가 적절한 Receiver와 함께 ConcreteCommand를 생성한다.
2. Invoker는 생성된 ConcreteCommand를 저장해둔다.
3. Invoker가 Command의 execute()를 호출하여 요청을 발행한다.
	- (Optional) 커맨드가 되돌리기를 지원해야하면 이를 위한 상태(히스토리 등)를 추가적으로 저장한다.
4. ConcreteCommand는 Receiver의 action()을 호출하여 실제 수행하도록 한다.

## Consequences
- 액션을 트리거하는 객체와 액션을 실제로 수행하고 그 방법을 알고 있는 객체를 분리한다.
- 여러개의 Command를 Composite command로 만들 수도 있다.
- 새로운 종류의 Command를 추가하기에 용이하다.

## Implementation
구현간 고려할 이슈
1. Command 객체의 책임 범위
	- receiver와의 바인딩만 정의하여 receiver로의 위임만 하는 최소한의 책임을 가질수도 있고,
	- 모든 상세 로직을 갖고 있을 수도 있다.
2. undo/redo의 지원
	- Command의 수행으로 인해 변경될 수 있는 모든 상태값들의 원본값을 추가로 저장해야한다.
		- 그리고 Receiver는 롤백을 위한 API를 제공해야한다.
	- 히스토리(커맨드의 리스트)를 저장해야하고, 그 리스트에서의 traversal이 undo/redo로 동작한다.
	- 그리고 undo를 지원하려면 Command의 재사용이 불가능하다.(매번 새로운 Command 인스턴스를 생성하여 execute()을 수행해야한다.)
		- 실행시점의 상태를 같이 저장하고 있어야하기 때문에
		- 혹은 히스토리에 저장시점에 복사를 하는 방식도 가능하다.(재사용이 불가능한 맥락은 동일하다.)
			- 이때 Prototype 패턴을 활용할 수 있음
		- 물론 Command가 상태를 변경하지 않는다면 재사용해도 됨
3. Command가 멱등성을 보장하지 않는 경우 문제가 될 수 있다.
	- 당연하게도, undo/redo 가 가능하려면 상태 변화가 Command 와 직전 상태에만 의존해야하고,
	- 그외에 것에 영향을 받게 되면 일관된 state 전환이 불가능해진다.
	- 이때 Memento 패턴을 활용하여 상태의 스냅샷을 저장할 수 있다.
4. c++ 템플릿의 활용 : 아래 Sample Code 참고

## Sample Code

```c++
class Command { // Abstract Command 클래스
public:
	virtual ~Command();

	virtual void Execute() = 0;

protected:
	Command();
};
```

```c++
class OpenCommand : public Command { // ConcreteCommand
public:
	OpenCommand(Application*);

	virtual void Execute();
protected:
	virtual const char* AskUser();
private:
	Application* _application;
	char* _response;
};

OpenCommand::OpenCommand (Application* a) { // Receiver로 Application을 주입받음
	_application = a;
};

void OpenCommand::Execute () { // Execute() 구현부. Receiver API 활용 구현
	const char* name = AskUser();
	if (name != 0) {
		Document* document = new Document(name);
		_application->Add(document);
		document->0pen();
	}
}
```

```c++
class PasteCommand : public Command { // ConcreteCommand
public:
	PasteCommand(Document *);

	virtual void Execute();
private:
	Document* _document;
};

PasteCommand::PasteCommand (Document* doc) { // Receiver로 Document를 주입받음
	_document = doc;
}

void PasteCommand::Execute () { // Execute() 구현부. Receiver API 활용 구현
	_document->Paste();
}
```

---
- 템플릿화(제네릭)된 Command의 구현
	- Receiver를 템플릿화.

```c++
template <class Receiver>
class SimpleCommand : public Command {
public:
	typedef void (Receiver::* Action)(); // Receiver의 멤버 함수
	
	SimpleCommand(Receiver* r, Action a): _receiver(r), _action(a) { }

	virtual void Execute();
private:
	Action _action;
	Receiver* _receiver;
};

template <class Receiver>
void SimpleCommand<Receiver>::Execute () { // Execute() 구현부
	(_receiver->*_action)();
}
```

- PasteCommand의 클래스 템플릿을 활용한 구현
```c++
Document* receiver = new Document;
Command* pasteCommand = new SimpleCommand<Document>(receiver, &Document::Paste);
pasteCommand->Execute()
```

---
- CompositeCommand의 구현

```c++
class MacroCommand : public Command { // Composite Command
public:
	MacroCommand();
	virtual ~MacroCommand();
	
	virtual void Add(Command*);
	virtual void Remove(Command*);
	
	virtual void Execute();
private:
	List<Command*>* _cmds; // subcommand만 있으면 되고 별도의 receiver는 필요하지 않다. 
};

void MacroCommand::Execute () {
	ListIterator<Command*> i(_cmds);
	
	for (i. First(); !i.IsDone(); i.Next()) {
		Command* c = i.Currentltem();
		c->Execute();
	}
}

void MacroCommand::Add (Command* c) {
	_cmds->Append(c) ;
}

void MacroCommand::Remove (Command* c) {
	_cmds->Remove(c);
}
```

- CompositeCommand 가 unexecute() 을 구현한다면, 하위 커맨드들의 unexecute()을 역순으로 호출해야한다.

## Related Patterns
- Composite : 연쇄 Command의 구현에 활용
- Memento : undo에 필요한 상태를 저장하는데 활용
- Prototype : 히스토리에 저장전 Command를 복사하는데에 활용
