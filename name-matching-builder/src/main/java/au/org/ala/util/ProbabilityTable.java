package au.org.ala.util;

public class ProbabilityTable {
    public static void main(String[] args) {
        double dmin = 1.0;
        float fmin = 1.0f;
        for (int i = 0; i < 32; i++) {
            System.out.print(i);
            System.out.print('\t');
            System.out.print(dmin);
            System.out.print('\t');
            System.out.print(1.0 - dmin);
            System.out.print('\t');
            System.out.print(fmin);
            System.out.print('\t');
            System.out.print(1.0 - fmin);
            System.out.println();
            dmin = dmin / 10.0;
            fmin = fmin / 10.0f;
        }
    }
}
