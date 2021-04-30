# Oscar's repo

1. [Shorten url](#1.-Shorten url) (w/ Spring boot, MariaDB, Redis)
2. [Pubsub](#2.-Pubsub) (w/ Spring boot, Redis)
3. [Iterator, Circular queue](#3.-Iterator,-Circular-queue) (w/ java)
4. [Api call limit](#4.-Api-call-limit) (w/ Spring boot, MariaDB, Redis)
5. [Kotlin porting from java spring boot prjs](#5.-Kotlin)

<br>

# **1. Shorten url**

## **목표**

- url이 주어지면 해당 url로 redirect 할 수 있는 단축 url을 생성 후 반환
- 같은 url이 주어지더라도 서로 다른 단축 url 반환
- 단축 url을 통해 몇 번이나 redirect 하였는지 조회 기능
- `mapstruct`를 사용한 mapper 구조

## **핵심**

1. 단축 url 생성 알고리즘
    1. 원본 url을 shorten 테이블에 저장
    2. shorten 테이블에 저장된 원본 url의 id 값을 get
    3. id 값에 salt(random number)를 concat 하여 [base62](https://en.wikipedia.org/wiki/Base62)로 인코딩 (사용 lib: [seruco/base62](https://github.com/seruco/base62))
    4. 인코딩된 값(단축 url) 반환
2. redirect count, redirect url caching
    - redis
        - redis에는 [Hashes](https://www.tutorialspoint.com/redis/redis_hashes.htm) 형식으로 저장
        - [단축 url]:[원본 url]:[redirect count]
    - redis에 단축 url이 있을 때
        1. redis에서 해당 단축 url이 가르키고 있는 원본 url 이나 redirect count 수를 반환
    - redis에 단축 url이 없을 때
        1. 단축 url을 디코딩
        2. 디코딩된 값에서 id 추출
        3. 추출된 id를 사용해 shorten 테이블에서 원본 url 조회
        4. redis에 [단축 url]:[원본 url]:[redirect count] 을 저장한다. 이 때, redirect count는 1
        5. 원본 url을 response header에 `Location` 키의 value로 삽입하고 http status code를 301로 세팅 후 반환
    - 1분마다 redis에 저장된 redirect count를 counts 테이블에 저장

        redirect가 일어날 때마다 counts 테이블에 update를 한다면 부하가 너무 심하다. 따라서 redirect 수를 redis에서 update 하고 있다가 1분이 지나면 counts 테이블에 반영시키고 redis의 count는 0으로 되돌린다.

## Logging

**피드백**

- 명시적 락, 묵시적 락, isolation level

- ~~http status code 어떻게 동작하는지 숙지~~

    301에 redirect url 넣어서 던졌으면 더 좋았을듯

- cacheable 어떻게 동작하는지 알고 쓸 것

- ~~Redis caching 설정 필수 (캐시 만료 시간 등등)~~

- ~~DB exception handling 필요~~

- 프로젝트 설명할때, api부터 기능, 메소드, input, output 등 필수 요소부터 설명

- ~~url encoding 과정에서 문제~~

    1. long overflow
    2. 무한 loop
    3. 중복된 shorten url
        1. 로직상의 문제
            - hashcode가 음수도 반환하기 때문에 과거도 조회 가능해짐
            - String attach가 좋아 보임
            - or 64bit + 64bit → 128bit big number 표현도 가능
        2. DB 저장상의 문제
            - unique index로 해결 가능

**피드백 수정**

1. response redirect 하도록 변경
2. shorten url encoding 로직 버그 수정
3. shorten url db 저장 로직 변경
    - db에는 url 테이블만 존재
    - shorten url을 반환할 때에는 저장된 url의 id값을 encoding하여 반환
4. controller단에서 service로부터 오는 에러 catch 하도록 변경
5. redirect count 조회 기능 추가
    1. redirect count는 redis에 저장
    2. 1분마다 redis에 쌓인 count 수를 DB에 저장
    3. DB에 저장 후 redis에 쌓인 정보 clear

**2차 피드백**

- 최신? Spring Boot 부터는 생성자에서 인자로 받는 객체는 알아서 Spring이 넣어줌 (Autowired 필요 없음)

- Type-safe를 신경써서 코딩할것. 남도 본다는 것을 항상 숙지

- application.yml 설정 뜻 숙지할 것

    `spring.jpa.open-in-view: false`

    `spring.jpa.generate-ddl: true`

    `spring.jpa.show-sql: true`

    `spring.jpa.hibernate.ddl-auto: update`

    - `spring.jpa.hibernate.ddl-auto` (= hibernate.hbm2ddl.auto) : 안전을 위해 항상 꺼야함

        매핑된 객체의 값이 변경됐을때 자동으로 반영이 아니라, 
    스프링 실행시 스키마가 변경됐을때 자동으로 반영이다.

        [Common Application properties](https://docs.spring.io/spring-boot/docs/current/reference/html/appendix-application-properties.html)

        [Spring에서 JPA / Hibernate 초기화 전략](https://pravusid.kr/java/2018/10/10/spring-database-initialization.html)

        [hibernate.hbm2ddl.auto 위험 헷지](https://github.com/HomoEfficio/dev-tips/blob/master/hibernate.hbm2ddl.auto%20%EC%9C%84%ED%97%98%20%ED%97%B7%EC%A7%80.md)

- 간단한 Optional 구문은 lambda로 바꾸면 가독성 up, but, 유지보수 관점 생각해서 적절히

- redis에 임시로 쌓이는 count는 제거하지 말고 0을 만들것

    → redis에서는 transaction이 없고 일괄처리만 있음 (multi)

    → watch를 통해 CAS는 사용 가능하지만 번거로움

- StringEncoderService로 비즈니스 로직 분리하는 것이 좋음

- redis에서 value 가져올때 int로 가져오지 못하는 부분

    → serializer가 StringRedisSerializer()로 선언되어 있기 때문에 String으로 저장되어 있었음

- Counts 조회시 index 사용하기 (@Table → @Index Annotation)

<br>

# 2. Pubsub

Spring Integration; Spring에서 lightweight messaging을 가능하게 하고 외부 어플리케이션과 통신할 수 있는 수단을 제공한다

[Spring Integration](https://spring.io/projects/spring-integration)

[](https://docs.spring.io/spring-integration/reference/html/redis.html)

Producer-Consumer Pattern과 비슷하지만, 해당 패턴의 경우 다음의 문제점이 있음

1. 여러 consumer가 queue의 자원에 동시에 접근할 경우 문제가 발생할 수 있음
    - 세마포어와 모니터로 해결한다고 하지만 근본적인 해결책은 아님
2. consumer가 가만히 있다가 msg를 받는 것이 아니라, msg를 queue로부터 요청해야함

따라서 별도의 어플리케이션을 두고 이것이 메세징을 담당하도록함 → pub-sub에서의 broker

[Redis - spring-data-redis : 발행/구독(pub/sub) 모델의 구현](https://daddyprogrammer.org/post/3688/redis-spring-data-redis-publish-subscribe/)

[Publish/Subscribe vs Producer/Consumer?](https://stackoverflow.com/questions/42471870/publish-subscribe-vs-producer-consumer)

[Java(자바) BlockingQueue - Producer 및 Consumer 패턴 예제](https://niceman.tistory.com/94)

<br>

# 3. Iterator, Circular queue

- iterator는 해당 collection 객체를 참조한다. (shallow copy)

    → 값을 조작하려면 객체 내부값(필드)을 조작해야 한다.

- iterator 사용시 collection 객체에 대해 add나 remove를 막아놓은 이유

    → iterator가 참조하던 위치가 틀어지기 때문

    → 막는 방법: iterator.next() 시에 collection 객체의 변동이 발생했는지 modCount를 감지

    → modCount 변경시 Exception 발생

- 다른 대안으로는 CopyOnWriteArrayList 가 있음

    → iterator 생성시 원본 collection의 스냅샷을 찍어 반영

    → 매 iterator 생성시 객체의 생성이 발생하기 때문에 느림

    → 대신 thread-safe를 보장 보장

![image](https://user-images.githubusercontent.com/41278416/116643460-14996b80-a9ac-11eb-902e-a677b6237b8f.png)

[How to change value of ArrayList element in java](https://stackoverflow.com/questions/12772443/how-to-change-value-of-arraylist-element-in-java)

[Iterator의 내부동작과 ConcurrentModificationException을 피하는 방법](https://brandpark.github.io/java/2021/01/24/iterator.html)

[Thread Safety and how to achieve it in Java - GeeksforGeeks](https://www.geeksforgeeks.org/thread-safety-and-how-to-achieve-it-in-java/)

[참고) List-Concurrent-List-만들기](https://okihouse.tistory.com/entry/List-Concurrent-List-%EB%A7%8C%EB%93%A4%EA%B8%B0)

Stream

- [Java 스트림 Stream (1) 총정리](https://futurecreator.github.io/2018/08/26/java-8-streams/)

- [자바 스트림(Stream) API 정리, 스트림을 이용한 가독성 좋은 코드 만들기(feat. 자바 람다, 함수형 프로그래밍, 자바8)](https://jeong-pro.tistory.com/165)

[for-loop 를 Stream.forEach() 로 바꾸지 말아야 할 3가지 이유](https://homoefficio.github.io/2016/06/26/for-loop-%EB%A5%BC-Stream-forEach-%EB%A1%9C-%EB%B0%94%EA%BE%B8%EC%A7%80-%EB%A7%90%EC%95%84%EC%95%BC-%ED%95%A0-3%EA%B0%80%EC%A7%80-%EC%9D%B4%EC%9C%A0/)

**피드백**
- 변수명; 과제 할 때도 운영코드처럼 짓기. ex Queue 구현시 frond, back의 의미 모호. qFrontIdx 등으로 지을 것.

- Queue에서 lookup같은 함수는 존재해서는 안 됨; Queue의 정체성에 어긋남. 마찬가지로 과제할때도 운영처럼.

- 구글링할때 되도록이면 최신 자료 찾아볼것. 1년 이내 추천.

- listIterator

- deep copy

<br>

# 4. Api call limit

- **자료조사**
    - Techniques for enforcing rate limits (비율 제한을 적용하는 기법)
        - **토큰 버킷**: [토큰 버킷](https://wikipedia.org/wiki/Token_bucket)은 롤링 및 누적 사용 예산을 *토큰*의 잔액으로 설정합니다. 이 기법은 서비스에 대한 모든 입력이 요청과 1:1로 대응하지는 않는다는 점을 전제로 합니다. 토큰 버킷은 토큰을 일정한 비율로 추가합니다. 서비스 요청이 이루어지면 서비스는 요청을 이행하기 위해 토큰을 인출하려고 합니다(토큰 수 감소). 버킷에 토큰이 없으면 서비스가 한계에 도달하고 백프레셔로 응답합니다. 예를 들면 GraphQL 서비스에서 단일 요청으로 인해 여러 작업이 하나의 결과로 구성될 수 있습니다. 이러한 작업은 각각 하나의 토큰을 사용할 수 있습니다. 이러한 방식으로 서비스는 비율 제한 기법을 요청에 직접 연결하는 대신 사용을 제한하는 데 필요한 용량을 추적할 수 있습니다.
        - **Leaky Bucket**: [Leaky Bucket](https://wikipedia.org/wiki/Leaky_bucket)은 토큰 버킷과 비슷하지만 비율이 버킷에서 떨어지거나 새는 양으로 제한됩니다. 이 기법은 시스템이 서비스를 수행할 수 있을 때까지 요청을 보유할 수 있는 어느 정도의 유한한 용량을 가지고 있음을 전제로 합니다. 여분은 단순히 경계를 넘어 흘러넘치고 삭제됩니다. 이러한 버퍼 용량 개념(Leacky Bucket의 사용이 필수는 아님)은 부하 분산기 및 디스크 I/O 버퍼처럼 서비스에 인접한 구성요소에도 적용됩니다.
        - **고정 기간**: 고정 기간 제한(예: 시간당 3,000개의 요청 또는 하루에 10개의 요청)은 쉽게 지정할 수 있지만, 기간의 경계에서 사용 가능한 할당량 재설정으로 급증할 수 있습니다. 예를 들어 시간당 3,000개의 요청 제한이 있는 경우, 한 시간의 첫 1분 동안 3,000개의 요청이 모두 발생할 수 있도록 허용하면 서비스에 문제가 발생할 수 있습니다.
        - **슬라이딩 기간**: 슬라이딩 기간은 고정 기간의 이점이 있지만, 롤링 기간은 집중을 완화합니다. Redis와 같은 시스템은 만료 키로 이 기법을 지원합니다.

    [Rate-limiting strategies and techniques | Solutions | Google Cloud](https://cloud.google.com/solutions/rate-limiting-strategies-techniques#techniques-enforcing-rate-limits)

    [서비스 가용성 확보에 필요한 Rate Limiting Algorithm에 대해](https://www.mimul.com/blog/about-rate-limit-algorithm/)

    **Leaky Bucket**

    [Rate Limiting with NGINX and NGINX Plus](https://www.nginx.com/blog/rate-limiting-nginx/)

    **Token Bucket**

    [vladimir-bukhtoyarov/bucket4j](https://github.com/vladimir-bukhtoyarov/bucket4j)

    [Bucket4j - Token bucket 알고리즘을 이용한 Rate limit 라이브러리를 알아보자!!](https://baeji77.github.io/dev/java/Java-rate-limit-bukcet4j/#bucket4j)

    [Rate limiting Spring MVC endpoints with bucket4j](https://golb.hplar.ch/2019/08/rate-limit-bucket4j.html)

    **Redis**

    [Redis rate limiter in Spring Boot](https://www.javacodemonk.com/redis-rate-limiter-in-spring-boot-6455a677)

    [Redis transaction (MULTI, EXEC)](https://redis.io/topics/transactions)

    [고 처리량 분산 비율 제한기 - LINE ENGINEERING](https://engineering.linecorp.com/ko/blog/high-throughput-distributed-rate-limiter/)

    **Lock**

    [자바의 락(Locks in Java)](https://parkcheolu.tistory.com/m/24)

    [Java Concurrency Utilities - java.util.concurrent](http://tutorials.jenkov.com/java-util-concurrent/index.html)

    [Java Spin lock implementation](https://www.tech693.com/2018/08/java-spin-lock-implementation.html?m=1)

    [Using a Mutex Object in Java | Baeldung](https://www.baeldung.com/java-mutex)

    **Actor**

    [Alpakka Documentation](https://doc.akka.io/docs/alpakka/current/spring-web.html)

- **작업내역**
    1. bucket4j
        - in-memory 기반 call limit
    2. redis
        1. key, value를 저장하면서 expire time을 1분으로 지정한다.
            - key → ip:mm (mm은 현재 분(minute))
            - value → count
    3. mutex
        1. spin-lock (V1)
            - ReentrantLock 상속받아 구현
            - lock() 호출시 다른 곳에서 락이 걸려 있으면 while 루프를 돌며 대기 → 락이 풀리면 (!isLocked()) lock()을 호출한 스레드에서 lock을 선점
        2. syncronized (V2)
            - 메모리를 조작하는 구문을 syncronized로 묶어서 사용
        3. call-counter (V1, V2 공통)

            ip별로 다른 call count를 세기 위해서 CallCounter 클래스 사용

            - CallCounter는 Map<String, List<?>> 형식의 변수(이하 userCounts)를 필드로 갖는다
                - userCounts key: {IP}:{mm}형식. mm은 현재 분(minute)
                - userCounts value: (count, expire time) 형식. List, 크기 2 고정
                    - 지금 보니 List가 아니라 객체에 데이터 담아서 사용해도 좋았을듯
            - CallCounter는 Scheduled 함수를 갖는다; removeCountAfterOneMinute
                - removeCountAfterOneMinute 함수는 1초마다 userCounts에 저장된 모든 데이터를 순회하면서 expire time을 꺼내 현재 시간과 비교한다.
                - expire time에서 60초가 지났으면 해당 key, value는 remove

<br>

# 5. Kotlin

[https://www.notion.so/Kotlin-72628d6952114aa899a8d6e6c4338282](https://www.notion.so/Kotlin-72628d6952114aa899a8d6e6c4338282)