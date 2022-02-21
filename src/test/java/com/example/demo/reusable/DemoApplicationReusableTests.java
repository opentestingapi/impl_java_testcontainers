package com.example.demo.reusable;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Arrays;

import static org.junit.Assert.assertTrue;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@Testcontainers
@Slf4j
class DemoApplicationReusableTests {

	//this time we will reuse it
	public static GenericContainer opentesting = new GenericContainer(DockerImageName.parse("robertdiers/opentesting:1.0"))
			.withExposedPorts(50000)
			.withAccessToHost(true)
			.withReuse(true)
			.withLogConsumer(new Slf4jLogConsumer(log));

	@BeforeAll
	public static void beforeAll() {
		opentesting.start();

		// expose host port to the opentesting container
		org.testcontainers.Testcontainers.exposeHostPorts(8080);
	}

	/**
	 * get server name and port as http://<server>:<port>
	 * 
	 * @return http://<server>:<port>
	 */
	private String getOpentestingServer() {
		String address = opentesting.getHost();
		Integer port = opentesting.getFirstMappedPort();
		return "http://" + address + ":" + port;
	}

	/**
	 * do a hello world test using the opentesting container
	 */
	@Test
	@SneakyThrows
	void helloworldtest1() {

		// execute OpenTestingAPI case (folder string example)
		boolean result1 = new OpenTestingApiExec(getOpentestingServer()).test(
				"opentestingapi/helloworldtest.json",
				"opentestingapi/helloworldtest",
				Arrays.asList("helloworldtest.inject-rest-1")
		);
		assertTrue("result1 should be true", result1);

	}

	/**
	 * do a hello world test using the opentesting container
	 */
	@Test
	@SneakyThrows
	void helloworldtest2() {

		// execute OpenTestingAPI case (files string example)
		boolean result2 = new OpenTestingApiExec(getOpentestingServer()).test(
				"opentestingapi/helloworldtest.json",
				Arrays.asList("opentestingapi/helloworldtest/check_rest_1.txt", "opentestingapi/helloworldtest/check_rest_1_request.txt"),
				Arrays.asList("helloworldtest.inject-rest-1")
		);
		assertTrue("result2 should be true", result2);

	}

	/**
	 * do a hello world test using the opentesting container
	 */
	@Test
	@SneakyThrows
	void helloworldtest3() {

		// execute OpenTestingAPI case (folder URL example)
		boolean result3 = new OpenTestingApiExec(getOpentestingServer()).test(
				getClass().getClassLoader().getResource("opentestingapi/helloworldtest.json"),
				getClass().getClassLoader().getResource("opentestingapi/helloworldtest"),
				Arrays.asList("helloworldtest.inject-rest-1")
		);
		assertTrue("result3 should be true", result3);

	}

	/**
	 * do a hello world test using the opentesting container
	 */
	@Test
	@SneakyThrows
	void helloworldtest4() {

		// execute OpenTestingAPI case (files URL example)
		boolean result4 = new OpenTestingApiExec(getOpentestingServer()).test(
				getClass().getClassLoader().getResource("opentestingapi/helloworldtest.json"),
				Arrays.asList(getClass().getClassLoader().getResource("opentestingapi/helloworldtest/check_rest_1.txt"),
						getClass().getClassLoader().getResource("opentestingapi/helloworldtest/check_rest_1_request.txt")),
				Arrays.asList("helloworldtest.inject-rest-1")
		);
		assertTrue("result4 should be true", result4);

	}

}