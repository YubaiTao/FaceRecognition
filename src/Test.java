/**
 * project: FaceRecognition
 *
 * @author YubaiTao on 09/12/2017.
 */
public class Test {
    Tool tool;
    double[][] listR;

    public Test(String directory, double[][] U, double[][] Omega, String[] trainFileNames, double T0, double T1){
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

        String d0djPath = "./Outputs/d0_dj.txt";
        Tool.writeMatrix(formatLines(result, tool.fileNames, trainFileNames), d0djPath);
        String resultPath = "./Outputs/result.txt";
        Tool.writeMatrix(formatLines(result, tool.fileNames, trainFileNames, T0, T1), resultPath);
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

    private String[] formatLines(double[][] result, String[] testFileNames, String[] trainFileNames) {
        String[] lines = new String[result.length];
        for (int i = 0; i < lines.length; i++) {
            boolean isMatch = Tool.getFileId(testFileNames[i]) == Tool.getFileId(trainFileNames[(int)result[i][2]]);
            String line = String.format("%-28s\t%-6.15f\t%-6.13f\t%-6b", testFileNames[i], result[i][0], result[i][1], isMatch);

            lines[i] = line;
        }

        return lines;
    }

    private String[] formatLines(double[][] result, String[] testFileNames, String[] trainFileNames, double T0, double T1) {
        String[] lines = new String[result.length];
        for (int i = 0; i < lines.length; i++) {
            boolean isMatch = Tool.getFileId(testFileNames[i]) == Tool.getFileId(trainFileNames[(int)result[i][2]]);
            boolean isRej = result[i][0] > T0;
            boolean isReco = result[i][1] < T1;
            String line = String.format("%-28s\t%-6.15f\t%-6.13f\t%-6s\t%-12s\t%-8s",
                    testFileNames[i], result[i][0], result[i][1],
                    isRej?"Reject":"Accept", isReco?"Recognized":"Unrecognized", isMatch?"Correct":"Wrong");

            lines[i] = line;
        }

        return lines;
    }
}
