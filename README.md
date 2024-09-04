# Freelancer Web Services (FWS)

Freelancer Web Services (FWS) is a fictional system that provides management services for freelancers. The system is based on the [Domain-Driven Design Example by Mirko Sertic](https://www.mirkosertic.de/blog/2013/04/domain-driven-design-example/), review the [project documentation](docs/README.md) to learn more about its domain, architecture and design.


## Getting Started

This project is a [Spring Boot](https://docs.spring.io/spring-boot/) application that uses [Gradle](https://docs.gradle.org/current/userguide/userguide.html) as a build tool. To run the project, you need to have Java 17 installed on your machine.

To open the project in Eclipse, run the following command:

    ./gradlew eclipse

Then, import the project into Eclipse as an existing Gradle project.

> The project uses several annotation processors to generate code, please keep in mind that Eclipse's annotation processing is not perfect and you may need to manually enable annotation processing for the project as described in this [thread](https://github.com/tbroyer/gradle-apt-plugin/issues/17).


## Building the Project

To build the project, run the following command:

    ./gradlew clean build


## Unit Tests

The unit tests are designed to support refactoring and are decoupled from implementation classes as much as possible. They test relevant business scenarios end to end and provide a high level of code coverage.

The unit tests are written using [JUnit 5](https://junit.org/junit5/) and the Gherking DSL framework.

The Gherkin DSL is a custom framework that encapsulates the system functionalities and its side effects in a fluent interface, which allows the developer to write more expressive tests. For example, the following test checks if the system is able to register freelancers:

```java
  @Test
  void registersFreelancers() {
    given(systemReady())
        .when(freelancer(tonyStark()).registered())
        .and(freelancer(steveRogers()).registered())
        .then(freelancers().collection())
        .contains(jsonPath("_embedded.freelancers[*].firstName").value(hasItems("Tony", "Steve")));
  }
```

The intent is to make the project tests more readable and maintainable while avoiding the fragility of traditional unit tests, which are commonly coupled to implementation classes and have to rely on mocks and stubs. Look at the [FreelancersApiTest](src/test/java/io/github/krloxz/fws/freelancer/FreelancersApiTest.java) and [TestScenario](src/test/java/io/github/krloxz/fws/test/gherkin/TestScenario.java) classes to see more examples of tests written using the Gherkin DSL.

Use the following command to run the unit tests:

    ./gradlew test


## Running the Application

The project is a Spring Boot application that uses an in-memory H2 database to store the data. To run the application, execute the following command:

    ./gradlew bootRun

The application will start on port 8080. You can access the root URL at [http://localhost:8080](http://localhost:8080) to get a list of available resources.


## Generating the Documentation

The documentation is written in Markdown and can be found in the `docs` directory.

The project uses [Structurizr](https://docs.structurizr.com/) and [PlantUML](https://plantuml.com/) to generate diagrams that document the system's architecture and design. Run the following command to generate the diagrams:

    ./gradlew diagrams

The generated diagrams can be found in the directories `docs/structurizr/.generated` and `docs/images`.
