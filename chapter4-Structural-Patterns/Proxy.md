---
created: 2025-03-18 (화) 22:50:58
modified: 2025-03-19 (수) 01:00:49
---

## Intent

원본 객체의 접근 제어를 위한 대리자 혹은 placeholder

## Also Known As

Surrogate (대리자)

## Motivation

- 접근 제어를 하는 이유 중 하나는 원본 객체의 생성과 초기화 비용을 실제 필요한 시점 직전까지 미루는 것.
	- 비싼 객체를 필요할때(on demand) 생성
- 문서 편집기에서 용량이 큰 이미지를 렌더링하는 예시
	- 보이기 시작하는 시점에 생성해도 됨.
	- 이때 고려할 사항들
		- 해당 이미지 대신에 무엇을 넣을 것인가?
		- 에디터에게 이미지를 on demand로 생성하는지 숨기고 싶다.(생성에 대한 최적화 로직을 상위 레벨에서 알 필요가 없음)
	- 이럴 때 Proxy를 사용할 수 있다.
	- Proxy : 실제 이미지의 대리자. 아래는 proxy의 역할
		- 실제 이미지를 필요한 시점에 생성한다.
		- 실제 이미지 생성 이후에는 모든 요청을 실제 이미지에 포워딩한다.
		- 일부 요청은 자체적으로 처리한다. 예를 들면, width & height 정보
	- ![image](https://github.com/user-attachments/assets/7e8d083f-ac13-4ac6-9e8f-873db573f92f)
		- 클라이언트(DocumentEditor)는 공통 인터페이스(Graphic)를 통해 접근한다.

## Applicability

- 단순한 객체 참조보다 객체 참조에 대한 보다 정교한 제어가 필요할 때
- 사용 예시
	1. remote proxy : 원격의 원본에 대한 로컬 대리자
	2. virtual proxy : 비싼 객체에 대한 가벼운 대리자로서, 필요 시점에 비싼 객체를 생성
	3. protection proxy : 원본 객체에 대한 접근 제어. 권한 관리
	4. smart reference : 원본 객체에 접근할 때 추가 작업을 하는 프록시 (decorator의 형태)
		 - smart pointer : 원본 객체의 참조 횟수를 저장하여, 참조가 더 이상 없을 때 원본 객체를 메모리에서 해제
		 - 영속 객체를 최초 참조 시점에 메모리에 lazy load
		 - 원본 객체에 접근 전 lock에 대한 검사 및 처리 수행

## Structure
![image](https://github.com/user-attachments/assets/5148e271-e5f8-467b-9557-5c6bc18fd298)

## Participants
- Subject
	- Proxy와 RealSubject를 상호호환 가능하게 하는 최상위 공통 인터페이스
- Proxy
	- RealSubject에 대한 참조를 갖고 있음.
		- RealSubject와 Subject 인터페이스가 동일하다면, RealSubject가 아닌 상위 인터페이스인 Subject에 대한 참조를 가질수도 있음.(Proxy가 또 다른 proxy를 참조하는 recursive한 형태도 가능)
	- Subject의 인터페이스를 동일하게 제공한다. (RealSubject를 대체 가능하도록)
	- RealSubject에 대한 접근을 제어한다. 필요에 따라 요청을 RealSubject에 포워딩한다.
	- 그 외의 책임은 proxy의 종류에 따라 달라진다.
		- remote proxy : 요청을 인코딩하고, 인코딩한 요청을 원격의 원본 객체에 포워딩하는 책임을 가짐.
		- virtual proxy : 원본 객체에 대한 부가정보를 캐싱(위 ImageProxy 예시에서의 extent)
			- 여기서 부가정보란, 원본 객체를 꼭 다 가져오지 않아도 알 수 있는 것들.
		- protection proxy : 클라이언트의 권한 체크
- RealSubject
	- proxy 뒤의 원본 객체

## Consequences
- Proxy 패턴은 객체 접근에 대한 간접 계층을 제공한다. 간접 계층의 종류는 다양하다.
	- remote proxy : 원격 객체의 존재를 숨긴다
	- virtual proxy : 필요 시점에 객체를 생성하여 성능을 최적화한다.
	- protection proxy, smart reference : 객체 접근 시 부가 기능을 수행한다.
- Copy-on-write가 대표적인 예시
	- 수정되지 않았을때는 copy 요청에 대해 실제로 reference count만 증가시킴
	- 그리고 수정이 발생했을때(on demand) 그제서야 실제로 복사를 수행함.
	- 이후에 reference count가 0이 되면 객체를 삭제함.

## Implementation
1. c++의 멤버 접근 연산자의 오버로딩을 활용한 프록시
2. Smalltalk의 `doesNotUnderstand`의 오버라이딩을 통한 프록시 구현
3. 프록시가 RealSubject의 구체 타입을 꼭 알 필요는 없음
	- 원본 객체로서 구체 타입이 아니라 추상 타입(Subject)을 참조해도 됨.
	- 다만, virtual proxy 같이 구체 타입(RealSubject)에 대한 생성 책임을 갖는 경우 구체 타입을 알아야함.

## Sample Code

Virtual proxy의 구현

```c++
class Graphic { // 최상위 Subject
public:
	virtual ~Graphic();

	virtual void Draw(const Point& at) = 0;
	virtual void HandleMouse(Event& event) = 0;

	virtual const Point& GetExtent() = 0;

	virtual void Load(istream& from) = 0;
	virtual void Save(ostream& to) = 0;

protected:
	Graphic();
};
```

```c++
class Image : public Graphic { // RealSubject
public:
	Image(const char* file);
	virtual ~Image();

	virtual void Draw(const Point& at);
	virtual void HandleMouse(Event& event);
	
	virtual const Points GetExtent();
	
	virtual void Load(istream& from);
	virtual void Save(ostream& to);

private:
	// ...
};
```

```c++
class ImageProxy : public Graphic { // Proxy
public:
	ImageProxy(const char* imageFile);
	virtual ~ImageProxy();

	virtual void Draw(const Point& at);
	virtual void HandleMouse(Event& event);

	virtual const Point& GetExtent();

	virtual void Load(istream& from);
	virtual void Save(ostream& to);

protected:
	Image* Getlmage();

private:
	Image* _image; // RealSubject에 대한 참조
	Point _extent; // extent에 대한 캐시
	char* _fileName;
};

ImageProxy::ImageProxy (const char* fileName) {
	_fileName = strdup(fileName);
	_extent = Point::Zero;
	_image = 0;
}

Image* ImageProxy::Getlmage() {
	if (_image ==0) {
		_image = new Image(_fileName); // on demand creation (virtual proxy의 구현)
	}
	return _image;
}

const Point& ImageProxy::GetExtent () { // 캐싱된 경우 캐싱된 값으로 제공
	if (_extent == Point::Zero) {
		_extent = Getlmage()->GetExtent();
	}
	return _extent; 
}

void ImageProxy::Draw (const Point& at) { // RealSubject에 포워딩
	GetImage()->Draw(at);
}

void ImageProxy::HandleMouse (Event& event) {
	Getlmage()->HandleMouse(event);
}

void ImageProxy::Save (ostream& to) {
	to << _extent << _fileName;
}

void ImageProxy::Load (istream& from) {
	from >> _extent >> _fileName;
}
```

```c++
class TextDocument { // Client
public:
	TextDocument();
	
	void Insert(Graphic*);
	// ...
};

TextDocument* text = new TextDocument;
// ...
text->Insert(new ImageProxy("image.jpg"));
```

## Related Patterns
- Adapter
	- Adapter는 원본 객체와 다른 인터페이스를 제공하지만, proxy는 원본 객체와 동일한 인터페이스를 제공함.
	- 다만, proxy도 원본 객체와 동일하지 않은(구체적으로는, 더 적은) 인터페이스를 제공할 수 있음
		- 예를 들어, protection proxy의 경우 권환이 없는 클라이언트에 대해서는 원본 객체의 일부 인터페이스(부분집합)만 지원함. (그 외에는 모두 거부)
- Decorator
	- proxy와 구현이 유사하나, 목적이 다르다.
	- decorator는 기능 혹은 책임을 추가하는 목적이고, proxy는 접근을 제어하기 위한 목적이다.
	- proxy는 종류에 따라 decorator와의 유사도가 다르다. (realSubject에 대한 참조를 갖고 있는지에 대한 관점에서 )
		- protection proxy는 decorator와 완전히 동일하게 구현될 수 있다. (기능을 추가하는 것이므로)
		- remote proxy는 realSubject에 대한 직접적인 참조를 갖고 있지 않다. (decorator와의 차이)
			- realSubject로 가는 방법만 알고 있음 (간접 참조)
		- virtual proxy는 초기화 시점에는 간접 참조만 갖고 있으나 로딩 후에는 직접 참조를 가짐.(위 두개의 하이브리드 버전)
