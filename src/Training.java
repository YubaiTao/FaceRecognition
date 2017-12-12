
/**
 * project: FaceRecognition
 *
 * @author YubaiTao on 09/12/2017.
 */
public class Training {
    Tool tool;
    String[] fileNames;



    // [number of training images][N * N(spanned image)]
    double[][] listR;

    double[] meanVector;

    // U and Omegas need to pass to Test
    double[][] U;
    double[][] Omegas;


    /* --- Constructor --- */
    public Training(String directory) {
        tool = new Tool(directory);
        listR = tool.listR;
        fileNames = tool.fileNames;
        meanVector = Tool.getMean(listR);
        double[][] A = Tool.getA(listR, meanVector);
        double[][] transA = Tool.transpose(A);
        double[][] L = Tool.getCov(transA);
        double[][] V = Tool.getEigenVectors(L);
        U = Tool.getEigenfaces(A, V);
        Omegas = Tool.getOmegas(U, A);// [index][omega_i]

        writeToFile();
    }

    private void writeToFile() {
        String pathU = "./Outputs/U.txt";
        String pathOmega = "./Outputs/Omegas.txt";
        Tool.writeMatrix(U, pathU);
        Tool.writeMatrix(Omegas, pathOmega);

//        for (int i = 0; i < tool.fileNames.length; i++) {
//            System.out.println(tool.fileNames[i]);
//        }

        /* output eigenfaces */
        double[][] transU = Tool.transpose(U);
        for (int i = 0; i < transU.length; i++) {
            String path = "./Outputs/eigenfaces/eigenface_" + i + ".jpg";
            Tool.drawEigenface(transU[i], path, tool.imageHeight, tool.imageWidth);
        }
    }

}
