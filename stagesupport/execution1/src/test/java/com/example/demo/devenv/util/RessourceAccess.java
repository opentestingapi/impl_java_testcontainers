package com.example.demo.devenv.util;

import java.io.InputStream;
import java.util.Scanner;

public class RessourceAccess {

    public static String readClasspathAndReplace(String path, String repository) {
        InputStream in = RessourceAccess.class.getClassLoader().getResourceAsStream(path);
        try (Scanner s = new Scanner(in).useDelimiter("\\A")) {
            String content = s.hasNext() ? s.next() : "";
            content = content.replace("${REPOSITORY}", repository);
            return content;
        }
    }
    
}
