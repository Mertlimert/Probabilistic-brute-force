import java.awt.*;
import java.util.*;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


import javax.swing.*;


class Test {

    public static void main(String[] args) {
        int numberOfRuns = 50;
        int bitStringLength = 20;
        double[] prob = generateRandomProbabilites(bitStringLength);
        ProbBruteSim sim = new ProbBruteSim(prob);

        int[] acctarget = sim.generateAccurateTarget();
        int[] unsorted = new int[sim.length];

        for (int i = 0; i < unsorted.length; i++) {
            unsorted[i] = i;
        }

        int[] sorted = sim.sortedIndices();

        List<Integer> list = new ArrayList<>();
        List<Integer> list1 = new ArrayList<>();
        double[] times = new double[numberOfRuns];
        double[] timesunsorted = new double[numberOfRuns];

        // Multiple Tests

        for (int i = 0; i < numberOfRuns; i++) {
            // sorted
            prob = generateRandomProbabilites(sim.length);
            sim = new ProbBruteSim(prob);
            sorted = sim.sortedIndices();
            acctarget = sim.generateAccurateTarget();
            long start2 = System.currentTimeMillis();
            int[] sortedguess = sim.generateGuess(sorted, acctarget);
            long end2 = System.currentTimeMillis();
            double timeTaken2 = (end2 - start2) / 1000.0;
            list.add(sim.attemps);
            times[i] = timeTaken2;
            // unsorted
            sim.attemps = 0;
            long start3 = System.currentTimeMillis();
            int[] unsortedguess = sim.generateNormalGuess(unsorted, acctarget);
            long end3 = System.currentTimeMillis();
            double timeTaken3 = (end3 - start3) / 1000.0;
            timesunsorted[i] = timeTaken3;
            list1.add(sim.attemps);

        }

        // Find Min,Max,Average Attemps

        Iterator<Integer> iter
                = list.iterator();
        Iterator<Integer> iter1
                = list1.iterator();

        int sum = 0;
        int max = iter.next();
        int min= max;
        while (iter.hasNext()) {
            int a = iter.next();
            sum += a;
            if(a > max) {
                max = a;
            }
            if(a < min) {
                min = a;
            }
        }

        int avg = (int)sum / list.size();

        int sum1 = 0;
        int max1 = iter1.next();
        int min1= max1;
        while (iter1.hasNext()) {
            int a = iter1.next();
            sum1 += a;
            if(a > max1) {
                max1 = a;
            }
            if(a < min1) {
                min1 = a;
            }
        }

        int avg1 = (int)sum1 / list1.size();

        // Chart Part

        DefaultCategoryDataset barDataset = new DefaultCategoryDataset();
        for (int i = 0; i < times.length; i++) {
            barDataset.addValue(times[i] * 1000, "Probabilistic", "Run " + (i + 1));
            barDataset.addValue(timesunsorted[i] * 1000, "Normal", "Run " + (i + 1));
        }

        JFreeChart barChart = ChartFactory.createBarChart(
                "Algorithm Runtime Comparison",
                "Run Number",
                "Time (milliseconds)",
                barDataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        CategoryPlot barPlot = barChart.getCategoryPlot();
        BarRenderer barRenderer = (BarRenderer) barPlot.getRenderer();
        barRenderer.setSeriesPaint(0, Color.RED);
        barRenderer.setSeriesPaint(1, Color.BLUE);
        barRenderer.setMaximumBarWidth(0.1);

        barPlot.setBackgroundPaint(Color.WHITE);
        barPlot.setRangeGridlinePaint(Color.GRAY);
        barPlot.setDomainGridlinePaint(Color.GRAY);

        NumberAxis barRangeAxis = (NumberAxis) barPlot.getRangeAxis();
        barRangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        barRangeAxis.setRange(0, Math.ceil(barRangeAxis.getUpperBound()));

        // Create and show line chart
        XYSeriesCollection lineDataset = new XYSeriesCollection();
        XYSeries sortedSeries = new XYSeries("Probabilistic");
        XYSeries unsortedSeries = new XYSeries("Normal");

        for (int i = 0; i < times.length; i++) {
            sortedSeries.add(i + 1, times[i] * 1000);
            unsortedSeries.add(i + 1, timesunsorted[i] * 1000);
        }

        lineDataset.addSeries(sortedSeries);
        lineDataset.addSeries(unsortedSeries);

        JFreeChart lineChart = ChartFactory.createXYLineChart(
                "Algorithm Runtime Comparison",
                "Run Number",
                "Time (milliseconds)",
                lineDataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        XYPlot linePlot = lineChart.getXYPlot();
        XYLineAndShapeRenderer lineRenderer = new XYLineAndShapeRenderer();
        lineRenderer.setSeriesPaint(0, Color.RED);
        lineRenderer.setSeriesPaint(1, Color.BLUE);
        lineRenderer.setSeriesStroke(0, new BasicStroke(2.0f));
        lineRenderer.setSeriesStroke(1, new BasicStroke(2.0f));
        lineRenderer.setSeriesShapesVisible(0, true);
        lineRenderer.setSeriesShapesVisible(1, true);

        linePlot.setRenderer(lineRenderer);
        linePlot.setBackgroundPaint(Color.WHITE);
        linePlot.setRangeGridlinePaint(Color.GRAY);
        linePlot.setDomainGridlinePaint(Color.GRAY);

        NumberAxis lineDomainAxis = (NumberAxis) linePlot.getDomainAxis();
        lineDomainAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        NumberAxis lineRangeAxis = (NumberAxis) linePlot.getRangeAxis();
        lineRangeAxis.setAutoRangeIncludesZero(true);

        // Create main frame
        JFrame frame = new JFrame("Runtime Comparisons");
        frame.setLayout(new BorderLayout());  // Changed to BorderLayout for better control
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create main panel for charts
        JPanel chartsPanel = new JPanel(new GridLayout(2, 1, 0, 5));  // 2 rows, 1 column, 5px vertical gap

        // Create statistics panel with minimal height
        JPanel statsPanel = new JPanel(new GridLayout(1, 2, 5, 0));  // Changed to horizontal layout
        statsPanel.setBorder(BorderFactory.createTitledBorder("Statistics"));
        statsPanel.setPreferredSize(new Dimension(800, 60));  // Reduced height further

        // Create panels for sorted and unsorted statistics
        JPanel sortedStatsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));  // Minimal vertical gap
        sortedStatsPanel.setBorder(BorderFactory.createTitledBorder("Probabilistic"));
        sortedStatsPanel.add(new JLabel(String.format("Average: %d attempts | ", avg)));
        sortedStatsPanel.add(new JLabel(String.format("Max: %d attempts | ", max)));
        sortedStatsPanel.add(new JLabel(String.format("Min: %d attempts", min)));

        JPanel unsortedStatsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));  // Minimal vertical gap
        unsortedStatsPanel.setBorder(BorderFactory.createTitledBorder("Normal"));
        unsortedStatsPanel.add(new JLabel(String.format("Average: %d attempts | ", avg1)));
        unsortedStatsPanel.add(new JLabel(String.format("Max: %d attempts | ", max1)));
        unsortedStatsPanel.add(new JLabel(String.format("Min: %d attempts", min1)));

        // Add statistics panels to main stats panel
        statsPanel.add(sortedStatsPanel);
        statsPanel.add(unsortedStatsPanel);

        // Configure charts with larger size
        ChartPanel barChartPanel = new ChartPanel(barChart);
        ChartPanel lineChartPanel = new ChartPanel(lineChart);

        barChartPanel.setPreferredSize(new Dimension(800, 250));  // Increased height
        lineChartPanel.setPreferredSize(new Dimension(800, 250));  // Increased height

        // Add components to panels
        chartsPanel.add(barChartPanel);
        chartsPanel.add(lineChartPanel);

        // Add panels to frame
        frame.add(chartsPanel, BorderLayout.CENTER);
        frame.add(statsPanel, BorderLayout.SOUTH);

        frame.pack();
        frame.setVisible(true);

        // Print Max,Min and Averages

        System.out.printf("in %d attemps avarage attemp is: %d \n",numberOfRuns,avg);
        System.out.printf("in %d attemps max attemp is: %d \n",numberOfRuns,max);
        System.out.printf("in %d attemps min attemp is: %d \n",numberOfRuns,min);
        System.out.println("------ Unsorted ---------");
        System.out.printf("in %d attemps avarage attemp is: %d \n",numberOfRuns,avg1);
        System.out.printf("in %d attemps max attemp is: %d \n",numberOfRuns,max1);
        System.out.printf("in %d attemps min attemp is: %d \n",numberOfRuns,min1);

        // Compare Probabilistic BF and Normal BF

        sim.attemps = 0;
        System.out.println("-------- Probabilistic Brute Force Search --------");
        long start = System.currentTimeMillis();
        System.out.println("Guess:  " + toString(sim.generateGuess(sorted, acctarget)) + "\nAttemps: " + sim.attemps);
        long end = System.currentTimeMillis();
        double timeTaken = (end - start) / 1000.0;
        System.out.printf("Search took: %6f seconds\n\n", timeTaken);
        sim.attemps = 0;
        System.out.println("-------- Normal Brute Force Search --------");
        long start1 = System.currentTimeMillis();
        System.out.println("Guess:  " + toString(sim.generateNormalGuess(unsorted, acctarget)) + "\nAttemps: " + sim.attemps);
        long end1 = System.currentTimeMillis();
        double timeTaken1 = (end1 - start1) / 1000.0;
        System.out.printf("Search took: %6f seconds", timeTaken1);

    }

    public static void lowPossibleIndexes(Map<int[],double[]> cases){
        for (Map.Entry<int[], double[]> entry : cases.entrySet()) {
            Map<List<Integer>, Integer> map = calculateTargetProbability(entry.getKey(), entry.getValue());
            for (Map.Entry<List<Integer>, Integer> ent : map.entrySet()) {
                Iterator<Integer> it
                        = ent.getKey().iterator();
                while (it.hasNext()) {
                    int a = it.next();
                    System.out.print("(" + a + ", ");
                    System.out.println(entry.getValue()[a] + "), ");
                }
                System.out.println("\nCount: " + ent.getValue());
            }

        }
    }

    public static Map<List<Integer>,Integer> calculateTargetProbability(int[] target,double[] probab) {
        int counter = 0;
        Map<List<Integer>,Integer> map = new HashMap<>();
        List<Integer> list = new ArrayList<>();


        for (int i = 0; i < target.length; i++) {
            if (probab[i] > 0.5) {
                if(target[i] != 1) {
                    list.add(i);
                    counter++;
                }
            } else {
                if(target[i] != 0) {
                    list.add(i);
                    counter++;
                }
            }
        }
        map.put(list,counter);
        return map;
    }


    public static String toString(int[] array) {
        String result = String.valueOf(array[0]);
        for (int i = 1; i < array.length; i++) {
            result += ", " + array[i];

        }
        return result;
    }

    public static String toString(double[] array) {
        String result = String.valueOf(array[0]).substring(0, 4);
        for (int i = 1; i < array.length; i++) {
            result += ", " + String.valueOf(array[i]).substring(0, 4);

        }
        return result;
    }

    public static double[] generateRandomProbabilites(int len) {

        double[] probs = new double[len];

        for (int i = 0; i < len; i++) {
            probs[i] = Math.random();
        }

        return probs;

    }
}

