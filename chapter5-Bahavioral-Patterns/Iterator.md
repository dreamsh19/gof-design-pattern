---
created: 2025-04-15 (화) 22:29:23
modified: 2025-04-16 (수) 00:59:43
---

## Intent
aggregate(컬렉션) 객체 내부 요소를 순회할 수 있는 방법을 제공한다. (객체 내부의 구현을 노출시키지 않으면서)

## Also Known As
Cursor

## Motivation
- aggregate 객체와 순회가 독립적으로 동작해야하는 이유들
	1. aggregate 객체는 내부 구조를 노출시키지 않으면서 요소를 순회할 수 있는 방법이 필요하다.
		- 클라이언트는 사실 내부 구조를 알 필요가 없고, 클라이언트 코드의 변경없이 내부 구조 변경이 가능한 것이 좋다.
	2. 동일한 aggregate 객체에 대해 니즈에 따라 여러가지 순회 방법(역순, 필터 등)을 제공해야할 수도 있다.
		- 하지만 그때마다 니즈에 맞는 순회 방법을 aggregate 객체에 추가하는 건 aggregate 객체를 비대하게 만든다.
	3. 그리고 동일한 aggregate 객체에 대해 동시에 여러개의 순회 작업이 진행중인 상황이 필요할 수도 있다.
