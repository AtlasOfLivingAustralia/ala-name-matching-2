package au.org.ala.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.Assert;

import java.io.*;

public class TestUtils {
    /**
     * Create a suitably configured object mapper.
     *
     * @return The object mapper
     */
    public static ObjectMapper createMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        return mapper;
    }

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
        File dir = File.createTempFile(prefix, "test");
        if (!dir.delete())
            throw new IOException("Unable to delete " + dir);
        if (!dir.mkdirs())
            throw new IOException("Unable to create " + dir);
        return dir;
    }



    /**
     * Read a resource file.
     *
     * @param clazz The source class
     * @param resource The name of the resource
     * @return A reader for the resource
     *
     * @throws IOException
     */
    public static Reader getResourceReader(Class<?> clazz, String resource) throws IOException {
        InputStream is = clazz.getResourceAsStream(resource);
        if (is == null)
            throw new IllegalArgumentException("Can't find resource " + resource + " in " + clazz);
        return new InputStreamReader(is, "UTF-8");
    }

    /**
     * Read a resource file.
     *
     * @param clazz The source class
     * @param resource The name of the resource
     * @return The resulting resource
     *
     * @throws IOException
     */
    public static String getResource(Class<?> clazz, String resource) throws IOException {
        Reader reader = getResourceReader(clazz, resource);
        StringWriter writer = new StringWriter(1024);
        int n;
        char[] buffer = new char[1024];

        while ((n = reader.read(buffer)) > 0) {
            writer.write(buffer, 0, n);
        }
        return writer.toString();
    }

    /**
     * Read a resource file as bytes.
     *
     * @param clazz The source class
     * @param resource The name of the resource
     *
     * @return The resulting resource as an array of bytes
     *
     * @throws IOException
     */
    public static byte[] getResourceBytes(Class<?> clazz, String resource) throws IOException {
        InputStream is = clazz.getResourceAsStream(resource);
        if (is == null)
            throw new IllegalArgumentException("Can't find resource " + resource + " in " + clazz);
        Reader reader = getResourceReader(clazz, resource);
        int n;
        byte[] buffer = new byte[1024];
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        while ((n = is.read(buffer)) > 0) {
            os.write(buffer, 0, n);
        }
        is.close();
        os.close();
        return os.toByteArray();
    }

    /**
     * Compare two strings, ignoreing whitespace
     *
     * @param expected The expected string
     * @param actual The actual string
     */
    public static void compareNoSpaces(String expected, String actual) {
        expected = expected.replace('\n', ' ');
        expected = expected.replaceAll("\\s+", " ").trim();
        actual = actual.replace('\n', ' ');
        actual = actual.replaceAll("\\s+", " ").trim();
        Assert.assertEquals(expected, actual);
    }
}
