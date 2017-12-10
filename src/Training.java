import java.io.File;

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
        for (int i = 0; i < tool.fileNum; i++) {
            double[][] matrix = Tool.transform(listR[i], tool.imageHeight, tool.imageWidth);
            String curPath = "./TestOutputs/" + tool.fileNames[i] + "_original.jpg";
            Tool.drawImage(matrix, curPath);
        }
    }

    /* ------- Private methods ------- */

}
