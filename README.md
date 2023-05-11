# impl_java_testcontainers

start impl_java as testcontainer to execute test cases with JUnit


Using this approach it is possible to execute existing test cases during maven build.

Here you can find the interesting stuff with auto-detecting of created test cases:

 ### simple 

 [Test Class](simple/src/test/java/com/example/demo/systemtest/OpenTestingSystemTest.java)

`mvn clean install`

### different environments / templating

[ReadMe](stagesupport/)

[Test Class](stagesupport/execution1/src/test/java/com/example/demo/systemtest/OpenTestingSystemTest.java)

`mvn test -Psystemtest`

`mvn test -Penv1`

`mvn test -Penv2`

(Testcontainers sometimes fails if containers are not available locally and pull takes to long - you should pull them manually before...)


## Example Testing

![arch](arch.drawio.svg "Arch")

You can also use it to test a set applications, the same approcach could be used with reduced phases.
