package com.example.demo.systemtest;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("env1")
public class Env1Test extends OpenTestingSystemTest {   
	
	Env1Test() {
		super("application-env1.yml");
	}    	
    
}