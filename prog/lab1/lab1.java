public class lab1 {
    public static void main(String[] args) {
        short[] n = new short[17];
        for (short i = 19; i >= 3; i--)
            n[19 - i] = i;
        

        float[] x = new float[20];
        float min = -4.0f;
        float max = 13.0f;
        for (int i = 0; i < 20; i++)
            x[i] = min + (float) Math.random() * (max - min);
        

        double[][] w = new double[17][20];

        for (int i = 0; i < 17; i++) {
            for (int j = 0; j < 20; j++) {
                switch (n[i]) {
                    case (short) 6:
                        w[i][j] = Math.sin(Math.tan(Math.exp(x[j])));
                        break;
                    case 4:
                    case 5:
                    case 9:
                    case 10:
                    case 11:
                    case 14:
                    case 16:
                    case 19:
                        w[i][j] = Math.atan(Math.cos(
                                Math.pow(Math.pow(2 / x[j], 2), (Math.pow((x[j] / (x[j] + 1.0 / 4.0)), 4) / 1.0) / 3.0)
                        ));
                        break;
                    default:
                        w[i][j] = Math.pow(2 * (Math.sin(Math.cbrt(Math.sin(x[j]))) - Math.PI), Math.sin(Math.tan(Math.atan((x[j] + 4.5) / 17.0))));
                        break;
                }
            }
        }
        Output.out(w);
    }
}

class Output {
    static void out(double[][] w) {
        for (double[] a : w) {
            for (double b : a) 
                System.out.printf("%.5f ", b);
            System.out.println();
        }
    }
}
