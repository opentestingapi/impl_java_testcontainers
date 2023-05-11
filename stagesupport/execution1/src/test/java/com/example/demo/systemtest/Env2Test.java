package com.example.demo.systemtest;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.example.demo.devenv.Env2;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("env2")
public class Env2Test extends OpenTestingSystemTest {   
	
	Env2Test() {
		super("application-env2.yml");
	}

	/**
	 * we need to startup the virtual environment using testcontainers
	 */
	@BeforeAll
	static void beforeAll() {

		// Start virtual environment
		Env2.init(true);   
	}    
    
}