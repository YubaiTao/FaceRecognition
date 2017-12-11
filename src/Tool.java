import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.decomposition.eig.SwitchingEigenDecomposition_DDRM;

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

    public static void printMatrix(double[][] matrix) {
        System.out.println();
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0;j < matrix[0].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
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

    /* get mean of a set of double[] vectors */
    public static double[] getMean(double[][] matrix) {
        double n = matrix.length;
        double sum = 0;
        double[] mean = new double[(int)n];
        for (int i = 0; i < matrix.length; i++) {
            sum = 0;
            for (int j = 0; j < matrix[0].length; j++) {
                sum += matrix[i][j];
            }
            mean[i] = sum / matrix[0].length;
        }
        return mean;
    }

    /* A = Ri - mean */
    public static double[][] getA(double[][] R, double[] mean) {
        double[][] A = new double[R.length][R[0].length];
        for (int i = 0; i < R.length; i++) {
            for (int j = 0; j < R[0].length; j++) {
                A[i][j] = R[i][j] - mean[i];
            }
        }
        return A;
    }

    /* transpose a matrix */
    public static double[][] transpose(double[][] matrix) {
        double[][] transposed = new double[matrix[0].length][matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                transposed[j][i] = matrix[i][j];
            }
        }
        return transposed;
    }

    /* compute the covariance matrix C */
    /* matrix: A ; C = A*A(transpose) */
    public static double[][] getCov(double[][] matrix) {
        double[][] C = new double[matrix.length][matrix.length];
        // double[][] transpose = transpose(matrix);
        // no need for explicitly compute the transposed matrix.
        int sum = 0;
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                sum = 0;
                for (int k = 0; k < matrix[0].length; k++) {
                    sum += matrix[i][k] * matrix[j][k];
                }
                C[i][j] = sum;
            }
        }
        return C;
    }

    /* compute eigen vector of input matrix, the input matrix must be symmetric */
    public static double[][] getEigenVectors(double[][] matrix) {
        int matrixSize = matrix.length;
        double[][] eigenVectors = new double[matrixSize][matrixSize];
        SwitchingEigenDecomposition_DDRM eigenDecompostion = new SwitchingEigenDecomposition_DDRM(matrixSize);
        DMatrixRMaj m = new DMatrixRMaj(matrix);
        if (eigenDecompostion.decompose(m)) {
            System.out.println("Matrix successfully decomposed.");
        } else {
            System.out.println("Matrix failed decomposition.");
        }

        DMatrixRMaj ev;
        for (int i= 0; i < matrixSize; i++) {
            ev = eigenDecompostion.getEigenVector(i);
            for (int j = 0; j < matrixSize; j++) {
                eigenVectors[i][j] = ev.get(j, 0);
            }
        }
        return eigenVectors;
    }

    public static double[][] getEigenfaces(double[][] A, double[][] V) {
        double[][] eigenfaces = new double[A.length][V.length];

        for (int i = 0; i < V.length; i++) {
            double sum = 0;
            for (int j = 0; j < A.length; j++) {
                for (int k = 0; k < V[0].length; k++) {
                    eigenfaces[j][i] += A[j][k] * V[i][k];
                }
                sum += eigenfaces[j][i] * eigenfaces[j][i];
            }
            double norm = Math.sqrt(sum);
            for (int l = 0; l < A.length; l++) {
                eigenfaces[l][i] /= norm;
            }
        }

        return eigenfaces;
    }

    public static double[][] getOmegas(double[][] U, double[][] R) {
        double[][] transU = transpose(U);
        double[][] transR = transpose(R);
        double[][] omegas = new double[transR.length][transU.length];
        for (int i = 0; i < transR.length; i++) {
            double[] omega = getOmega(transU, transR[i]);
            omegas[i] = omega;
        }
        return omegas;
    }

    public static double[] getOmega(double[][] transU, double[] Ri) {
        double[] omega = new double[transU.length];
        for (int i = 0; i < transU.length; i++) {
            for (int j = 0; j < Ri.length; j++) {
                omega[i] += transU[i][j] * Ri[j];
            }
        }
        return omega;
    }


    public static void drawEigenface(double[] eigenface, String path, int height, int width) {
        double min = Double.MAX_VALUE;
        double max = -Double.MAX_VALUE;
        for (int i = 0; i < eigenface.length; i++) {
            min = Math.min(min, eigenface[i]);
            max = Math.max(max, eigenface[i]);
        }
        double[] image = new double[eigenface.length];
        for (int i = 0; i < eigenface.length; i++) {
            image[i] = (int)(255.0 * (eigenface[i] - min) / (max - min));
        }
        drawImage(transform(image, height, width), path);
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
        listR = transpose(listR);
    }

    /* load an image file from a file path, return a spanned vector of image pixels */
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
