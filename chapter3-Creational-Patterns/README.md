---
created: 2025-01-23 (목) 21:13:59
modified: 2025-01-23 (목) 21:56:20
---

- 생성 패턴은 인스턴스를 만드는 "과정" 자체를 추상화하는 패턴.
- 클래스 생성 패턴과 객체 생성 패턴
	- 클래스 생성 패턴 : 상속을 통해
	- 객체 생성 패턴 : 생성만을 담당하는 다른 객체에게 위임을 통해.
- 생성 패턴의 중요성
	- 클래스 상속보다는 객체의 조합이 대세가 되면서 "작은" 클래스를 만드는게 중요해졌다.
		- 복잡한 하나의 클래스보다는 작은 여러개의 클래스.
	- 그렇게 되면서, 단순히 하나의 클래스를 만드는 것보다 복잡한 것을 요구하게 되었고, 그에 따라 생성 패턴이 중요해짐.
- 생성 패턴에 대한 두가지 이야기
	1. concrete class에 대한 정보를 숨긴다.
	2. 인스턴스들이 어떻게 생성되고, 조합되는지 숨긴다.
- 결국, 시스템으로부터 어떤 인스턴스가 어떻게 생성되는지에 대한 정보를 숨긴다.
	- 시스템은 그들의 인터페이스(추상 클래스)만을 알고 있을 뿐이다.
	- 생성될 product에 대해서, product를 사용하는 쪽(시스템)과 product를 생성하는 쪽(생성 패턴)을 분리하는 목적.
- 3장에서는 "미로를 만드는 문제"라는 동일한 문제에 대해 5개의 생성 패턴을 적용하는 방식으로 내용을 전개할 예정
- 이때 미로의 정의 = 방(Room)의 집합.
	- 기본 구성 요소 : MapSite
		- 구현체 : Room, Door, Wall
	- 각 Room은 상하좌우에 MapSite(Room, Door, Wall)를 가질 수 있다.
	- ![[Pasted image 20250123212158.png]]

- 개선하고자 하는 원본 코드.

```c++
Maze* MazeGame::CreateMaze () {
	Maze* aMaze = new Maze;
	Room* rl = new Room(1);
	Room* r2 = new Room(2);
	Door* theDoor = new Door(rl, r2);

	aMaze->AddRoom(rl);
	aMaze->AddRoom(r2);

	rl->SetSide(North, new Wall);
	rl->SetSide(East, theDoor);
	rl->SetSide(South, new Wall);
	rl->SetSide(West, new Wall);
	
	r2->SetSide(North, new Wall);
	r2->SetSide(East, new Wall);
	r2->SetSide(South, new Wall);
	r2->SetSide(West, theDoor);
	
	return aMaze;
}
```

- **클래스의 인스턴스를 직접 생성하도록 하는 것이 가장 큰 문제임. 유연성이 떨어짐**
- 위와 같은 미로의 구성요소를 정의하는 클래스를 쉽게 변경할 수 있는 방법을 생성 패턴으로 통해 해결하고자 한다.
- 개선 예시
	1. Factory Method 패턴 : 직접적인 생성자가 아닌 가상 함수(인터페이스)를 통해 생성한다면, 생성 방법에 대한 구현의 유연성을 가져갈 수 있다.
	2. Abstract Factory 패턴 : 팩토리(구성 요소의 "군"들에 대한 생성을 담당하는)를 파라미터로 받을 수 있다면, 구성 요소의 군들에 대한 생성 유형을 한꺼번에 변경할 수 있다.
	3. Builder 패턴 : 빌더(새로운 미로를 만들 수 있는 방법을 담당하는 객체)를 파라미터로 받을 수 있다면, 이 빌더의 상속을 통해 미로 생성의 유연성을 가져갈 수 있다.
	4. Prototype 패턴 : 기존에 만들어둔 Room, Door, Wall 객체를 복사하여 사용함으로써, 이 객체의 변경을 통해 미로의 조합을 변경할 수 있다.
- 5. Singleton 패턴
	- 오로지 하나의 미로 객체만 존재할 수 있고, 모든 객체가 글로벌하게 접근가능하도록 보장함.
	- 기존 코드 변경없이 대체하거나, 확장 가능하도록 함.
