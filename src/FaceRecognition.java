
/**
 * project: FaceRecognition
 *
 * @author YubaiTao on 09/12/2017.
 */
public class FaceRecognition {
    public static void main(String args[]) {
        Training training = new Training("./TrainingImages");
        Test test = new Test("./TestImages", training.U, training.Omegas);
    }
}
