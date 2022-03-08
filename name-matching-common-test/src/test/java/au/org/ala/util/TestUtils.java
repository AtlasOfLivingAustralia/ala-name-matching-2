package au.org.ala.util;

import org.junit.Assert;
import org.junit.ComparisonFailure;

import java.io.*;

public class TestUtils {
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
     * Compare two strings, ignoring whitespace during the comparison
     *
     * @param expected The expected string
     * @param actual The actual string
     */
    public static void compareNoSpaces(String expected, String actual) {
        String expected1 = expected.replace('\n', ' ');
        expected1 = expected1.replaceAll("\\s+", " ").trim();
        String actual1 = actual.replace('\n', ' ');
        actual1 = actual.replaceAll("\\s+", " ").trim();
        try {
            Assert.assertEquals(expected1, actual1);
        } catch (ComparisonFailure fail) {
            throw new ComparisonFailure(fail.getMessage(), expected, actual);
        }
     }
}
