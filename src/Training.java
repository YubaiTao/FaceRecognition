/**
 * project: FaceRecognition
 *
 * @author YubaiTao on 09/12/2017.
 */
public class Training {
    Tool tool;
    // [number of training images][N * N(spanned image)]
    double[][] listR;


    /* --- Constructor --- */
    public Training() {
        tool = new Tool("./TrainingImages");
        listR = tool.listR;
        double[] meanVector = Tool.getMean(listR);
        System.out.println(meanVector.length);
        double[][] A = Tool.getA(listR, meanVector);
        double[][] transA = Tool.transpose(A);
        double[][] L = Tool.getCov(transA);
        double[][] V = Tool.getEigenVectors(L);
        double[][] U = Tool.getEigenfaces(A, V);
        double[][] Omegas = Tool.getOmegas(U, A);
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
