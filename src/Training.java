/**
 * project: FaceRecognition
 *
 * @author YubaiTao on 09/12/2017.
 */
public class Training {
    Tool tool;
    // [number of training images][N * N(spanned image)]
    double[][] listR;

    // U and Omegas need to pass to Test
    double[][] U;
    double[][] Omegas;


    /* --- Constructor --- */
    public Training(String directory) {
        tool = new Tool(directory);
        listR = tool.listR;
        double[] meanVector = Tool.getMean(listR);
        System.out.println(meanVector.length);
        double[][] A = Tool.getA(listR, meanVector);
        double[][] transA = Tool.transpose(A);
        double[][] L = Tool.getCov(transA);
        double[][] V = Tool.getEigenVectors(L);
        U = Tool.getEigenfaces(A, V);
        Omegas = Tool.getOmegas(U, A);// [index][omega_i]
        Tool.printMatrix(U);

        /* output eigenfaces */
        double[][] transU = Tool.transpose(U);
        for (int i = 0; i < transU.length; i++) {
            String path = "./Outputs/eigenfaces/eigenface_" + i + ".jpg";
            Tool.drawEigenface(transU[i], path, tool.imageHeight, tool.imageWidth);
        }


    }

    /* ------- Private methods ------- */
}
