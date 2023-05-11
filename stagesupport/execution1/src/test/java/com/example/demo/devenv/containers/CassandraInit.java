package com.example.demo.devenv.containers;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.utility.DockerImageName;

import com.example.demo.devenv.util.RessourceAccess;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CassandraInit extends GenericContainer<CassandraInit> {

    //see resource folder
    public static final String PATH = "testcontainers/cassandrainit/";

    private static GenericContainer<CassandraInit> instance = null;

    @SuppressWarnings("resource") //testcontainers will do an automatic cleanup
    public static GenericContainer<CassandraInit> getInstance(String repository) {

        if (instance == null) {
            instance = new CassandraInit(
                new ImageFromDockerfile()
                        .withFileFromString("Dockerfile", 
                            RessourceAccess.readClasspathAndReplace(PATH+"Dockerfile", repository))
                        .withFileFromClasspath("init", PATH+"init"))                                 
                .withCreateContainerCmdModifier(cmd -> cmd.withName("testcontainers_cassandrainit_"+System.currentTimeMillis()))
                .withNetworkMode("host")
                .withReuse(false)
                .withLogConsumer(new Slf4jLogConsumer(log));
        }

        return instance;
    }

    public CassandraInit(ImageFromDockerfile withFileFromClasspath) {
        super(withFileFromClasspath);
    }

    public CassandraInit(DockerImageName dockerImageName) {
        super(dockerImageName);
    }
    
}