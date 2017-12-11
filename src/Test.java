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
        double[][] result = new double[listR[0].length][3];
        for (int i = 0; i < listR[0].length; i++) {
            result[i] = testSingle(listI[i], U, Omega);
        }
        String resultPath = "./Outputs/d0_dj.txt";
        Tool.writeMatrix(result, resultPath, tool.fileNames);
    }

    private double[] testSingle(double[] I, double[][] U, double[][] Omega) {
        double[] result = new double[3];// 0: d0 ; 1: dj(min among di) ; 2: index
        // OmegaI = U_trans * I_bar
        double[][] transU = Tool.transpose(U);
        double[] Omega_I = Tool.getOmega(transU, I);

        // IR = U * OmegaI
        double[] IR = Tool.getOmega(U, Omega_I);
        // d0 = dist(IR - I_bar)
        double d0 = Tool.dist(I, IR);
        // dj = min{ di = dist(OmegaI - Omega_i) }
        double di = 0, dj = Double.MAX_VALUE;
        double index = -1;
        for (int i = 0; i < Omega.length; i++) {
            di = Tool.dist(Omega_I, Omega[i]);
            if (dj > di) {
                index = i;
                dj = di;
            }
            // dj = Math.min(di, dj);
        }
        result[0] = d0;
        result[1] = dj;
        result[2] = index;
        return result;
    }
}
