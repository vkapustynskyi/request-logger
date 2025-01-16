# Spring Boot WebClient request DB logger 

Example of WebClient filter which allows you to save request and response details log in DB.\
I thought it is a trivial task, but after implementation I decided to save it for myself. Maybe you will find that usefull for yourself too.\
Good luck!

## Fields which I managed to save about the request

```java
@Entity
@Getter
@Setter
public class ApiLog {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String requestUri;

    private Instant requestTime;

    private Instant responseTime;

    private Integer statusCode;

    private String responseBody;

}
```

