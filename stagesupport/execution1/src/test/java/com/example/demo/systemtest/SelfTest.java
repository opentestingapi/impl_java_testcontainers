package com.example.demo.systemtest;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("systemtest")
public class SelfTest extends OpenTestingSystemTest {  
	
	SelfTest() {
		super("application.yml");
	}
    
}