package com.example.demo.devenv;

import com.example.demo.common.Sleeper;
import com.example.demo.devenv.containers.*;
import lombok.SneakyThrows;

/**
 * Env2 Environment - a virtual one
 */
public class Env2 extends DevEnv {    

    @SneakyThrows
    public static void init(boolean isSystemtest){       

        /** in addition to normal tests we will startup a "virtual environment" using testcontainers */
        
        //Cassandra
        create(Cassandra.getInstance(configure.getNetworkmode(), REPOSITORY_DOCKERHUB));
        create(CassandraInit.getInstance(REPOSITORY_DOCKERHUB));

        //add your apps here

        //Opentesting
        create(Opentesting.getInstance(REPOSITORY_GHCR)); //will use host network to get access to other containers 

        startContainers();

        //wait for cassandra initializer - yes Cassandra will take some time to start up correctly...
        Sleeper.sleep(120000); 
    }

    public static void main(String[] args) {    
        init(false);    
        run();
    }
    
}