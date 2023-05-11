package com.example.demo.devenv;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.assertj.core.util.Arrays;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Container.ExecResult;

import com.example.demo.common.Sleeper;
import com.example.demo.devenv.containers.*;
import com.example.demo.devenv.util.ConfigureDevEnv;
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

    //store running containers
    public static List<GenericContainer> containers = new ArrayList<>();   

    public static String REPOSITORY_DOCKERHUB = "";
    public static String REPOSITORY_GHCR = "";
    public static ConfigureDevEnv configure;

	static {
		try {
			Properties prop = new Properties();
			prop.load(DevEnv.class.getClassLoader().getResourceAsStream("testcontainers.properties"));
			REPOSITORY_DOCKERHUB = prop.getProperty("repository.dockerhub");
            REPOSITORY_GHCR = prop.getProperty("repository.ghcr");
            configure = new ConfigureDevEnv();
		} catch (Exception e) {
			log.error("read testcontainers.properties failed", e);
		}
    }

    @SneakyThrows
    public static void init(boolean isSystemtest){        

        //Opentesting
        create(Opentesting.getInstance(REPOSITORY_GHCR)); //will use host network to get access to other containers        

        startContainers();
    }

    @SneakyThrows
    public static void startContainers(){

        //set socket if detected
        if (configure.isDetected()) updateEnv("DOCKER_HOST", configure.getDockersocket());

        //expose host port to the opentesting container
        org.testcontainers.Testcontainers.exposeHostPorts(8080);

        /**
         * ATTENTION
         * if startup takes to much time it might be because of image pull issues
         * please try to build images upfront with "docker build ." 
         */

        //start parallel
        log.info("starting containers...");
        containers.parallelStream().forEach(cont -> start(cont));

        //keep it running
        log.info("###################################################");
        log.info("Startup Testenvironment Done");
        log.info("###################################################"); 
    }

    public static void main(String[] args) {  
        init(false);      
        run();
    }

    /**
     * run DevEnv for local development
     */
    public static void run() {        
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

    protected static void create(GenericContainer cont) {
        containers.add(cont);
        log.info(cont.getClass().getSimpleName()+" created");
    }

    protected static void start(GenericContainer cont) {
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
