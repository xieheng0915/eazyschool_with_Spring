### RestAPI provider & consumer

##### Create restapi provider
- (1) Create "rest" pakage and restcontroller java files, mark @ResponseBody to inform no UI data will be sent back
- (2) Config ProjectSecurityConfig.java, add path, disable csrf()
- (3) Use postman application to test (consumer)
- (4) If use @RestController annotation, could omit @ResponseBody on top of each method
- (5) For PostMapping, add Response model to take returned object
- (6) If you want to hide the elements from API, in model, add @JsonIgnore annotation on top of them.
- (7) To handle API exceptions, in "rest" package, add GlobalExceptionRestController.java file, use @RestControllerAdvice annotation, to avoid confilic with GlobalExceptionController, add @Order(1) 
- (8) To allow CORS access, add @CrossOrigin in RestController
- (9) To use XML format, add MediaType in @RequestMapping and add "jarkata-dataformat-xml" package in pom.xml. 
```
@Slf4j
@RestController
@RequestMapping(path = "/api/contact",produces = {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE})
@CrossOrigin(origins="*")
public class ContactRestController {
```

```
<dependency>
    <groupId>com.fasterxml.jackson.dataformat</groupId>
    <artifactId>jackson-dataformat-xml</artifactId>
</dependency>
```

##### Consume rest api
There are 3 ways to consume restapi, openFeign, which is provided by spring cloud, restTemplate, which is deprecated because it couldn't handle async call, and webClient.  
**OpenFeign:**
- (1) Add Spring cloud dependency packages
- (2) Create Proxy @FeignClient 
- (3) Config auth 
- (4) use proxy object in controller  
**RestTemplate**
- (1) Config auth 
- (2) Use exchange() method directly in controller   
**WebClient** 
- (1) Config auth
- (2) Use related method directly in controller

### Use Spring data rest and Hal Explorer to create Rest API automatically

- (1) Add data rest and hal explorer dependencies into pom.xml
```
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-data-rest</artifactId>
</dependency>
<dependency>
	<groupId>org.springframework.data</groupId>
	<artifactId>spring-data-rest-hal-explorer</artifactId>
</dependency>
```
- (2) Java rest will create profileController automatically, in case profile Controller name conflict happenes, add @Controller("profileControllerBean") to avoid confilict. 
- (3) By accessing "http://localhost:8080/profile" to get all the api list.
- (4) Add base.path in application.properties for rest-api, to let url redirect to hal api page automatically without url conflict with main applicaiton home url.
```
spring.data.rest.basePath=/data-api
```
- (5) Add authentication configuration in ProjectSecurityConfiguration.java, disable csrf and add authenticate() to make sure no anonymous user to access.
```ProjectSecurityConfiguration.java
 http.csrf().ignoringRequestMatchers("/saveMsg").ignoringRequestMatchers("/public/**")
                .ignoringRequestMatchers("/api/**").ignoringRequestMatchers("/data-api/**").and()
                // omit...
                .requestMatchers("/data-api/**").authenticated()
```
- (6) To filter class or elements from exposing to outside, use @RepositoryRestResource(exported=false). To filter element use @JsonIgnore
- (7) To control the path name, use @RepositoryRestResource(path="courses")

### Log features  
- (1) Log level: Fatal < error < warn < info < debug < trace, "<" means levels, if you choose trace, all types of log will be logged out.
- (2) You can set log level in application.properties file, and define into detail packages, or group multiple packages
```
#debug=true
#trace=true

logging.level.root=INFO
#logging.level.com.eazybytes.eazyschool.aspects = ERROR
#logging.level.com.eazybytes.eazyschool.controller = ERROR

# Initialize log group eazyschool_error
logging.group.eazyschool_error=com.eazybytes.eazyschool.aspects, com.eazybytes.eazyschool.controller
# Set log level to log group eazyschool_error
logging.level.eazyschool_error=ERROR

spring.output.ansi.enabled=ALWAYS
```
- (3) To define log color use spring.output.ansi.enabled (above file)
- (4) Use logback.xml can define console and logfile properties

