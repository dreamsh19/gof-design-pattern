---
created: 2025-02-18 (화) 23:22:03
modified: 2025-02-25 (화) 21:51:22
---

## Intent

- 특정 클래스의 인터페이스를 클라이언트가 기대하는 인터페이스로 변경한다.
- 이를 통해, 서로 호환되지 않는 인터페이스들을 일관된 하나의 인터페이스로 사용할 수 있게 한다.

## Also Known As
Wrapper

## Motivation
- 라이브러리 클래스가 인터페이스 호환이 되지 않아 재사용이 안될때
- 예를 들면, 범용 라이브러리는 당연히 내 애플리케이션에 특화된 인터페이스를 갖지 않는다.
	- Shape이라는 애플리케이션 한정 인터페이스
	- TextView라는 라이브러리 인터페이스(혹은 구현)
	- 이를 이어주는 TextShape = Adapter
- 구현방식은 class adapter 방식과 object adapter 방식이 가능
- Adapter는 Adaptee가 지원하는 기능을 연결만 할 필요는 없다.
	- 특정 기능만 Adpatee에게 위임하는 정도면 충분하다.
	- 지원하지 않는 기능은 자체 구현(혹은 또 다른 Adaptee에 위임)하면 된다.
	- 아래 이미지에서 TextShape은 TextView의 어댑터로서 동작하지만, 그외의 기능인 Manipulator는 다른 객체에 위임
	- ![image](https://github.com/user-attachments/assets/7d7d6b29-ceaa-406c-a29f-a093f8da5afa)

## Applicability
- 이미 존재하는 클래스를 사용하고 싶으나 인터페이스 호환이 안될때.
- 서로 관련없는 클래스와 협력할 수 있는, 재사용가능한 클래스를 만들고 싶을때.

## Structure
- class adapter

![image](https://github.com/user-attachments/assets/30a21703-5497-46e7-af20-5b375949fc60)

![Image](https://github.com/user-attachments/assets/814feef5-ee93-404c-a12d-65d23526f3aa)

- object adapter
![image](https://github.com/user-attachments/assets/d1a40c2d-c028-4124-b8b3-f73c6540f249)

## Participants
- Target : 클라이언트가 바라보는 인터페이스
- Client : Target과 소통
- Adaptee : 기존에 구현된, 호환되지 않는 인터페이스
- Adapter : Target의 구현체로서, Adaptee의 기능을 연결함.

## Collaborations
- Client -> Adapter : 클라이언트는 어댑터의 인터페이스를 호출한다.
- Adapter -> Adaptee : 위 요청에 대해 어댑터는 어댑티의 인터페이스를 호출하여 요청을 처리한다.

## Consequences
- class adapter와 object adapter는 trade-off가 있다.
- class adapter
	- 특정 구현 클래스 Adaptee를 상속한다.
		- 그렇기 때문에 연결하고자 하는 Adaptee의 서브클래스가 여러개인 경우 사용할 수 없다. (Adaptee의 다른 서브클래스들은 Adapter와 형제관계이다.)
		- 즉, 최대 하나(=상속한 특정 구현 Adaptee)의 Adaptee만 연결 가능하다
	- Adaptee의 구현을 오버라이딩할 수 있다.
	- 한 개의 객체만 있으면 된다. (Adapter가 Adaptee를 상속했기 때문에 자기 자신이 Adaptee 그 자체임)
		- Adaptee에 대한 별도 참조를 저장할 필요가 없다.
- object adapter
	- 단일 Adapter : 다수 adaptee(1:N) 조합이 가능하다. 어댑티들이 모두 동일 클래스의 서브클래스이더라도.
	- Adaptee의 연산을 오버라이딩하기 어렵다.
		- Adaptee 오버라이딩한 서브클래스(서브어댑티)를 만들고, Adapter는 서브어댑티를 참조하는 방식으로 해야한다.
- 어댑터 패턴 사용시 고려할 점들
	1. 어댑터가 개입하는 작업의 범위가 얼마나 되는가?
		- 어댑티의 인터페이스를 단순 변환하는 것부터
		- 어댑티의 인터페이스를 이용하여 아예 새로운 연산을 지원하는 것까지 다양할 수 있다.
		- 그리고 범위는 Target과 Adaptee가 얼마나 유사한지에 달려 있다. 유사할수록 Adapter가 할일이 적다.
	2. pluggable adapter(Adapter 클래스의 다형성)
		- 범용 TreeDisplay 라이브러리를 떠올려보자.
		- 애플리케이션마다 Tree의 구현체는 다양할 수 있고, 그 구현체들은 서로 전혀 호환되지 않는게 일반적이다.
		- 이때, 호환되지 않는 임의의 Tree 구현체에 대해, TreeDisplay는 단순히 Tree 형태의 자료구조(실제로 어떻게 구현했든)를 그리는 기능만 지원하고 싶다.
			- 참고로 임의의 구현체가 항상 고정된 Tree라는 라이브러리 내부적으로 사용하는 Abstract class를 구현하도록 하는 건 재사용성이 떨어진다.
		- 이를 위해 Adapter만 갈아끼우면서 TreeDisplay를 재사용하고 싶은 니즈.
		- 결국 한가지 Adaptee가 아닌, 서로 연관이 전혀 없는 Adaptee 들에 대한 지원을 각각에 맞는 Adapter로 갈아끼움으러써 지원하는 방법에 대한 논의
		- 자세한 내용은 구현부 참고
	3. two-way adapter for transparency
		- 어댑터의 문제는 "모든" 클라이언트에게 투명하지 않다는 것이다.
		- 어댑터를 적용하는 순간 어댑터의 인터페이스만 사용할 수 있고, Adaptee의 인터페이스는 클라이언트로부터 숨겨진다.
		- Two-way adapter는 이 문제를 해결한다.
			- 서로 다른 클라이언트가 서로 다른 인터페이스를 한 클래스를 통해 바라볼 수 있도록 한다.
		- ![image](https://github.com/user-attachments/assets/c6a9cba1-3ea0-4561-8061-b99a8c987781)
			- ConstaintStateVariable = two-way adapter
				- ConstraintVariable와 StateVariable를 둘 다 구현(혹은 상속)한다.
				- `ConstaintStateVariable.toConstraintVariable()`, `ConstaintStateVariable.toStateVariable()` 연산을 지원.
				- ConstraintVariable <-> StateVariable 간 양방향 전환이 가능하도록 한다.

## Implementation

구현간 이슈
1. c++에서의 클래스 어댑터 구현
	- class adapter의 구현에서 Adapter는 Target은 public 상속하지만, Adaptee는 private 상속한다.
	- 즉, Adapter는 Target의 서브타입이지만, Adaptee의 서브타입은 아니다.
2. pluggable adapter
	- pluggable adapter의 세가지 구현
	- 가장 먼저 Adaptee의 "narrow interface"를 찾아야한다.
		- narrow interface : Adaptation을 위한 연산의 최소 부분 교집합
		- Adaptee는 임의의 Tree 구현체
		- 결국 간극이 발생하는 부분을 인터페이스로 분리하겠다는 의미.
		- 최소한의 인터페이스는 아래 두개면 충분하다.
			- 1. 노드에 해당하는 객체를 어떻게 그릴 것인지.
			- 2. 노드의 자식을 어떻게 가져올 것인지.
	1. 추상 메서드 이용
		- narrow interface를 추상 메서드로 정의한다.
	2. 위임 객체 이용
		- 1번의 추상 메서드를 상속하여 구현하는 대신,
		- 해당 메서드를 구현한 위임 객체를 주입하여 forwarding하는 형태
	3. 어댑터를 파라미터로 전달
		- 어댑터가 하는일(Adaptee의 인터페이스를 Target의 인터페이스로 변환)은 결국 함수이므로, 함수를 파라미터로 전달하는 방법
		- 서브클래싱이 없이 구현이 가능하다.
		```java
		class TreeNode {
		    List<TreeNode> getSubdirectories() {
		        // Implementation to get child nodes
		        return List.of();
		    }
		
		    GraphicNode createGraphicNode() {
		        // Implementation to create graphic node
		        return new GraphicNode();
		    }
		}

		class TreeDisplay {
		    private final TreeNode root;
		    private final Function<TreeNode, List<TreeNode>> getChildren;
		    private final Function<TreeNode, GraphicNode> createGraphicNode;
		
		    public TreeDisplay(TreeNode root,
		                       Function<TreeNode, List<TreeNode>> getChildren,
		                       Function<TreeNode, GraphicNode> createGraphicNode) {
		        this.root = root;
		        this.getChildren = getChildren;
		        this.createGraphicNode = createGraphicNode;
		    }
		
		    public static TreeDisplay on(TreeNode root) {
		        return new TreeDisplay(root, root::getSubdirectories, root::createGraphicNode);
		    }
		}
		```

## Sample Code

```c++
class Shape { // Target
public:
	Shape();
	virtual void BoundingBox(
		Point& bottomLeft, Point& topRight
	) const;
	virtual Manipulator* CreateManipulator() const;
};

class TextView { // Adaptee
public:
	TextView();
	void GetOrigin(Coord& x, Coord& y) const;
	void GetExtent(Coord& width, Coord& height) const;
	virtual bool IsEmpty() const;
}
```

- class adapter
```c++
class TextShape : public Shape, private TextView {
public:
	TextShape () ;
	virtual void BoundingBox (
		Point& bottombeft, Point& topright
	) const;
	virtual bool IsEmpty() const;
	virtual Manipulator* CreateManipulator() const;

	void TextShape::BoundingBox (
		Point& bottombeft, Point& topright
	) const {
		Coord bottom, left, width, height;
		
		GetOrigin(bottom, left);
		GetExtent(width, height);
		
		bottomLeft = Point(bottom, left);
		topRight = Point(bottom + height, left + width);
	}
	
};
```
- object adapter
```c++
class TextShape : public Shape {
public:
	TextShape(TextView*);

	virtual void BoundingBox(
		Point& bottomLeft, Point& topRight
	) const;

	virtual bool IsEmpty() const;
	virtual Manipulator* CreateManipulator() const;
private:
	TextView* _text;

	TextShape::TextShape(TextView* t){
		_text = t;
	}

	void TextShape::BoundingBox (
		Point& bottomLeft, Point& topRight
	) const {
		Coord bottom, left, width, height;
		
		_text->GetOrigin(bottom, left);
		_text->GetExtent(width, height);
		
		bottomLeft = Point(bottom, left);
		topRight = Point(bottom + height, left + width);
	}
}
```

- object adapter가 class adapter보다 유연하다.
	- TextView의 서브클래스에 대해서도 동작 가능하다.
	- (class adapter는 불가능하다)

## Related Patterns
- Bridge
	- 객체 어댑터와 구조적으로 유사하지만, 의도가 다르다.
	- 브릿지 패턴은 인터페이스와 구현을 분리하기 위함이지만, 어댑터는 이미 존재하는 객체의 인터페이스를 변경하는 것이 목적이다.
- Decorator
	- 데코레이터는 인터페이스를 변경하지 않고 확장한다.
	- 따라서 어댑터보다 클라이언트에게 투명함을 제공하며,
	- 재귀적 합성을 가능하게 한다.(어댑터에서는 불가능함. 인터페이스가 변경되기 때문에)
- Proxy
	- 다른 객체의 대리로서 동작하고, 대리하는 객체의 인터페이스를 변경하지 않는다.
