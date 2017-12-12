
/**
 * project: FaceRecognition
 *
 * @author YubaiTao on 09/12/2017.
 */
public class FaceRecognition {
    public static void main(String args[]) {
        Training training = new Training("./TrainingImages");
        new Test("./TestImages", training, 41, 2300);
        System.out.println("Complete.");
        System.out.println("All results are in 'Outputs' folder.");
    }
}
