# impl_java_testcontainers

start impl_java as testcontainer to execute test cases with JUnit


Using this approach it is possible to execute existing test cases during maven build.

Here you can find the interesting stuff:

[Test Class](src/test/java/com/example/demo/DemoApplicationTests.java) (here you will find the code starting up the opentesting container)

[Test Case](src/test/resources/opentestingapi/helloworldtest.json) (localhost will be available as <i>host.testcontainers.internal</i>)


## our suggestion - make it reusable using this approach:

* copy this class to your test classes [OpenTestingApiExec](src/test/java/com/example/demo/reusable/OpenTestingApiExec.java)
* directly use it in your test class, for example [Test Class](src/test/java/com/example/demo/reusable/DemoApplicationReusableTests.java)
