package com.example.demo.systemtest.util;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.utility.DockerImageName;

/**
 * wrapper class to allow fixed ports configuration
 */
public class GenericContainerFixedPort  extends GenericContainer {

    public GenericContainerFixedPort(ImageFromDockerfile withFileFromClasspath) {
        super(withFileFromClasspath);
    }

    public GenericContainerFixedPort(DockerImageName dockerImageName) {
        super(dockerImageName);
    }

    public GenericContainerFixedPort configurePort(int hostPort, int containerPort) {
        super.addFixedExposedPort(hostPort, containerPort);
        return this;
    }

}