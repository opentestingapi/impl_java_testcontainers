# OTA test execution

execute OTA test cases in pipeline using maven commands

* support maven profiles
* support preparation test cases
* parallel or sequential execution of test cases using annotation (https://github.com/opentestingapi/impl_java_testcontainers/blob/main/stagesupport/execution1/src/test/java/com/example/demo/systemtest/OpenTestingSystemTest.java#L122)
* define repository in properties
* define OTA instance (within testcontainers or external)
* support content replacement in test cases (templating, for example URLs)
* a "virtual environment" instead of testing external ressources (Env2)
