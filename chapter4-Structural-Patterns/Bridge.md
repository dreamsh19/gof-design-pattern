---
created: 2025-02-22 (토) 16:37:33
modified: 2025-02-25 (화) 23:46:54
---

## Intent
- 추상화와 구현을 분리하여, 독립적으로 확장할 수 있도록 하기 위함.

## Also Known As

- handle / body
	- handle : abstraction
	- body : implementor

## Motivation
- 하나의 추상화 여러개의 구현을 가질 수 있을때 가장 흔하게 사용할 수 있는 방법 중 하나는 상속이다.
	- 추상클래스에서 (추상적인) 인터페이스를 정의하고, 구체 클래스는 이 추상클래스를 상속하여 각자 구현하는 방식
	- 그런데 상속은 추상과 구현을 강하게 결합시켜서, 추상과 구현의 독립적인 재사용을 어렵게 한다.
- X window 시스템과 PM 시스템에서 동시에 동작하는 애플리케이션을 작성하고싶다.
	- 추상 클래스 Window를 정의하고, XWindow와 PMWindow가 상속을 통해 이를 구현하는 경우
	- 새로운 종류의 window나 새로운 플랫폼으로의 확장을 하려고 하는 경우 상속의 클래스 폭발 문제를 그대로 가져간다.
	- 클라이언트 코드를 platform-dependent 하게 만든다. 클라이언트가 직접적으로 Window 구현체를 알고 생성해야한다.
- Bridge 패턴은 이러한 문제를 해결한다.
	- 추상 레이어와 구현 레이어가 각각의 독립적인 hierarchy를 갖도록 한다.
