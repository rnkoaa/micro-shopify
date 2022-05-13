package org.richard;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileReader {

    public static String readFile(String path) {
        try {
            return Files.readString(Paths.get(path));
        } catch (IOException e) {
            System.out.println("error while reading file " + path);
            return "";
        }
    }

    public static InputStream readResourceFile(String path) {
        return FileReader.class.getClassLoader().getResourceAsStream(path);
    }
}