### Configuration
- (1) Below are the mehods to set up properties, command line args have the top priority and property files the lowest.
```
properties present inside files like application.properties
OS Env variables
Java system properties
JNDI attributes from java:comp/env
ServletContext init parameters
ServletConfig init parameters
Command line arguments
```
- (2) For properties/configurations, use @value to load properties in related controller layer
- (3) Or use Environment with @Autowired to load mulitple properties in related controller layer
```
    @Value("${eazyschool.pageSize}")
    private int defaultPageSize;

    @Value("${eazyschool.contact.successMsg}")
    private String message;

    @Autowired
    Environment environment;
```
And: 
```
    log.error("defaultPageSize value with @Value annotation is : "+defaultPageSize);
    log.error("successMsg value with @Value annotation is : "+message);

    log.error("defaultPageSize value with Environment is : "+environment.getProperty("eazyschool.pageSize"));
    log.error("successMsg value with Environment is : "+environment.getProperty("eazyschool.contact.successMsg"));
    log.error("Java Home environment variable using Environment is : "+environment.getProperty("JAVA_HOME"));
```
- (4) Read properties with @ConfigurationProperties, add EazySchoolProps.java in config package
```
@Component("eazySchoolProps")
@Data
@ConfigurationProperties(prefix = "eazyschool")
@Validated
public class EazySchoolProps {

    @Min(value=5, message="must be between 5 and 25")
    @Max(value=25, message="must be between 5 and 25")
    private int pageSize;
    private Map<String, String> contact;
    private List<String> branches;

}
```
Then change in ContactService bean, and all the places using these properties.
- (5) Separately config properties in different environments and activate when needed. For example, application_prod.properties, application_uat.properties, in application.properties file use below configuration.  
```
spring.config.import=application_prod.properties,application_uat.properties
spring.profiles.active=uat
```
- (6) To activate other profiles by command and other ways: 
```command line:
java "-Dspring-boot.run.profile=prod" -jar myApp-0.0.1-SNAPSHOT.jar
mvn spring-boot:run "-Dspring-boot.run.profiles=prod"
```
- (7) To avoid input password in dev environment, you can create another configuration file in security package with @Profile("!prod"")

### actuator
- Actuator offers production-ready features such as monitoring and metrics to SpringBoot applications 
- (1) Add below dependency package in pom.xml
```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```
- (2) Default url: http://localhost:8080/actuator, to set your own url to avoid conflict with web home path,config as below in application.properties.
```
management.endpoints.web.base-path=/eazyschool/actuator
management.endpoints.web.exposure.include=*
```
- (3) To read acuator api on the browser, add **JsonVue** plugin in chrome browser
- (4) Add base path into project security configuration file 
```
.ignoringRequestMatchers("/eazyschool/actuator/**")
.requestMatchers("/eazyschool/actuator/**").hasRole("ADMIN")
```
- (5) For "http://localhost:8080/eazyschool/actuator/info", need developer to implement. Add EazySchoolInfoContributor class in audit package.
```
@Component
public class EazySchoolInfoContributor implements InfoContributor {

    @Override
    public void contribute(Info.Builder builder) {
        Map<String, String> eazyMap = new HashMap<String, String>();
        eazyMap.put("App Name", "EazySchool");
        eazyMap.put("App Description", "Eazy School Web Application for Students and Admin");
        eazyMap.put("App Version", "1.0.0");
        eazyMap.put("Contact Email", "info@eazyschool.com");
        eazyMap.put("Contact Mobile", "+1(21) 673 4587");
        builder.withDetail("eazyschool-info", eazyMap);
    }

}
```

##### Use third party tool to build up admin stage.
- (1) Tool site: https://github.com/codecentric/spring-boot-admin
- (2) Create a new project (example 51), pom.xml configuration, spring-boot-admin.version need match to org.springframework.boot version in parent.
```
<properties>
    <java.version>17</java.version>
    <spring-boot-admin.version>3.0.0-M5</spring-boot-admin.version>
</properties>
```
add dependencies: 
```
<dependency>
    <groupId>de.codecentric</groupId>
    <artifactId>spring-boot-admin-starter-server</artifactId>
</dependency>
```
And, below ${spring-boot-admin.version} refer to properties section
```
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>de.codecentric</groupId>
            <artifactId>spring-boot-admin-dependencies</artifactId>
            <version>${spring-boot-admin.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```
- (3) In admin project main application, import package and add annotation @EnableAdminServer
```
import de.codecentric.boot.admin.server.config.EnableAdminServer;
...
@EnableAdminServer
```
- (4) Add server.port in application.properties of admin project，please ensure to use **port:9090**
```
server.port=9090
```

- (5) In original application project(example 50), add below configuration in pom.xml
```
<properties>
    <java.version>17</java.version>
    <spring-boot-admin.version>3.0.0-M5</spring-boot-admin.version>
</properties>
```
And: 
```
<dependency>
    <groupId>org.hibernate</groupId>
    <artifactId>hibernate-micrometer</artifactId>
    <version>6.1.6.Final</version>
</dependency>
<dependency>
    <groupId>de.codecentric</groupId>
    <artifactId>spring-boot-admin-starter-client</artifactId>
    <version>${spring-boot-admin.version}</version>
</dependency>
```
- (6) Add properties in application.properties
```
spring.application.name=EazySchool

# SpringBoot Admin Server Configuration
spring.boot.admin.client.url=http://localhost:9000
spring.boot.admin.client.instance.metadata.user.name=admin@eazyschool.com
spring.boot.admin.client.instance.metadata.user.password=admin
```
Then restart application, do some actions, and check admin site.

- (7) To build clean jar: in Intellij, double click "control" button, select "mvn clean install" from drop down list, and select project name from projects drop down list. Pay attention to stop the previous project and delete all the file under "target" folder.

- (8) **If admin server can't find application server, could start client application first, then start admin server.**
- _*Btw,this third party platform just list up all the acuator api in its site, not very useful.*_

### Deploy application to AWS 
- (1) Instead of EC2, AWS beanstalk can take care of the server with embeded not only EC2 instance, but also loadbalancer, S3 buckes.
- (2) Buid a clean jar file 
- (3) Create instance in beanstalks, upload the jar file
- (4) For configuration file, you can use configuration in beanstalk management page to avoid reupload jar file, but for source code related editing, need to reupload the updated jar file.
- (5) Server port should be set to 5000, and make sure "/" home url permit all access to allow beanstalk to take over the application. If you redeploy and still keep error, select version "fix" instead of "percentage" to make sure the whole jar is updated.
- (6) To avoid DB user/pwd exposed to outside, remove the configuration in application.properties, and delete current inbound rule in RDS DB instance. Create customized inbound rule, bind access with application server security group, limit port to 3306 (DB port)
- (7) Add DB url/user/pwd in beanstalk configuration page (online), therefore, only dba or devops team can see the user/pwd.
- (8) To delete the instance, first, delete the inbound rules; second, delete RDS DB instance; third, delete beanstalk instance. 









