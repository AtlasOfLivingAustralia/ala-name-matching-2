package au.org.ala.util;

import java.io.*;

public class FileUtils {
    /**
     * Recursively delete all files.
     *
     * @param file The start file or directory
     *
     * @throws IOException If unable to delete the file
     */
    public static void deleteAll(File file) throws IOException {
        if (!file.exists())
            return;
        if (file.isDirectory()) {
            for (File sub: file.listFiles())
                deleteAll(sub);
        }
        if (!file.delete())
            throw new IOException("Unable to delete " + file);
    }

    /**
     * Create a temporary directory.
     *
     * @param prefix The prefix for the directory
     *
     * @return A created temporary directory
     *
     * @throws IOException if there is a problem creating the directory
     */
    public static File makeTmpDir(String prefix) throws IOException {
        File dir = File.createTempFile(prefix, "");
        if (!dir.delete())
            throw new IOException("Unable to delete " + dir);
        if (!dir.mkdirs())
            throw new IOException("Unable to create " + dir);
        return dir;
    }
}
