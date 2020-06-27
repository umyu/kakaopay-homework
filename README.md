#과제 카카오페이 뿌리기 기능 구현하기

## 개발 프레임워크, 문제해결 전략, 빌드 및 실행 방법

### 개발 프레임워크
 - Spring Boot 2.3
 - Spring JPA
 - Lombok

### 문제해결 전략
 - 토큰은 중복체크를 하여 3번까지 하게 하였습니다.
    - 토큰은 숫자, 소문자, 대문자로 각각 자리수마다 존재할 수 있고, 이것 또한 WELL512 발생으로 생성하였습니다.
 - 난수 발생은 WELL512 로 하였습니다. Java Random 함수보다는 좀 더 평균적인 값을 출력해주기에 사용해보았습니다.
 - 뿌리기 금액은 난수 발생하는 것에 정렬을 하지 않고 넣었고, 사용자는 데이터 쌓이는 순서대로 가져가기 때문에 그것 또한 랜덤이라 그렇게 하였습니다.
### 빌드 및 실행 방법
 - 서버 실행을 하고 http://localhost:8080 으로 접근하시면 됩니다.

### API 목록

뿌리기 API
 - /spray/request
    - 요청 : 뿌리기 금액, 인원수
    - 응답 : 토큰
```  
    Request :
     { "amount" : 10000 , "person" : 3 }
    Response :
     ABC
```
받기 API
 - /spray/receive
    - 요청 : 토큰
    - 응답 : 받은금액
```
    Request : 
        { "token" , "ABC" }
    Response :
        10000
```
조회 API
 - /spray/receive
    - 요청 : 토큰
    - 응답 : 받은금액
```
    Request : 
        { "token" , "ABC" }
    Response :
        10000
```