- 위와 같은 이유로 인해 iterator 패턴이 필요하다.
- iterator의 핵심은 "순회만을 위한 책임"을 원본 aggregate 객체와 분리시키는 것이다.
- List와 SkipList의 예시
	- ![Image](https://github.com/user-attachments/assets/f940cd08-6399-4300-bc45-837400364967)
	- List와 SkipList는 각자의 고유한 순회 로직을 가져간다.
		- 따라서 각자의 고유한 iterator를 생성하는 책임을 갖는다. (CreateIterator)
			- Factory Method 패턴의 예시
	- 클라이언트는 내부 구현체와 무관하게 Iterator의 인터페이스를 활용하면 된다.

## Applicability

- aggregate 객체의 내부 구조 노출 없이 요소들에 접근해야할때
- aggregate 객체에 대해 여러가지 순회 방법을 지원해야할 때
- 서로 다른 내부 구조를 갖더라도 일관된 순회 인터페이스를 제공하기 위해(polymorphic iteration)

## Structure
![Image](https://github.com/user-attachments/assets/1f726a1c-0418-4843-884b-b569ce4ea4a3)

## Participants
- Iterator
	- 순회를 위한 인터페이스를 정의
- ConcreteIterator
	- 구체적인 순회 로직을 구현
	- 순회 진행 중 현재 위치를 저장하고, 다음 위치를 계산할 수 있음.
- Aggregate
	- 순회 대상이 되는 객체
	- CreateIterator(iterator를 생성하기 위한 인터페이스)를 정의
- ConcreteAggregate
	- 적합한 ConcreteIterator를 생성하도록 CreateIterator()를 구현

## Consequences
- 동일한 aggregate 객체에 대해 다양한 순회 알고리즘을 지원할 수 있다.
	- 그리고 순회 알고리즘 간 전환 및 교체가 용이하다.(Iterator 구현체만 바꿔끼면 됨)
	- 새로운 순회 알고리즘 도입도 용이하다.
- aggregate 객체가 비대해지는 것을 막는다.
- 순회는 상태를 가져야하므로, 별도의 iterator 객체를 만듦으로써 동시에 여러개의 순회가 가능하게 한다.
	- 원본 aggregate 객체에 순회가 내장되어있으면 최대 한 개의 상태밖에 갖지 못함.

## Implementation

구현이 아주 다양함.

1. 누가 순회를 제어할 것인가 : 클라이언트 vs iterator
	- external iterator: 클라이언트가 직접 제어
		- 직접 다음 요소를 명시적으로 요청하면서 순회함.
	- internal iterator : iterator가 제어
		- 클라이언트는 iterator 에게 연산을 전달하고, iterator가 알아서 순회하면서 연산을 수행하는 방식
	- external iterator가 더 유연함.
2. 누가 순회 알고리즘을 정의할 것인가? : iterator vs 원본 aggregate 객체
	- aggregate 객체가 순회 알고리즘을 관리하고, iterator는 단순히 "현재 상태"만 저장하는 방식의 구현도 가능함.
		- 이걸 cursor라고 함.
	- iterator가 순회 알고리즘에 대한 책임을 갖는 구현의 경우
		- 동일 aggregate에 대해 다양한 순회 알고리즘 지원이 가능하고,
		- 다른 aggregate에 대해 동일한 알고리즘을 재사용이 가능함.
		- 다만, iterator가 원본 aggregate의 private 요소에 접근해야할수도 있음.
3. iterator의 robustness
	- robust iterator : 순회 중 원본 aggregate의 수정(삽입, 제거 등)이 순회에 영향을 받지 않는 iterator
		- 대부분은 원본 aggregate 객체에서 수정이 발생했을때 iterator의 상태도 같이 업데이트해주는 방식으로 구현.
4. 부가적인 순회 기능 지원
	- previous(), skipTo()
5. iterator와 aggregate는 강결합이 되어있다.
	- iterator는 aggregate에 대해 아주 높은 권한을 갖는다. (c++의 friend)
	- 이는 새로운 종류의 iterator를 추가하기 어렵게 만들 수 있다.
6. Composite의 iterator
	- Composite 과 같이 재귀적인 구조를 갖는 객체의 external iterator는 구현이 까다로움
	- 현재 위치만 저장해서는 추적이 어렵기 때문에 재귀호출의 경로를 같이 저장해야함.
	- 대안은
		- internal iterator를 활용하는 것도 방법이고,
		- cursor 형태의 iterator(순회 알고리즘을 원본 객체가 갖고 iterator는 현재 위치만 저장)를 활용
7. Null iterator
	- isDone()이 항상 true인 iterator
	- 용도 : tree 형태의 aggregate에서 순회할 자식이 없는 리프 노드에 대해서도 일관적인 iterator 인터페이스를 적용할때 유용하다.

## Sample Code

```c++
template <class Item>
class List { // 원본 aggregate
public:
	List(long size = DEFAULT_LIST_CAPACITY);
	long Count() const;
	Item& Get(long index) const;
// . . .
};
```

```c++
template <class Item>
class Iterator {
public:
	virtual void First() = 0;
	virtual void Next() = 0;
	virtual bool IsDone() const = 0;
	virtual Item Currentltem() const = 0;
protected:
	Iterator();
};
```

```c++
template <class Item>
class Listlterator : public Iterator<Item> { // ConcreteIterator
public:
	Listlterator(const List<Item>* aList);
	
	virtual void First();
	virtual void Next();
	virtual bool IsDone() const;
	virtual Item Currentltem() const;
private:
	const List<Item>* _list;
	long _current;
};

template <class Item>
Listlterator<ltem>::Listlterator (const List<Item>* aList) : _list(aList), _current(0) {
}

template <class Item>
void Listlterator<ltem>::First(){
	_current = 0;
}

template <class Item>
void Listlterator<ltem>::Next() {
	_current++;
}

template <class Item>
bool Listlterator<ltem>::IsDone() const {
	return _current >= _list->Count();
}

template <class Item>
Item Listlterator<ltem>::CurrentItem () const {
	if (IsDone()) {
		throw IteratorOutOfBounds;
	}
	return _list->Get(_current);
}
```
- ReverseIterator의 구현은 First()와 Next() 구현부만 바꾸면 됨.

```c++
// 클라이언트
void PrintEmployees (Iterator<Employee*>& i) {
	for (i.First(); !i.IsDone(); i.Next()) {
		i.Currentltem()->Print();
	}
}

List<Employee*>* employees;
// . . .

ListIterator<Employee*> forward(employees);
ReverseListIterator<Employee*> backward(employees);

PrintEmployees(forward); 
PrintEmployees(backward);
// 동일한 aggregate에 대해 iterator의 변경만으로 순회 알고리즘 변경
```

- List의 특정 구현에 종속되지 않은 순회

```c++
template <class Item>
class AbstractList {
public:
// . . .
	virtual Iterator<Item>* Createlterator() const = 0;
}
```

```c++
template <class Item>
Iterator<Item>* List<Item>::CreateIterator() const {
	return new Listlterator<ltem>(this);
}

template <class Item>
Iterator<Item>* SkipList<Item>::CreateIterator() const {
	return new SkipListlterator<ltem>(this);
}
```

```c++
// 클라이언트
AbstractList<Employee*>* employees; // 특정 구현에 종속되지 않음.
// . . .
Iterator<Employee*>* iterator = employees->CreateIterator();
PrintEmployees(*iterator);
delete iterator;
```

- iterator에 대한 메모리 정리 : iterator에 대한 proxy(IteratorPtr)를 사용
	- c++의 경우 proxy를 통해 스택이 사라질때 자동으로 정리되도록함.

```c++
template <class Item>
class IteratorPtr {
public:
	IteratorPtr(Iterator<Item>* i): _i(i) { }
	~IteratorPtr() { delete _i; }
	Iterator<Item>* operator->() { return _i; }
	Iterator<Item>& operator*() { return *_i; }
private:
	// disallow copy and assignment to avoid multiple deletions of _i:
	IteratorPtr(const IteratorPtr&);
	IteratorPtr& operator=(const IteratorPtr&);
private:
	Iterator<Item>* _i;
};
```

- internal iterator(passive iterator)
```c++
template <class Item>
class ListTraverser { // internal iterator
public:
	ListTraverser(List<Item>* aList);
	bool Traverse();
protected:
	virtual bool Processltem(const Item&) = 0; // 순회 시 마다 수행할 연산
private:
	Listlterator<ltem> _iterator;
};

template <class Item>
ListTraverser<Item>::ListTraverser (
	List<Item>* aList
) : _iterator(aList) { }

template <class Item>
bool ListTraverser<Item>::Traverse () { // 순회에 대한 제어를 내부에서 함.
	bool result = false;
	for (_iterator.First();!_iterator.IsDone();_iterator.Next()) {
		result = Processltem(_iterator.Currentltem());
		if (result == false) {
			break;
		}
	}
	return result;
}
```

```c++
class PrintNEmployees : public ListTraverser<Employee*> { // subclassing을 통한 구현
public:
	PrintNEmployees(List<Employee*>* aList, int n):
		ListTraverser<Employee*>(aList),
		_total(n), _count(0) { }

protected:
	bool Processltem(Employee* const&);
private:
	int _total;
	int _count;
};

bool PrintNEmployees::Processltem (Employee* const& e) {
	_count++;
	e->Print();
	return _count < _total;
}
```

```c++
// 클라이언트
List<Employee*>* employees;
// . . .
PrintNEmployees pa(employees, 10) // 클라이언트가 순회에 대한 제어를 하지 않음.
pa.Traverse();
```

```c++
template <class Item>
class FilteringListTraverser { // internal iterator 또 다른 구현. 순회 알고리즘을 캡슐화할 수 있음.
public:
	FilteringListTraverser(List<Item>* aList);
	bool Traverse();
protected:
	virtual bool Processltem(const Item&) = 0;
	virtual bool Testltem(const Item&) = 0;
private:
	Listlterator<ltem> _iterator;
};

template <class Item>
void FilteringListTraverser<Item>::Traverse () {
	bool result = false;
	for (_iterator.First();!_iterator.IsDone();_iterator.Next()) {
		if (Testltem(_iterator.Currentltem())) {
			result = Processltem(_iterator.Currentltem());
			if (result == false) {
				break;
			}
		}
	}
	return result;
}
```

## Related Patterns
- Composite : iterator는 composite 과 같은 재귀적 구조에 종종 적용됨.
- Factory Method : polymorphic iterator는 적절한 iterator를 생성하기 위해 팩토리 메서드를 활용한다.
- Memento : 순회 중 상태 저장을 위해 종종 iterator는 내부적으로 memento를 활용한다.
