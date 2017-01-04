package org.fsd.dl4j;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Peter Davis on 30/12/2016.
 */
public class DataSetGenerator {

    public static void main(String [] args) throws IOException {
        FileWriter fw = new FileWriter(new File("src/main/resources/circle-train.csv"));
        createDataSet(fw, 10000, true);
        fw.close();
        fw = new FileWriter(new File("src/main/resources/circle-eval.csv"));
        createDataSet(fw, 10000, false);
        fw.close();

    }

    private static void createDataSet(FileWriter fw, int count, boolean skip) throws IOException {
        for(int i=0; i<count; i++) {
            double x = Math.random() * 4 - 2;
            double y = Math.random() * 4 - 2;
            int in = Math.sqrt(x * x + y * y) < 1 ? 1 : 0;

            if(skip && x > 1.2)
                continue;

            fw.write("" + in + "," + x + "," + y + "\n");
        }
    }
}
