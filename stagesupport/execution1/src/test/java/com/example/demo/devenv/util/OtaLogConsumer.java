package com.example.demo.devenv.util;

import java.io.FileWriter;

import org.testcontainers.containers.output.BaseConsumer;
import org.testcontainers.containers.output.OutputFrame;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * we do not use logger framework, as we want to directly print the log in the same format
 */
@Slf4j
public class OtaLogConsumer extends BaseConsumer<OtaLogConsumer> {

    private FileWriter fileWriter;

    public OtaLogConsumer() {
        try {
            fileWriter = new FileWriter("target/opentestingapi.log");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    @SneakyThrows
    public void accept(OutputFrame outputFrame) {

        OutputFrame.OutputType outputType = outputFrame.getType();
        String utf8String = outputFrame.getUtf8String();

        switch (outputType) {
            case END:
                break;
            case STDOUT:
                System.out.print(utf8String); //NOSONAR
                fileWriter.write(utf8String);
                break;
            case STDERR:
                System.err.print(utf8String); //NOSONAR
                fileWriter.write(utf8String);
                break;
            default:
                throw new IllegalArgumentException("Unexpected outputType " + outputType);
        } 
        
        fileWriter.flush();
    }
    
}
