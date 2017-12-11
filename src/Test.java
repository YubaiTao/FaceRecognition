/**
 * project: FaceRecognition
 *
 * @author YubaiTao on 09/12/2017.
 */
public class Test {
    Tool tool;
    double[][] listR;

    public Test(String directory, double[][] U, double[][] Omega){
        tool = new Tool(directory);
        listR = tool.listR;
        double[] meanVector = Tool.getMean(listR);
        // substract mean face from input face
        double[][] listI = Tool.transpose(Tool.getA(listR, meanVector));
        // []: d0 ; dj(min[di])
        double[][] result = new double[listR[0].length][2];
        for (int i = 0; i < listR[0].length; i++) {
            result[i] = testSingle(listI[i], U, Omega);
        }
    }

    private double[] testSingle(double[] I, double[][] U, double[][] Omega) {
        double[] result = new double[2];// 0: d0 ; 1: dj(min among di)
        // OmegaI = U_trans * I_bar


        // IR = U * OmegaI
        // d0 = dist(IR - I_bar)
        // dj = min{ di = dist(OmegaI - Omega_i) }
        return result;
    }
}
