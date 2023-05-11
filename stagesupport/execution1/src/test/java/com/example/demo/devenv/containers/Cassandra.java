package com.example.demo.devenv.containers;

import java.time.Duration;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.utility.DockerImageName;

import com.example.demo.devenv.util.RessourceAccess;

public class Cassandra extends GenericContainer<Cassandra> {

    //see resource folder
    public static final String PATH = "testcontainers/cassandra/";

    private static GenericContainer<Cassandra> instance = null;

    @SuppressWarnings("resource") //testcontainers will do an automatic cleanup
    public static GenericContainer<Cassandra> getInstance(String networkmode, String repository) {

        if (instance == null) {
            instance = new Cassandra(                
                new ImageFromDockerfile()
                        .withFileFromString("Dockerfile", 
                            RessourceAccess.readClasspathAndReplace(PATH+"Dockerfile", repository)))                              
                .withCreateContainerCmdModifier(cmd -> cmd.withName("testcontainers_cassandra_"+System.currentTimeMillis()))           
                //.withCreateContainerCmdModifier(cmd -> cmd.getHostConfig().withMemory(memory)) //fails...       
                .withStartupTimeout(Duration.ofMinutes(5))
                .withNetworkMode(networkmode)
                .withReuse(false);

            //add fixed port
            ((Cassandra) instance).configurePorts(9042, 9042);
        }

        return instance;
    }

    public Cassandra(ImageFromDockerfile withFileFromClasspath) {
        super(withFileFromClasspath);
    }

    public Cassandra(DockerImageName dockerImageName) {
        super(dockerImageName);
    }

    public void configurePorts(int hostPort, int containerPort) {
        super.addFixedExposedPort(hostPort, containerPort);        
    }
    
}