public class ProbBruteSim {

    private final double[] probabilities;
    final int length;
    private final Random random;
    int attemps;

    ProbBruteSim(double[] probabilities) {
        this.probabilities = probabilities;
        this.length = probabilities.length;
        this.random = new Random();
        this.attemps = 0;
    }

    public double[] changeProbs(double[] probs) {
        for (int i = 0; i < probs.length; i++) {
            if (probs[i] < 0.5) {
                probs[i] = 1 - probs[i];
            }
        }
        return probs;
    }

    public int[] generateAccurateTarget() {

        int[] guess = new int[length];

        for (int i = 0; i < length; i++) {
            guess[i] = random.nextDouble() < probabilities[i] ? 1 : 0;
        }

        return guess;

    }

    public int[] generateGuess(int[] sortedIndices, int[] target) {
        int n = sortedIndices.length;
        int[] guess = generateOptimalGuess();

        if (test(guess, target)) {
            return guess;
        }

        for (long idx = 1; idx < (1L << n); idx++) {
            int[] currentGuess = generateOptimalGuess();

            for (int i = 0; i < n; i++) {
                if ((idx & (1L << i)) != 0) {
                    int index = sortedIndices[i];
                    currentGuess[index] = 1 - currentGuess[index];
                }
            }
            attemps++;

            if (test(currentGuess, target)) {
                return currentGuess;
            }
        }
        return null;
    }