- ![Image](https://github.com/user-attachments/assets/d8c6b8d1-97e5-4987-900f-888d2160b30c)
- Window와 WindowImp는 의미상으로는 유사한 기능을 하지만, 컴파일러 입장에서 클래스간 연간관계가 전혀 없다. 이 둘을 이어주는 게 Bridge 패턴.
- Window 계층은 추상화된 Window의 확장을 담당하고,
- WindowImp 계층은 platform별 구현의 확장을 담당한다.
- 각각 확장의 영역이 독립적으로 분리되어 있다.

## Applicability
- 추상화와 구현 간의 결합을 방지하고 싶을때.
- **추상화 계층과 구현 계층이 각각 독립적으로 확장 가능할 필요가 있을때**

## Structure
![image](https://github.com/user-attachments/assets/7798990e-1bb5-4597-a480-0a5008753d57)

## Participants
- Abstraction
	- 추상화 계층을 표현하는 인터페이스
	- Implementor에 대한 참조를 가진다.
		- Abstraction은 클라이언트 요청을 Implementor에 포워딩한다.
- Implementor
	- 구현 계층을 표현하는 인터페이스
	- Abstraction과 꼭 인터페이스가 일치할 필요는 없음.
		- 대체로, Implementor는 기초적인 연산을 제공하고, Abstraction은 이 기초연산을 조합한 고수준의 연산을 정의한다.
- RefinedAbstraction
	- 추상화 계층의 확장
- ConcreteImplementor
	- 구현 계층의 확장(혹은 구현)

## Consequences
1. 추상화와 구현의 결합도를 낮춘다.
	- 런타임에 교체가 가능하다
	- 컴파일 타임 의존성을 제거한다.
		- Implementor의 구현이 변경되어도(인터페이스가 동일하다면) Client 및 Abstraction은 recompile 하지 않아도 된다.
		- 이는 서로 다른 버전간 Binary compatibility를 확보할때 중요하다.
2. 확장성 증가 : Abstraction과 Implementor의 계층의 독립적인 확장 가능

## Implementation

구현간 이슈
1. ConcreteImplementor가 하나 뿐인 경우
	- Implementor 인터페이스를 굳이 만들 필요는 없다.
	- 다만, 이때도 Abstraction과 ConcreteImplementor만 있어도 그 둘의 분리는 의미가 있다. 구현의 변경이 Abstraction의 변경을 야기하지 않으므로
2. Implementor 구현체 중 어떤 것을 언제 어떻게 선택할 것인가?
	- 디폴트 구현을 채택하고, 상황에 따라서 구현체를 변경하는 방법.
	- 추상 팩토리를 활용하는 방법.
		- Abstraction 클래스가 구현체의 종류를 알 필요가 없게 됨.
3. Implementor의 공유
	- 여러개의 Abstraction 객체가 하나의 implementor를 공유하도록 구현할 수도 있다.

## Sample Code
### Abstraction의 확장
```c++
class Window { // Abstraction
public:
	Window(View* contents);

	// requests handled by window by itself
	virtual void DrawContents();
	// ...

	// requests forwarded to implementation
	virtual void DrawRect(const Point&, const Point&);
	// ...

protected:
	Windowlmp* GetWindowImp();
	View* GetViewO;
private:
	Windowlmp* _imp;
	View* _contents; 
};
```

```c++
// Abstraction의 확장
class ApplicationWindow : public Window { 

public:
	// ...
	virtual void DrawContents();
	
	void ApplicationWindow::DrawContents () {
		GetView()->DrawOn(this);
	};
}

class IconWindow : public Window {
public:
	// ...
	virtual void DrawContents();

	void IconWindow::DrawContents() {
		Windowlmp* imp = GetWindowImp();
		if (imp != 0) {
			imp->DeviceBitmap(_bitmapName, 0.0, 0.0);
		}
	}

private:
	const char* _bitmapName;
};
```

- 각자의 방식으로 `DrawContents()` 연산을 구현한다.
- Implementor와 독립적인 Abstraction 만의 확장.

### Bridge를 통한 연결(forwarding)

```c++
void Window::DrawRect (const Point& p1, const Point& p2) { // Implementor에게 forwarding을 통한 구현
	Windowlmp* imp = GetWindowImp();
	imp->DeviceRect(pl.X(), pl.Y(), p2.X(), p2.Y());
}
```

### Implementor의 확장

```c++
class Windowlmp {
public:	
	virtual void DeviceRect(Coord, Coord, Coord, Coord) = 0 ;
	// lots more functions for drawing on windows...

protected:
	Windowlmp();
};
```

```c++
class XWindowImp : public Windowlmp {
public:
	XWindowImp();

	virtual void DeviceRect(Coord, Coord, Coord, Coord);
	// ...

	void XWindowImp::DeviceRect ( // ConcreteImplementor로서의 구현
		Coord xO, Coord yO, Coord x1, Coord y1
	) {
		int x = round(min(xO, x1));
		int y = round(min(yO, y1));
		int w = round(abs(xO - x1));
		int h = round(abs(yO - y1));
		XDrawRectangle(_dpy, _winid, _gc, x, y, w, h);
	}
private:
	// lots of X window system-specific state, including:
	Display* _dpy;
	Drawable _winid; // window id
	GC _gc; // window graphic context
};

class PMWindowlmp : public Windowlmp {
public:
	PMWindowlmp();

	virtual void DeviceRect(Coord, Coord, Coord, Coord);
	// ...
	
	void PMWindowImp::DeviceRect (  // ConcreteImplementor로서의 구현
		Coord xO, Coord yO, Coord x1, Coord y1
	) {
		Coord left = min(xO, x1);
		Coord right = max(xO, x1);
		Coord bottom = min(yO, y1);
		Coord top = max(yO, y1);
		
		PPOINTL point[4];
		point[0].x = left; point[0].y = top;
		point[1].x = right; point[1].y = top;
		point[2].x = right; point[2].y = bottom;
		point[3].x = left; point[3].y = bottom;
		if ((GpiBeginPath(_hps, 1L) == false) // or with some conditions
		) {
			// report error
		} else {
			GpiStrokePath(_hps, 1L, OL);
		}

private:
	// lots of PM window system-specific state, including:
	HPS _hps;
};
```

### ConcreteImplementor의 선택
```c++
Windowlmp* Window::GetWindowImp () {
	if (_imp ==0) {
		_imp = WindowSystemFactory::Instance()->MakeWindowImp();
	}
	return _imp;
}
```
- Abstract Factory를 활용하여 구현체 선택.
- 특정 구현체에 의존하지 않음.(구현체의 선택 책임은 Abstract Factory에게 있도록 함)

## Known Uses
- 추상화 Set에 대한 Implementor LinkedSet, HashSet
	- LinkedSet의 구현은 LinkedList에게 연산을 forwarding
	- HashSet의 구현은 HashTable에게 연산을 forwarding

## Related Patterns
- Abstract Factory : 특정 구현체에 해당하는 Bridge를 생성할 수 있다.
- Adapter : 서로 연관없는 클래스를 협동할 수 있게 한다는 점에서 궤를 같이한다.
	- 다만, adapter 패턴은 이미 설계(및 작성)가 완성된 시스템의 연결을 위해 사용된다.
	- 반대로 bridge 패턴은 설계를 시작하는 시점에 사용된다.
