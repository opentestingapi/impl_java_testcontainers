package com.example.demo.modultest;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, properties = { "server.port=40001" })
public class WebEnvTest {

    @Autowired
    private Environment env;

    @Test
    public void testProperties() {

        //need to be available
        Assert.assertTrue("Environment does not exist", env != null);

        log.info("JAVA_HOME: "+env.getProperty("JAVA_HOME"));
        log.info("server.port: "+env.getProperty("server.port"));
    }

}