package org.example.Util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;

public class GetPaths {
    public static Object[] getPaths(String path) throws IOException {
       // System.out.println("In Paths");
        Object[] arr =  Files.walk(Paths.get(path)).filter(a->Files.isRegularFile(a)).map(e->e.getParent()+"/"+e.getFileName()).collect(Collectors.toList()).toArray();
    //    System.out.println(Arrays.toString(arr));
        return arr;
    }

}
