package com.example.demo.reusable;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.FileNameUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.Assert.assertTrue;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@Testcontainers
@Slf4j
class DemoApplicationReusableTests {
	private static String defaultInjectid = "inject-rest-1";
	//this time we will reuse it
	public static GenericContainer opentesting = new GenericContainer(DockerImageName.parse("robertdiers/opentesting:latest"))
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

	/**
	 * Testing if .json are in the subfolders
	 */
	private boolean isJsonInSub() {
		boolean jsonInSubFolder = true;
		File folder = new File(getClass().getClassLoader().getResource("opentestingapi").getFile());
		for (File f : folder.listFiles()) {
			if (FileNameUtils.getExtension(f.getName()).equals("json")) {
				jsonInSubFolder = false;
				break;
			}
		}
		return jsonInSubFolder;
	}
	/**
	 * @return Stream of all subfolder names
	 */
	public static Stream<String> getProvidedTestCases() {
		File folder = new File(DemoApplicationReusableTests.class.getClassLoader().getResource("opentestingapi").getFile());
		String[] directories = folder.list(new FilenameFilter() {
			@Override
			public boolean accept(File current, String name) {
				return new File(current, name).isDirectory();
			}
		});
		return Stream.of(directories);
	}
	/**
	 * executes a test for all testcases in "opentestingapi"
	 */
	@SneakyThrows
	@ParameterizedTest
	@MethodSource("getProvidedTestCases")
	void testAllData(String data) {
		String folder = "opentestingapi/" + data;
		StringBuilder jsonData = new StringBuilder("opentestingapi/" + data);
		if (isJsonInSub()) {
			jsonData.append("/" + data);
		}
		jsonData.append(".json");
		boolean result1 = new OpenTestingApiExec(getOpentestingServer()).test(
				jsonData.toString(),
				folder,
				Arrays.asList(data + "." + defaultInjectid)
		);
		assertTrue("result should be true", result1);
	}

	/**
	 * executes a test for a specified list of files
	 */
	@SneakyThrows
	@ParameterizedTest
	@ValueSource(strings = {"helloworldtest","hello"})
	void testAFewData(String data) {
		String folder = "opentestingapi/" + data;
		StringBuilder jsonData = new StringBuilder("opentestingapi/" + data);
		if (isJsonInSub()) {
			jsonData.append("/" + data);
		}
		jsonData.append(".json");
		boolean result1 = new OpenTestingApiExec(getOpentestingServer()).test(
				jsonData.toString(),
				folder,
				Arrays.asList(data + "." + defaultInjectid)
		);
		assertTrue("result should be true", result1);
	}


}