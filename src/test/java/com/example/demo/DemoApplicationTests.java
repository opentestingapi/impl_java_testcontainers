package com.example.demo;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Testcontainers
@Slf4j
class DemoApplicationTests {

	@Container
	public GenericContainer opentesting = new GenericContainer(DockerImageName.parse("robertdiers/opentesting:latest"))
			.withExposedPorts(50000);

	@Test
	@SneakyThrows
	void contextLoads() {
		// check if opentesting container is available
		String address = opentesting.getHost();
		Integer port = opentesting.getFirstMappedPort();
		HttpClient client = HttpClient.newBuilder().build();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("http://" + address + ":" + port + "/actuator/health")).build();
		HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
		log.info("##### opentesting container: " + response.body());
		assertEquals("{\"status\":\"UP\"}", response.body());
	}

}
