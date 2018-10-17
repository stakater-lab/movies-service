## Building

`mvn spring-boot:run`

## Neo4J

To run neo4j:

```
docker run \
    --publish=7474:7474 --publish=7687:7687 \
    --volume=$HOME/neo4j/data:/data \
    --env=NEO4J_AUTH=none \
    neo4j
```

which allows you to access neo4j through your browser at http://localhost:7474.

This binds two ports (7474 and 7687) for HTTP and Bolt access to the Neo4j API. A volume is bound to /data to allow the database to be persisted outside the container.

By default, this requires you to login with neo4j/neo4j and change the password. You can, for development purposes, disable authentication by passing `--env=NEO4J_AUTH=none` to docker run.

## TODO's

- [X] Add some data on startup / Load test data - DONE
- [ ] Fix path of Neo4j repository path as it prints currently No identity field found for class of type: com.stakater.lab.movieservice.security.ResourceServerConfig when creating persistent property for : private java.lang.String com.stakater.lab.movieservice.security.ResourceServerConfig.jwtPublicKey
- [ ] Fix discoverability of REST API endpoints
- [X] Customize the oauth2 error response; currently it returns 2 fields; the whole API should have same error response - DONE
- [ ] Print the filter chain; all the filters which have been invoked before the request reaches the DispatcherServlet
- [X] Change the keycloak signing key and verify - DONE
- [ ] Time the API requests; log how much API request takes
- [ ] Log the API requests / responses everything including headers ... AbstractRequestLoggingFilter
- [X] Extract the principal from the id-token - DONE
- [ ] In keycloak change validity period and verify that its rejected if token has expired
- [X] Harmonize the API error responses from when e.g. path is not found; look in examples below - DONE
- [ ] In JavaScript its preferred to use underscore instead of camelCase; i.e. user_name and not userName; so, we should change the fields in the token
- [X] Override `config.authenticationEntryPoint()` and provide custom auth entry point - DONE
- [X] Return app information on `/actuator/info` - DONE
- [X] Keep configs in one property file and secrets in another property file! - THIS WON'T WORK! `spring-cloud-kubernetes`
- [ ] Load some configs from a shared lib e.g. `spring.mvc.hrow-exception-if-no-handler-found=true`
- [ ] Just expose `/actuator/info` & `/actuator/health`; but rest must not be exposed to whole world we need to expose `/actuator/prometheus`
- [ ] All configs should be provided through configmaps and secrets

## How to get keycloak token?

- `https://keycloak.cp.lab187.k8syard.com/auth/admin/lab/console/`
- Login with users of `lab` realm; frodo or 
- Open developer tools and then look for `Authorization` header in API calls to `realms` or `serverInfo` and extract select 
value after `Bearer`
- Or run the react-app
- Try to access the secure path
- Then in browser terminal print: `window.keycloak.idToken` or `keycloak.idToken`
- Copy the idToken and then add it as `Authorization` header with `Bearer ` + idToken

## Features

### Colored output on console of SpringBoot application

Colored output working within Intellij Idea for a Spring Boot application

```
spring:
  output:
    ansi:
      enabled: always
```

### Lombok

To avoid repetitive code!

### Same API Error format

- Same API error response for business and oauth errors

Fixed!

### Monitoring

- https://docs.spring.io/spring-metrics/docs/current/public/prometheus#web

`http://localhost:8090/actuator/prometheus`

#### How to enable?

As from Spring Boot 2.0, Micrometer is the default metrics export engine. Micrometer is an application metrics facade 
that supports numerous monitoring systems. 

When you add Spring Boot Actuator and micrometer as your dependencies, it auto-configures a composite MeterRegistry and 
adds a registry for each of the supported implementations that it finds on the classpath. Having a dependency on 
micrometer-registry-prometheus in your runtime classpath is enough for Spring Boot to configure the registry.

- In `pom.xml`

```
<!-- Spring boot actuator to expose metrics endpoint -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<!-- Micormeter core dependecy  -->
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-core</artifactId>
</dependency>
<!-- Micrometer Prometheus registry  -->
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

- In `application.yaml`

```
management:
  endpoint:
    metrics:
      enabled: true
    prometheus:
      enabled: true
  endpoints:
    web:
      exposure:
        include: "*"
  metrics:
    export:
      prometheus:
        enabled: true
