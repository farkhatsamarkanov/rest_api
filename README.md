REST API service of university registrar.

Used technologies:
- Spring framework for DI
- Hibernate for persistence
- PostgreSQL as database
- Flyway for database version control
- HikariCP for connection pooling
- Swagger for documentation generation
- JUnit, Hamcrest, Mockito, Spring Mock MVC for testing
- Lombok for boilerplate code generation

Deployment instructions:

- Clone repository
- Install PostgreSQL server, assign it to port number 5432, if not available, change port number in hikaricp.properties file
- Create empty database with name and password of your choice (assign your db name and password to according properties  in hikaricp.properties file)
- Install web server (I used Apache Tomcat) http://tomcat.apache.org/tomcat-8.5-doc/setup.html
- Run the app in IDE, if not then perform "mvn compile" and "mvn package" commands in shell
- Documentation can be obtained by the foolowing URL: localhost:YourPortNumber/YourDataBaseName/v2/docs
