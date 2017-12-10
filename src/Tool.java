import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * project: FaceRecognition
 *
 * @author YubaiTao on 09/12/2017.
 */

/*
 * Read all files from a directory,
 * return them as a list of spanned images.
 *
 * Also include some static helper methods,
 * like drawing image to a file.
 *
 */
public class Tool {

    String directory;
    int fileNum;
    String[] fileNames;
    String[] filePaths;
    int imageHeight;
    int imageWidth;
    // [number of training images][N * N(spanned image)]
    double[][] listR;


    public Tool(String directory) {


        this.directory = directory;
        fileNum = getFileNum(directory);
        fileNames = new String[fileNum];
        filePaths = new String[fileNum];
        setFileParas(directory, fileNames, filePaths);
        getImageParas(filePaths[0]);
        loadImages(filePaths);

    }

    public static void drawImage(double[][] matrix, String path) {
        File newImgFile = new File(path);
        int width = matrix[0].length;
        int height = matrix.length;
        try {
            BufferedImage image = new BufferedImage(matrix[0].length, matrix.length, BufferedImage.TYPE_BYTE_GRAY);
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    Color c = new Color((int)matrix[j][i], (int)matrix[j][i], (int)matrix[j][i]);
                    image.setRGB(i, j, c.getRGB());
                }
            }
            ImageIO.write(image, "jpg", newImgFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static double[][] transform(double[] spannedImage, int height, int width) {
        double[][] matrix = new double[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                matrix[i][j] = spannedImage[i * width + j];
            }
        }
        return matrix;
    }

    public static double[] getMean(double[][] matrix) {
        return new double[0];
    }


    private int getFileNum(String directory) {
        File folder = new File(directory);
        File[] listOfFiles = folder.listFiles();
        int counter = 0;
        for (File f : listOfFiles) {
            if (f.isFile()) {
                counter++;
            }
        }
        return counter;
    }

    private void setFileParas(String directory, String[] fileNames, String[] filePaths) {
        File folder = new File(directory);
        File[] listOfFiles = folder.listFiles();
        try {
            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile()) {
                    fileNames[i] = listOfFiles[i].getName();
                    filePaths[i] = directory + "/" + fileNames[i];
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("No image file in the target folder");
        }
//        for (File f : listOfFiles) {
//            System.out.println(f.getName());
//        }
    }

    private void getImageParas(String path) {
        File imgFile = new File(path);
        try {
            BufferedImage image = ImageIO.read(imgFile);
            imageHeight = image.getHeight();
            imageWidth = image.getWidth();
        } catch (IOException i){
            i.printStackTrace();
            return;
        }
        listR = new double[filePaths.length][imageHeight * imageWidth];
    }

    private void loadImages(String[] filePaths){
        for (int i = 0; i < fileNum; i++) {
            listR[i] = loadImage(filePaths[i]);
        }
    }

    private double[] loadImage(String path) {
        File imgFile = new File(path);
        double[] spannedImage;
        try {
            BufferedImage image = ImageIO.read(imgFile);
            spannedImage = new double[imageHeight * imageWidth];
            for (int i = 0; i < imageHeight; i++) {
                for (int j = 0; j < imageWidth; j++) {
                    Color c = new Color(image.getRGB(j, i));
                    // System.out.println(c.getRed() + " " + c.getGreen() + " " + c.getBlue());
                    int value = c.getRed();
                    spannedImage[i * imageWidth + j] = value;
                }
            }
        } catch (IOException i) {
            i.printStackTrace();
            return null;
        }
        return spannedImage;
    }
}