```

That is all you need to do to enable the metrics. Start the application with these changes and if you browse URL 
`http://localhost:8090/actuator` you should see the actuator endpoints.

Notice spring-boot 2 and actuator has enabled an endpoint `http://localhost:8090/actuator/prometheus` for us. If you 
browse this URL, you will be able to see the metrics exported from the person-application. The data is the actual 
metrics collected from the application and exported as JSON.

```
http_server_requests_seconds_count{exception="None",method="GET",status="200",uri="/api/username-from-oauth2-authentication",} 2.0
http_server_requests_seconds_sum{exception="None",method="GET",status="200",uri="/api/username-from-oauth2-authentication",} 0.273836206
```

### Logging

Logback is the concrete implementation & SLF4J is the API!

Logback natively implements the SLF4J API. This means that if you are using logback, you are actually using the SLF4J API. 

#### Logbok

We use logbok to log HTTP requests and responses; and it can be turned off and on by using this yaml/properties file:

```
logging:
  level:
    org:
      zalando:
        logbook: TRACE
```

More settings of logbok can be controlled with:

https://github.com/zalando/logbook#example-configuration

```
logbook:
    include:
        - /api/**
        - /actuator/**
    exclude:
        - /actuator/health
        - /api/admin/**
    filter.enabled: true
    format.style: http
    obfuscate:
        headers:
            - Authorization
            - X-Secret
        parameters:
            - access_token
            - password
    write:
        category: http.wire-log
        level: INFO
        chunk-size: 1000
```

### Principal extraction from JWT IdToken; OAuth2 OpenID Connect

- 

### Build Info

`/actuator/info` returns the build info

- https://docs.spring.io/spring-boot/docs/current/maven-plugin/examples/build-info.html

e.g.

```
{
    "build": {
        "name": "movies-service",
        "time": "2018-10-07T20:26:09.713Z",
        "java": {
            "target": "1.8",
            "source": "1.8"
        },
        "encoding": {
            "source": "UTF-8",
            "reporting": "UTF-8"
        },
        "version": "0.0.1-SNAPSHOT",
        "group": "com.stakater.lab",
        "artifact": "movies-service"
    }
}
```

## Different API Error Responses

Goal: Same error format for all APIs including 401/unauthorized errors!

Need to harmonize the different API error response!

- Try to reach a path which doesn't exist

The API error response looks like:

```
{
    "timestamp": "2018-10-01T20:38:50.308+0000",
    "status": 404,
    "error": "Not Found",
    "message": "No message available",
    "path": "/api/username"
}
```

- Comment this line `config.resourceId(audience);` in `ResourceServerConfig` and the API error response will look like:

```
{
    "error": "access_denied",
    "error_description": "Invalid token does not contain resource id (oauth2-resource)"
}
```

We need custom format of OAuth2 exceptions

So the solution for me to achieve a different custom error structure for oauth2 errors on resource server resources was to register a custom OAuth2AuthenticationEntryPoint and OAuth2AccessDeniedHandler that I register using a ResourceServerConfigurerAdapter.

Solution: https://github.com/spring-projects/spring-security-oauth/issues/605

The OAuth 2.0 specification defines how the authorization server error responses must be, but it does not force resource servers to adopt the same format.

While adding OAuth 2.0 security to our services, we found it confusing for our clients to have to deal with two different error formats : one for "business errors" the other for "authorization errors".

## Extracting user/principal from IdToken

- `DefaultUserAuthenticationConverter` is called but it can't extract information as it is looking for a field with 
name `user_name`; need to add this field to KeyCloak claims - DONE
- `SecurityController` has examples to extract principal in 4 different ways
- There is no need to create a new class as `OAuth2Authentication` is good enough as it has all claims as well
- Good to put breakpoints in `DefaultAccessTokenConverter` and debug the status and see how principal is extracted from the token

## Acknowledgements

- https://github.com/neo4j-examples/movies-java-spring-data-neo4j
- https://github.com/spring-projects/spring-data-examples/tree/master/neo4j