/** ------------------------------------------------------------------------- *
 * libpomdp
 * ========
 * File: Utils.java
 * Description: useful general routines - everything in this class is static
 * Copyright (c) 2009, 2010 Diego Maniloff
 * Copyright (c) 2010 Mauricio Araya
 --------------------------------------------------------------------------- */

package util;

import model.POMDP;
import org.jfree.data.category.DefaultCategoryDataset;
import test.TestResult;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Random;

public class Utils {

    //  set the random only once for every instance
    public static final Random random = new Random(System.currentTimeMillis());

    public static void writeTestToFile(
            POMDP model,
            List<TestResult> res,
            String filename,
            String pre) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(filename));
            out.write(pre + "\n");
            out.newLine();
            out.write(model + "\n");
            out.newLine();
            for (TestResult tr : res) {
                out.write(tr.toString() + "\n");
                out.newLine();
                out.flush();
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void generateChart(List<TestResult> res, String subtitle) {
        // Create Charts
        // Iteration Time Comparision
        DefaultCategoryDataset dsTimeIter = new DefaultCategoryDataset();
        DefaultCategoryDataset dsTimeSolver = new DefaultCategoryDataset();
        DefaultCategoryDataset dsValueSolver = new DefaultCategoryDataset();

        for (TestResult r : res) {
            String name = r.getTestName();
            dsTimeIter.addValue(r.getInitTime(), name, "init");

            dsTimeSolver.addValue(r.getInitTime(), "Init Time", name);
            dsTimeSolver.addValue(r.getTotalTime(), "Total Time", name);
            dsValueSolver.addValue(r.getValue(), "Value", name);

            for (int i = 0; i < r.getIterTime().size(); i++) {
                dsTimeIter.addValue(r.getIterTime().get(i), name, "iter" + i);
            }
        }
        Chart.showLineChart("Iteration Time Comparison",
                            "Iteration Time Comparison " + subtitle,
                            "Iteration",
                            "Time(ms)",
                            dsTimeIter);


        Chart.showBarChart("Solver Time Consume Comparison",
                           "Solver Time Consume Comparison " + subtitle,
                           "Solver",
                           "Time(ms)",
                           dsTimeSolver);

        Chart.showBarChart("Solver Expected Value Comparison",
                           "Solver Expected Value Comparison " + subtitle,
                           "Solver",
                           "Value",
                           dsValueSolver);
    }
} // Utils
