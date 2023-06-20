package org.example.Util;

import java.io.File;

public class CreateFolders {


    public static synchronized boolean createFolders(String filePath){
        String[] paths = filePath.split("/");
        for (int i = 1; i < paths.length; i++) {
            paths[i] = paths[i-1]+"/"+paths[i];
        }
        for (String s: paths) {
            if(!new File(s).exists()){
              boolean b = new File(s).mkdir();
               if(!b) return false;
            }

        }
        return true;
    }


}
