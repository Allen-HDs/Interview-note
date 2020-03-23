import java.util.ArrayList;

/**
 * @BelongProjecet: interview
 * @BelongPackage: PACKAGE_NAME
 * @ClassName: HeapTest
 * @Description: TODO
 * @Author: Allen
 * @CreateDate: 2020/3/18 22:33
 * @Version: V1.0
 */
public class HeapTest {
    public static void main(String[] args) throws InterruptedException {
        ArrayList<HeapTest> heapTests = new ArrayList<>();
        while (true){
            heapTests.add(new HeapTest());
            Thread.sleep(10);
        }
    }
}
