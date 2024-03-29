package com.example.demo.devenv;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Slf4j
public class ConfigureDevEnv {

    private String networkMode;
    private String dockerSocket;
    private boolean detected = false;

    public ConfigureDevEnv() {
        init();
    }

    private String processCmd(String cmd){
        try {
            Runtime run = Runtime.getRuntime();
            Process process = run.exec(cmd);
            process.waitFor();
            return getStringInputFromProcess(process);

        } catch (IOException | InterruptedException e) {
            log.debug(e.getMessage());
        }
        return null;
    }

    @NotNull
    @SneakyThrows
    private String getStringInputFromProcess(Process process) {
        try (BufferedReader buf = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            StringBuilder output = new StringBuilder();
            while ((line = buf.readLine()) != null) {
                output.append(line + "\n");
            }
            return output.toString();
        }
    }

    private Boolean hasInstalled(String cmd, String responseContains){
        String output = processCmd(cmd);
        if (output != null &&
                output.toUpperCase().contains(responseContains))
            return true;

        return false;
    }

    private void init(){
        String os = System.getProperty("os.name");
        log.info(os);
        Boolean docker = hasInstalled("docker ps", "CONTAINER ID");
        log.info("Docker installed: " + docker);
        Boolean podman = hasInstalled("podman ps", "CONTAINER ID");
        log.info("Podman installed: " + podman);

        if(docker){
            networkMode = "bridge";
            detected = true;
        } else if (podman){
            networkMode = "slirp4netns"; //VPN support
            detected = true;
        } else {
            networkMode = "bridge"; //might be Docker in Pipeline
            dockerSocket = "";
            log.warn("Supported Container Environment Missing (Docker/Podman) - trying default");
            return;
        }

        if(os.toLowerCase().contains("win")){
            dockerSocket = "tcp://localhost:2375";
        } else if (os.toLowerCase().contains("linux")){
            if(docker) {
                dockerSocket = "unix:///var/run/docker.sock";
            } else if (podman) {
                String uid = processCmd("id -u");
                uid = uid.replaceAll("[^\\d.]", "");
                log.info("UID: " + uid);                
                dockerSocket = "unix:///run/user/"+uid+"/podman/podman.sock";
            }
        }
    }

    public String getNetworkmode(){
        if(networkMode == null)
            init();
        return networkMode;
    }

    public String getDockersocket(){
        if(dockerSocket == null)
            init();
        return dockerSocket;
    }

    public boolean isDetected(){
        return detected;
    }

}
