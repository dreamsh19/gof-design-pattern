---
created: 2025-04-12 (토) 23:52:55
modified: 2025-04-16 (수) 01:13:21
---

## Intent

언어의 문법을 구조적으로 문(sentence)으로 표현하는 방법을 정의하고, 그 표현을 해석(실행 또는 평가)하기 위한 인터프리터를 정의한다.

## Motivation

- 특정 문제가 자주 발생한다면, 그 공통의 문제를 문으로 표현하는 게 의미가 있다.
- 왜냐하면, 문으로 표현하게 되면, 그 문을 해석하는 인터프리터를 만들어서 동일한 패턴의 문제를 해결할 수 있다.
- 대표적인 예로, 텍스트 매칭 문제가 있다. 텍스트 매칭 문제를 문으로 표현한게 바로 정규표현식이다.
	- 공통되는 문제를 처리하기 위해 정규표현식의 형태의 표준적인 문법을 만듦.
- 그리고 이 문법을 클래스로 구조화한 형태로 표현하고, 인터프리터는 이 문법을 평가(처리, 실행 등)하도록 구현한다.
	- 각 인터프리터 구현체는 각자의 interpret() 함수를 구현하여, 문을 평가한다.
![Image](https://github.com/user-attachments/assets/48b02c75-0223-4c2b-842f-51f3ff284bbc)

## Applicability
- 문법을 가진 언어가 있고, 그 언어의 문들이 abstract syntax tree(이하 AST)로 표현이 가능할때 사용.
- 인터프리터 패턴은 아래와 같을 때 사용하면 좋다.
	- 문법이 단순할때.
		- 문법이 복잡한 경우 클래스 수가 많아지고 클래스 구조가 복잡해질 수 있다.
	- 성능이 크리티컬하지 않을 떄
	- 문법이 복잡하거나 성능 최적화가 필요한 경우 파서 생성기가 더 나은 대안이 될 수 있다.
		- 인터프리터는 아무래도 객체 지향적 표현에 집중한 패턴이므로 재귀 호출 등으로 인한 성능 이슈가 있을 수 있음

## Structure

![Image](https://github.com/user-attachments/assets/935dc373-25af-4f1f-a172-d4e609049564)

## Participants

- AbstractExpression
	- interpret() 인터페이스를 정의한다.
	- AST를 구성하는 노드
- TerminalExpression
	- 문의 종결 심볼(문법의 원자적 요소, 리프 노드)에 대한 표현
- NonterminalExpression
	- 문의 규칙에 대한 표현
	- 합성 요소에 대한 interpret()을 구현한다.
		- 그리고 일반적으로 합성요소에 대한 interpret()은 합성 요소를 구성하는 요소들의 interpret()을 재귀적으로 호출하는 형태로 구현된다.
- Context
	- AST 처리에 대한 문맥(진행상태) 정보를 담고 있는 전역 객체.
- Client
	- (필요하다면) 문으로부터 AST를 구성하고,
	- AST에 대한 interpret() 을 호출하여 문을 평가한다.

## Collaborations
- 클라이언트는 AST를 구성하거나 이미 주어진 상태에서 문맥을 초기화하고 AST의 interpret() 함수를 호출한다.
- NonterminalExpression 노드의 interpret() 구현은 하위 표현의 interpret()을 호출하여 그 결과를 조합한다.(Composite 패턴의 형태)
- TerminalExpression 노드의 interpret() 구현은 자체적으로 구현한다.(트리의 리프 노드 역할)
- 각 노드는 전역 문맥 정보에 상태를 저장하고 공유한다.

## Consequences

- 문법의 변경과 확장에 용이하다.
	- 각각의 문법이 클래스로 표현되므로
- 문법의 구현 또한 간단하다.
	- AST 노드의 구현은 간단하고 서로 유사한 구현을 갖는다.
- 복잡한 문법은 유지보수가 어렵다.
	- 따라서 순수 인터프리터 패턴보다는 다른 방법(파서 생성기 등)을 통한 보완이 필요할 수 있다.
- 동일한 문에 대해 새로운 표현방식을 추가할 수 있다.
	- interpret()의 구현만 바꾸면 pretty printing, type checking 등의 부가 기능을 추가할 수 있다.
	- 이때 visitor 패턴을 고려해볼 수도 있다.

## Implementation

인터프리터 패턴과 Composite 패턴은 많은 구현 이슈가 중첩된다. 그 중 인터프리터 한정 구현 이슈들:

1. AST의 생성
	- 인터프리터 자체는 AST를 "만드는" 방법에 관여하지 않는다. 즉 파싱 개념이 없다.
2. interpret() 연산의 주체
	- 꼭 interpret() 연산을 AST 노드 안에 포함할 필요는 없다.
	- AST 노드의 interpret() 연산 안에 공통된 연산이 중복되는 경우(타입 체킹, 최적화, 코드 생성 등..) 모든 AST 노드에 해당 로직이 중복으로 들어가야하므로
	- 이런 경우 별도 객체(visitor)를 이용하는 것도 방법이다.
3. 종결 요소(terminal symbol)를 flyweight 패턴을 활용하여 공유
	- 종결 요소는 상태가 없고, 여러번 반복 사용되므로

## Sample Code
- boolean 문을 평가하는 예제
- terminal symbol : true, false
- nonterminal symbol : and, or, not

```c++
class BooleanExp { // 최상위 AbstractExpression
public:
	BooleanExp();
	virtual ~BooleanExp();
	
	virtual bool Evaluate(Context&) = 0; // 표현 평가를 위한 인터페이스
	virtual BooleanExp* Replace(const char*, BooleanExp&) = 0;
	virtual BooleanExp* Copy() const = 0;
};
```

```c++
class Context {
public:
	bool Lookup(const char*) const;
	void Assign(VariableExp*, bool);
};
```

```c++
class VariableExp : public BooleanExp { // TerminalExpression
public:
	VariableExp(const char*);
	virtual ~VariableExp();
	virtual bool Evaluate(Context&);
	virtual BooleanExp* Replace(const char*, BooleanExp&);
	virtual BooleanExp* Copy() const;
private:
	char * _name;
}

VariableExp::VariableExp(const char* name) {
	_name = strdup(name);
}

bool VariableExp::Evaluate(Context& aContext) {
	return aContext.Lookup(_name);
}

BooleanExp* VariableExp::Copy() const {
	return new VariableExp(_name);
}

BooleanExp* VariableExp::Replace(const char* name, BooleanExp& exp) {
	if (strcmp(name, _name) ==0) {
		return exp.Copy();
	} else {
		return new VariableExp(_name);
	}
}
```

```c++
class AndExp : public BooleanExp { // NonterminalExpression
public:
	AndExp(BooleanExp*, BooleanExp*);
	virtual ~AndExp();
	
	virtual bool Evaluate(Context&);
	virtual BooleanExp* Replace(const char*, BooleanExp&);
	virtual BooleanExp* Copy() const;

private:
	BooleanExp* _operand1;
	BooleanExp* _operand2;
};

AndExp::AndExp(BooleanExp* opl, BooleanExp* op2) {
	_operandl = opl;
	_operand2 = op2;
}

bool AndExp::Evaluate(Context& aContext) {
	return 
		_operandl->Evaluate(aContext) && 
		_operand2->Evaluate(aContext);
}

BooleanExp* AndExp::Copy() const {
	return new AndExp(_operandl->Copy(), _operand2->Copy());
}

BooleanExp* AndExp::Replace(const char* name, BooleanExp& exp){
	return new AndExp(_operandl->Replace(name, exp), _operand2->Replace(name, exp));
}
```

```c++
// 클라이언트
// (true and x) or (y and (not x) ) 에 대한 평가 코드
BooleanExp* expression;
Context context;

VariableExp* x = new VariableExp("X");
VariableExp* y = new VariableExp("Y");

expression = new OrExp(
	new AndExp(new Constant(true), x),
	new AndExp(y, new NotExp(x))
) ;

context.Assign(x, false);
context.Assign(y, true);

bool result = expression->Evaluate(context);

// replace
VariableExp* z = new VariableExp("Z");
NotExp not_z(z);

BooleanExp* replacement = expression->Replace("Y", not_z);

context.Assign(z, true);

result = replacement->Evaluate(context);
```

## Related Patterns
- Composite : AST도 결국 tree이므로, Composite 패턴의 한 사례임
- Flyweight : 종결 요소를 공유할 때 활용할 수 있음
- Iterator : 인터프리터가 AST를 탐색할때 iterator 패턴을 사용할 수 있음
- Visitor : AST 노드에서의 공통 연산을 단일 visitor에 포함시킬 수 있음
