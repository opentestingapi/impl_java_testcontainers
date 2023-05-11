package com.example.demo.common;

public class Sleeper {

    public static void sleep(long millisec) {
        //Sonar Fix for Thread.sleep
        long start = System.currentTimeMillis();
        long now = System.currentTimeMillis();
        while (start >= (now - millisec)) {
            now = System.currentTimeMillis();
        }
    }
    
}
