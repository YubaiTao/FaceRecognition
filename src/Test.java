/**
 * project: FaceRecognition
 *
 * @author YubaiTao on 09/12/2017.
 */
public class Test {
    Tool tool;
    double[][] listR;
    double[][] U;
    double[][] Omega;
    String[] trainFileNames;
    double[] trainMeanVector;

    // public Test(String directory, double[][] U, double[][] Omega, String[] trainFileNames, double T0, double T1){
    public Test(String directory, Training training, double T0, double T1){

        this.U = training.U;
        this.Omega = training.Omegas;
        this.trainFileNames = training.fileNames;
        trainMeanVector = training.meanVector;

        tool = new Tool(directory);
        listR = tool.listR;
        double[] meanVector = Tool.getMean(listR);
        // substract mean face from input face
        double[][] listI = Tool.transpose(Tool.getA(listR, meanVector));
        // []: d0 ; dj(min[di])
        double[][] result = new double[listR[0].length][3];
        for (int i = 0; i < listR[0].length; i++) {
            result[i] = testSingle(listI[i], U, Omega, tool.fileNames[i]);
        }

        String d0djPath = "./Outputs/d0_dj.txt";
        Tool.writeMatrix(formatLines(result, tool.fileNames, trainFileNames), d0djPath);
        String resultPath = "./Outputs/inter_result" + "_T0:" + (int)T0 + "_T1:" + (int)T1 + ".txt";
        Tool.writeMatrix(formatLines(result, tool.fileNames, trainFileNames, T0, T1), resultPath);
        String finalResultPath = "./Outputs/final_result" + "_T0:" + (int)T0 + "_T1:" + (int)T1 + ".txt";
        Tool.writeMatrix(formatLines(result, tool.fileNames, trainFileNames, T0, T1, true), finalResultPath);

    }

    /* face recognition on single image */
    private double[] testSingle(double[] I, double[][] U, double[][] Omega, String fileName) {
        double[] result = new double[3];// 0: d0 ; 1: dj(min among di) ; 2: index
        // OmegaI = U_trans * I_bar
        double[][] transU = Tool.transpose(U);
        double[] Omega_I = Tool.getOmega(transU, I);

        // IR = U * OmegaI
        double[] IR = Tool.getOmega(U, Omega_I);
        double[] IR_recons = new double[IR.length];
        // add mean face
        for (int i = 0; i < IR.length; i++) {
            IR_recons[i] = IR[i] + trainMeanVector[i];
        }
        double[][] matrixIR = Tool.transform(IR_recons, tool.imageHeight, tool.imageWidth);
        String pathIR = "./Outputs/reconstructed_faces/reconstructed_face_" + Tool.getFileId(fileName)
                + "_" + Tool.getFileSymbol(fileName)+ ".jpg";
        Tool.drawImage(matrixIR, pathIR);

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

    private String[] formatLines(double[][] result, String[] testFileNames, String[] trainFileNames, double T0, double T1, boolean sign) {
        String[] lines = new String[result.length];
        for (int i = 0; i < lines.length; i++) {
            boolean isMatch = Tool.getFileId(testFileNames[i]) == Tool.getFileId(trainFileNames[(int)result[i][2]]);
            boolean isRej = result[i][0] > T0;
            boolean isReco = result[i][1] < T1;
            String state = getState(isMatch, isRej, isReco, Tool.getFileId(trainFileNames[(int)result[i][2]]));
            String line = String.format("%-28s\t%-16s",
                    testFileNames[i], state);

            lines[i] = line;
        }
        return lines;
    }

    private String getState(boolean isMatch, boolean isRej, boolean isReco, int ID) {
        if (isRej) {
            return isMatch?"non-face (WRONG)":"non-face";
        }
        if (isReco) {
            if (isMatch) {
                return Integer.toString(ID);
            } else {
                return Integer.toString(ID) + " (WRONG)";
            }
        } else {
            return isMatch?"unknown face (WRONG)":"unknown face";
        }
    }
}
