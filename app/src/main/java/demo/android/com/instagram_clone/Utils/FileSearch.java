package demo.android.com.instagram_clone.Utils;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Admin on 05-06-2018.
 */

public class FileSearch {

    /**
     * search the
     * @param directory
     * @return all the folders under it
     */
    public static ArrayList<String> getDirectoriesPath(String directory) {
        ArrayList<String> subDirectoryPaths = new ArrayList<>();

        File file = new File(directory);
        File[] listFiles = file.listFiles();
        for(int i = 0; i < listFiles.length; i++) {
            if(listFiles[i].isDirectory()) {
                subDirectoryPaths.add(listFiles[i].getAbsolutePath());
            }
        }
        return subDirectoryPaths;
    }


    /**
     * search the
     * @param directory
     * @return all the files under it
     */
    public static ArrayList<String> getFilesPath(String directory) {
        ArrayList<String> subFilesPaths = new ArrayList<>();

        File file = new File(directory);
        File[] listFiles = file.listFiles();
        for(int i = 0; i < listFiles.length; i++) {
            if(listFiles[i].isFile()) {
                subFilesPaths.add(listFiles[i].getAbsolutePath());
            }
        }
        return subFilesPaths;
    }
}