    public int[] generateNormalGuess(int[] sortedIndices, int[] target) {
        int n = sortedIndices.length;
        int[] guess = generateFirstGuess();

        if (test(guess, target)) {
            return guess;
        }

        for (long idx = 1; idx < (1L << n); idx++) {
            int[] currentGuess = generateFirstGuess();

            for (int i = 0; i < n; i++) {
                if ((idx & (1L << i)) != 0) {
                    int index = sortedIndices[i];
                    currentGuess[index] = 1 - currentGuess[index];
                }
            }
            attemps++;

            if (test(currentGuess, target)) {
                return currentGuess;
            }
        }
        return null;
    }

    public int[] sortedIndices() {
        double[] probs = probabilities.clone();
        int[] sortedIndices = new int[length];
        for (int i = 0; i < length; i++) {
            sortedIndices[i] = i;
        }

        double[] changedProbs = changeProbs(probs);

        for (int i = 0; i < length - 1; i++) {
            for (int j = 0; j < length - i - 1; j++) {
                if (changedProbs[sortedIndices[j]] > changedProbs[sortedIndices[j + 1]]) {
                    int temp = sortedIndices[j];
                    sortedIndices[j] = sortedIndices[j + 1];
                    sortedIndices[j + 1] = temp;
                }
            }
        }
        return sortedIndices;
    }

    public int[] generateOptimalGuess() {
        int[] guess = new int[length];
        for (int i = 0; i < length; i++) {
            if (probabilities[i] > 0.5) {
                guess[i] = 1;
            } else {
                guess[i] = 0;
            }

        }
        return guess;
    }
    public int[] generateFirstGuess() {
        int[] guess = new int[length];
        for (int i = 0; i < length; i++) {
            guess[i] = 1;
        }
        return guess;
    }



    public boolean test(int[] guess, int[] target) {
        return Arrays.equals(guess, target);
    }

}