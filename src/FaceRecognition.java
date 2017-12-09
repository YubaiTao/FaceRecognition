import org.ejml.data.DMatrixRMaj;

/**
 * project: FaceRecognition
 *
 * @author YubaiTao on 09/12/2017.
 */
public class FaceRecognition {
    public static void main(String args[]) {
        DMatrixRMaj A = new DMatrixRMaj(2,3,true,1.1,2.34,3.35436,4345,59505,0.00001234);

        A.print();
        System.out.println();
        A.print("%e");
        System.out.println();
        A.print("%10.2f");
    }
}
