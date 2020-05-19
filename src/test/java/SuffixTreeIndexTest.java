import index.SuffixTreeIndex;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SuffixTreeIndexTest {
    @Test
    public void suffixTreeIndexTest() {
        SuffixTreeIndex stIndex = new SuffixTreeIndex();
        stIndex.addText(Arrays.asList("e1", "e2", "e3"), "t1");
        stIndex.addText(Arrays.asList("e2", "e3", "e4"), "t2");

        List<String> path1 = Arrays.asList("e2", "e3");
        ArrayList<String> res1 = stIndex.search(path1);
        System.out.println("st index search " + path1 + " => result: " + res1);  // [t1, t2]

        List<String> path2 = Arrays.asList("e1", "e2", "e3");
        ArrayList<String> res2 = stIndex.search(path2);
        System.out.println("st index search " + path2 + " => result: " + res2);  // [t1]

        List<String> path3 = Collections.singletonList("e4");
        ArrayList<String> res3 = stIndex.search(path3);
        System.out.println("st index search " + path3 + " => result: " + res3);  // [t2]

        List<String> path4 = Arrays.asList("e4", "e5");
        ArrayList<String> res4 = stIndex.search(path4);
        System.out.println("st index search " + path4 + " => result: " + res4);  // []

        List<String> path5 = Arrays.asList("e1", "e3", "e2");
        ArrayList<String> res5 = stIndex.search(path5);
        System.out.println("st index search " + path5 + " => result: " + res5);  // []
    }
}
