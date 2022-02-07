package com.example.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@Testcontainers
@Slf4j
class DemoApplicationTests {

	private final int SLEEPTIMEMS = 5000;

	@Container
	public GenericContainer opentesting = new GenericContainer(DockerImageName.parse("robertdiers/opentesting:1.0"))
			.withExposedPorts(50000)
			.withAccessToHost(true)
			.withLogConsumer(new Slf4jLogConsumer(log));

	@Autowired
	private ObjectMapper objectMapper;

	HttpClient client = HttpClient.newBuilder().build();

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
	 * read file content from resources folder
	 * 
	 * @param filename name of the file
	 * @return file content
	 * @throws IOException
	 */
	private String getResourceContent(String filename) throws IOException {
		InputStream is = getClass().getClassLoader().getResourceAsStream(filename);
		return new String(is.readAllBytes(), StandardCharsets.UTF_8);
	}

	/**
	 * Json string to JsonNode
	 * 
	 * @param input json input
	 * @return JsonNode
	 * @throws JsonProcessingException
	 */
	public JsonNode json2JsonNode(String input) throws JsonProcessingException {
		return objectMapper.readTree(input);
	}

	/**
	 * map from Json string using JsonNode
	 * 
	 * @param input json input
	 * @return map with attributes
	 * @throws JsonProcessingException
	 */
	public Map<String, Object> json2Map(String input) throws JsonProcessingException {
		return objectMapper.convertValue(json2JsonNode(input), new TypeReference<Map<String, Object>>() {
		});
	}

	/**
	 * do a hello world test using the opentesting container
	 */
	@Test
	@SneakyThrows
	void helloworldtest() {

		// check if my endpoints are available (optional)
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("http://localhost:8080/actuator/health"))
				.build();
		HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
		log.info("##### my health: " + response.body());

		// check if opentesting container is available
		request = HttpRequest.newBuilder()
				.uri(URI.create(getOpentestingServer() + "/actuator/health"))
				.build();
		response = client.send(request, BodyHandlers.ofString());
		log.info("##### opentesting container: " + response.body());

		// validation
		assertEquals("{\"status\":\"UP\"}", response.body());

		// expose host port to the opentesting container
		org.testcontainers.Testcontainers.exposeHostPorts(8080);

		// upload test case files
		request = HttpRequest.newBuilder()
				.POST(HttpRequest.BodyPublishers
						.ofString(getResourceContent("opentestingapi/helloworldtest/check_rest_1_request.txt")))
				.uri(URI.create(getOpentestingServer() + "/upload/file/helloworldtest/check_rest_1_request.txt"))
				.header("Content-Type", "application/json")
				.build();
		response = client.send(request, BodyHandlers.ofString());
		log.info("##### opentesting upload check_rest_1.txt: " + response.body());
		request = HttpRequest.newBuilder()
				.POST(HttpRequest.BodyPublishers
						.ofString(getResourceContent("opentestingapi/helloworldtest/check_rest_1.txt")))
				.uri(URI.create(getOpentestingServer() + "/upload/file/helloworldtest/check_rest_1.txt"))
				.header("Content-Type", "application/json")
				.build();
		response = client.send(request, BodyHandlers.ofString());
		log.info("##### opentesting upload check_rest_1.txt: " + response.body());

		// upload test case
		request = HttpRequest.newBuilder()
				.POST(HttpRequest.BodyPublishers.ofString(getResourceContent("opentestingapi/helloworldtest.json")))
				.uri(URI.create(getOpentestingServer() + "/upload/test"))
				.header("Content-Type", "application/json")
				.build();
		response = client.send(request, BodyHandlers.ofString());
		log.info("##### opentesting upload helloworldtest.json: " + response.body());

		// start bulk execution
		request = HttpRequest.newBuilder()
				.POST(HttpRequest.BodyPublishers.ofString("[\"helloworldtest.inject-rest-1\"]"))
				.uri(URI.create(getOpentestingServer() + "/trigger/bulk"))
				.header("Content-Type", "application/json")
				.build();
		response = client.send(request, BodyHandlers.ofString());
		log.info("##### opentesting trigger bulk: " + response.body());

		// read bulkid from result
		Map<String, Object> opentestingdata = json2Map(response.body());
		String bulkid = (String) opentestingdata.get("bulkid");

		// wait for the test case result:
		// "percentage":0.0,"all":1,"success":0,"open":1,"failed":0,"mandatorySuccess":false,"allSuccess":false
		int openchecks = 1;
		String result = "";
		while (openchecks > 0) {
			Thread.sleep(SLEEPTIMEMS);
			result = getBulkResult(bulkid);
			opentestingdata = json2Map(result);
			openchecks = (Integer) opentestingdata.get("open");
		}
		log.info("##### opentesting bulk result: " + result);

		// validation
		boolean mandatorySuccess = (Boolean) opentestingdata.get("mandatorySuccess");
		boolean allSuccess = (Boolean) opentestingdata.get("allSuccess");
		assertTrue(mandatorySuccess);
		if (!allSuccess) {
			log.warn("##### WARNING - some non-mandatory checks failed: " + opentestingdata.get("failed"));
		}

	}

	/**
	 * request bulk result by bulkid
	 * 
	 * @return the result of the bulk
	 * @throws InterruptedException
	 * @throws IOException
	 */
	private String getBulkResult(String bulkid) throws IOException, InterruptedException {

		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(getOpentestingServer() + "/reporting/bulk?bulkid=" + bulkid))
				.build();
		HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
		log.debug("##### opentesting bulk result: " + response.body());

		return response.body();
	}

}