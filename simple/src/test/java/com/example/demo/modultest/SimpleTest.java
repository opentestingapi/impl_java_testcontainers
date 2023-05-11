package com.example.demo.modultest;

import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

@Slf4j
public class SimpleTest {

    @Test
    public void testConversation() throws JsonProcessingException {

        String value = "hallo";
        log.info(value);

        String expectation = "hallo";
        log.info(expectation);

        Assert.assertTrue("expectation not fullfilled", value.equals(expectation));
    }

}