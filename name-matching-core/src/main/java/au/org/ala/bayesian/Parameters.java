package au.org.ala.bayesian;

import java.io.*;

/**
 * Base class for generated parameters classes.
 *
 */
abstract public class Parameters {
    /**
     * Load a vector of parameters into the parameter set.
     * <p>
     * Subclasses implement this method to define the mapping between a vector of doubles and the
     * prior and conditional probabilities.
     * </p>
     *
     * @param vector The parameter vector
     *
     * @see #store()
     */
    abstract public void load(double[] vector);

    /**
     * Load an encoded set of doubles from an encoded set of bytes.
     *
     * @param vector The bytes to read
     *
     * @throws IOException if unable to read
     *
     * @see #storeAsBytes()
     */
    public void loadFromBytes(byte[] vector) throws IOException {
        ByteArrayInputStream is = new ByteArrayInputStream(vector);
        ObjectInputStream os = new ObjectInputStream(is);
        int length = os.readInt();
        double[] values = new double[length];
        for (int i = 0; i < length; i++)
            values[i] = os.readDouble();
        os.close();
        this.load(values);
    }

    /**
     * Map the internal structure of the parmater set onto a vector of doubles.
     * <p>
     * Subclasses implement this method to define the mapping between a vector of doubles and the
     * prior and conditional probabilities.
     * </p>
     *
     * @return A mapped vector of doubles
     *
     * @see #load(double[])
     */
    abstract public double[] store();

    /**
     * Produces a serialized encoding of the parameters.
     * <p>
     * The encoding is an integer with the length of the array
     * and an array of doubles containing the parameters.
     * </p>
     *
     * @return A byte array with the encoded result.
     *
     * @throws IOException if unable to write
     *
     * @see #loadFromBytes(byte[])
     */
    public byte[] storeAsBytes() throws IOException {
        double[] values = this.store();
        int length = values.length;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream(length * Double.BYTES + 32);
        ObjectOutputStream os = new ObjectOutputStream(bytes);
        os.writeInt(length);
        for (int i = 0; i < length; i++)
            os.writeDouble(values[i]);
        os.close();
        return bytes.toByteArray();
    }
 }
