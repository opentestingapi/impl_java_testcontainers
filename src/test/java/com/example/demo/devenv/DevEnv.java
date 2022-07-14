package com.example.demo.devenv;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.assertj.core.util.Arrays;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Container.ExecResult;

import com.example.demo.common.Sleeper;
import com.example.demo.devenv.containers.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * Example Development Environment
 */
@Slf4j
@SuppressWarnings({"rawtypes"})
public class DevEnv {

    //memory values
    public static final long MB256 = 256*1024*1024;
    public static final long MB512 = 512*1024*1024;
    public static final long MB1024 = 1024*1024*1024;
    public static final long MB2048 = 2048*1024*1024;
    public static final long MB3072 = 3072*1024*1024;
    public static final long MB4096 = 4096*1024*1024;
    public static final long MB6144 = 6144*1024*1024;
    public static final long MB8192 = 8192*1024*1024;

    //store running containers
    public static List<GenericContainer> containers = new ArrayList<>();   

    @SneakyThrows
    public static void init(boolean isSystemtest){

        ConfigureDevEnv configure = new ConfigureDevEnv();        

        //set socket
        updateEnv("DOCKER_HOST", configure.getDockersocket());       

        //here we will add all required containers
        log.info("creating containers...");        

        //Opentesting
        if (!isSystemtest) create(Opentesting.getInstance()); //will use host network to get access to other containers        

        //you might have more containers here that will be started for local dev env and systemtests (like databases)

        //start parallel
        log.info("starting containers...");
        containers.parallelStream().forEach(cont -> start(cont));

        //keep it running
        log.info("###################################################");
        log.info("Startup Testenvironment Done");
        log.info("###################################################");        
    }

    @SneakyThrows
    public static void main(String[] args) {        
        init(false);
        log.info("stop after usage using Ctrl+C");
        log.info("###################################################");

        while (true) {

            //container, do not call getRunning as it will throw broken pipe errors
            StringBuilder sb = new StringBuilder();    
            containers.stream().forEach(cont -> sb.append(cont.getClass().getSimpleName()+", "));             
            log.info(sb.toString());            

            Sleeper.sleep(60000);            
        }
    }

    /**
     * stop containers
     */
    public static void stop() {
        containers.stream().forEach(cont -> cont.stop()); 
    }

    private static void create(GenericContainer cont) {
        containers.add(cont);
        log.info(cont.getClass().getSimpleName()+" created");
    }

    private static void start(GenericContainer cont) {
        cont.start();
        log.info(cont.getClass().getSimpleName()+" started");
    }

    public static GenericContainer get(Class clazz) {
        for (GenericContainer cont : containers) {
            if (cont.getClass().getSimpleName().equals(clazz.getSimpleName())) return cont;
        }
        throw new RuntimeException(clazz+" not found");
    }
    
    @SneakyThrows
    private static void execCommand(GenericContainer cont, String... command) {   
        log.info(cont.getClass().getSimpleName()+" executing: "+Arrays.asList(command));
        ExecResult res = cont.execInContainer(command);
        log.info(cont.getClass().getSimpleName()+" exec done (Exit "+res.getExitCode()+"):");
        log.info("Stdout: "+res.getStdout());
        log.info("Stderr: "+res.getStderr());        
    }

    @SuppressWarnings({ "unchecked" })
    public static void updateEnv(String name, String val) throws ReflectiveOperationException {
        Map<String, String> env = System.getenv();
        Field field = env.getClass().getDeclaredField("m");
        field.setAccessible(true);
        ((Map<String, String>) field.get(env)).put(name, val);
    }
    
}
