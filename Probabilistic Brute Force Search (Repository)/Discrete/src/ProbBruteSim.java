import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


import javax.swing.*;


class Test {

    public static void main(String[] args) {
        int numberOfRuns = 1000;
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
            double timeTaken2 = (end2 - start2);
            list.add(sim.attemps);
            times[i] = timeTaken2;
            // unsorted
            sim.attemps = 0;
            long start3 = System.currentTimeMillis();
            int[] unsortedguess = sim.generateNormalGuess(unsorted, acctarget);
            long end3 = System.currentTimeMillis();
            double timeTaken3 = (end3 - start3);
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
            barDataset.addValue(times[i], "Probabilistic", "Run " + (i + 1));
            barDataset.addValue(timesunsorted[i], "Normal", "Run " + (i + 1));
        }

// Create histogram datasets
        int numBins = 20;
        HistogramDataset probDataset = new HistogramDataset();
        HistogramDataset normDataset = new HistogramDataset();
        probDataset.addSeries("Probabilistic Runtime", times, numBins);
        normDataset.addSeries("Normal Runtime", timesunsorted, numBins);

// Create charts
        JFreeChart probHistogram = ChartFactory.createHistogram(
                "Probabilistic Search Runtime Distribution",
                "Runtime (milliseconds)",
                "Frequency",
                probDataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        JFreeChart normHistogram = ChartFactory.createHistogram(
                "Normal Search Runtime Distribution",
                "Runtime (milliseconds)",
                "Frequency",
                normDataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

// Customize plots
        XYPlot probPlot = (XYPlot) probHistogram.getPlot();
        probPlot.setBackgroundPaint(Color.WHITE);
        probPlot.setRangeGridlinePaint(Color.GRAY);
        probPlot.setDomainGridlinePaint(Color.GRAY);

        XYPlot normPlot = (XYPlot) normHistogram.getPlot();
        normPlot.setBackgroundPaint(Color.WHITE);
        normPlot.setRangeGridlinePaint(Color.GRAY);
        normPlot.setDomainGridlinePaint(Color.GRAY);

// Customize renderers
        XYBarRenderer probRenderer = (XYBarRenderer) probPlot.getRenderer();
        probRenderer.setSeriesPaint(0, Color.RED);
        probRenderer.setBarPainter(new StandardXYBarPainter());
        probRenderer.setDrawBarOutline(true);
        probRenderer.setSeriesOutlinePaint(0, Color.BLACK);

        XYBarRenderer normRenderer = (XYBarRenderer) normPlot.getRenderer();
        normRenderer.setSeriesPaint(0, Color.BLUE);
        normRenderer.setBarPainter(new StandardXYBarPainter());
        normRenderer.setDrawBarOutline(true);
        normRenderer.setSeriesOutlinePaint(0, Color.BLACK);

// Create frame with grid layout
        JFrame histFrame = new JFrame("Runtime Distributions");
        histFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        histFrame.setLayout(new GridLayout(2, 1, 0, 10));

// Add chart panels
        ChartPanel probChartPanel = new ChartPanel(probHistogram);
        ChartPanel normChartPanel = new ChartPanel(normHistogram);
        probChartPanel.setPreferredSize(new Dimension(800, 300));
        normChartPanel.setPreferredSize(new Dimension(800, 300));

        histFrame.add(probChartPanel);
        histFrame.add(normChartPanel);

        histFrame.pack();
        histFrame.setLocationRelativeTo(null);
        histFrame.setVisible(true);

        // BarChart and XYline

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
            sortedSeries.add(i + 1, times[i]);
            unsortedSeries.add(i + 1, timesunsorted[i]);
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

        // New Frame

        // Create new frame and chart for sorted times
        JFrame sortedTimesFrame = new JFrame("Sorted Times Comparison");
        sortedTimesFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

// Sort the times arrays
        double[] sortedProbTimes = times.clone();
        double[] sortedNormalTimes = timesunsorted.clone();
        Arrays.sort(sortedProbTimes);
        Arrays.sort(sortedNormalTimes);

// Create dataset for sorted times
        XYSeriesCollection sortedDataset = new XYSeriesCollection();
        XYSeries sortedProbSeries = new XYSeries("Probabilistic (Sorted)");
        XYSeries sortedNormalSeries = new XYSeries("Normal (Sorted)");

        for (int i = 0; i < times.length; i++) {
            sortedProbSeries.add(i + 1, sortedProbTimes[i]);
            sortedNormalSeries.add(i + 1, sortedNormalTimes[i]);
        }

        sortedDataset.addSeries(sortedProbSeries);
        sortedDataset.addSeries(sortedNormalSeries);

// Create the chart
        JFreeChart sortedChart = ChartFactory.createXYLineChart(
                "Sorted Runtime Comparison",
                "Run Number",
                "Time (milliseconds)",
                sortedDataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

// Customize the plot
        XYPlot sortedPlot = sortedChart.getXYPlot();
        XYLineAndShapeRenderer sortedRenderer = new XYLineAndShapeRenderer();
        sortedRenderer.setSeriesPaint(0, Color.RED);
        sortedRenderer.setSeriesPaint(1, Color.BLUE);
        sortedRenderer.setSeriesStroke(0, new BasicStroke(2.0f));
        sortedRenderer.setSeriesStroke(1, new BasicStroke(2.0f));
        sortedRenderer.setSeriesShapesVisible(0, true);
        sortedRenderer.setSeriesShapesVisible(1, true);

        sortedPlot.setRenderer(sortedRenderer);
        sortedPlot.setBackgroundPaint(Color.WHITE);
        sortedPlot.setRangeGridlinePaint(Color.GRAY);
        sortedPlot.setDomainGridlinePaint(Color.GRAY);

// Configure axes
        NumberAxis sortedDomainAxis = (NumberAxis) sortedPlot.getDomainAxis();
        sortedDomainAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        NumberAxis sortedRangeAxis = (NumberAxis) sortedPlot.getRangeAxis();
        sortedRangeAxis.setAutoRangeIncludesZero(true);

// Create chart panel and add to frame
        ChartPanel sortedChartPanel = new ChartPanel(sortedChart);
        sortedChartPanel.setPreferredSize(new Dimension(800, 400));

// Add statistics panel for sorted times
        sortedStatsPanel = new JPanel(new GridLayout(2, 1));
        sortedStatsPanel.setBorder(BorderFactory.createTitledBorder("Sorted Times Statistics"));

// Create text areas to display sorted times
        JTextArea probTimesArea = new JTextArea(3, 40);
        probTimesArea.setEditable(false);
        probTimesArea.setText("Probabilistic Sorted Times (ms):\n" +
                Arrays.stream(sortedProbTimes)
                        .mapToObj(t -> String.format("%.3f", t * 1000))
                        .collect(Collectors.joining(", ")));

        JTextArea normalTimesArea = new JTextArea(3, 40);
        normalTimesArea.setEditable(false);
        normalTimesArea.setText("Normal Sorted Times (ms):\n" +
                Arrays.stream(sortedNormalTimes)
                        .mapToObj(t -> String.format("%.3f", t * 1000))
                        .collect(Collectors.joining(", ")));

        sortedStatsPanel.add(new JScrollPane(probTimesArea));
        sortedStatsPanel.add(new JScrollPane(normalTimesArea));

// Add components to frame
        sortedTimesFrame.setLayout(new BorderLayout());
        sortedTimesFrame.add(sortedChartPanel, BorderLayout.CENTER);
        sortedTimesFrame.add(sortedStatsPanel, BorderLayout.SOUTH);

        sortedTimesFrame.pack();
        sortedTimesFrame.setLocationRelativeTo(null);
        sortedTimesFrame.setVisible(true);

        // Pie Chart

        // Count runs where normal search was faster
        int normalFasterCount = 0;
        for (int i = 0; i < numberOfRuns; i++) {
            if (timesunsorted[i] < times[i]) {
                normalFasterCount++;
            }
        }

// Create dataset for pie chart
        DefaultPieDataset pieDataset = new DefaultPieDataset();
        pieDataset.setValue("Normal Faster", normalFasterCount);
        pieDataset.setValue("Probabilistic Faster", numberOfRuns - normalFasterCount);

// Create pie chart
        JFreeChart pieChart = ChartFactory.createPieChart(
                "Runtime Comparison Distribution",
                pieDataset,
                true,
                true,
                false
        );

// Customize appearance
        PiePlot plot = (PiePlot) pieChart.getPlot();
        plot.setSectionPaint("Normal Faster %", Color.BLUE);
        plot.setSectionPaint("Probabilistic Faster %", Color.RED);
        plot.setBackgroundPaint(Color.WHITE);

// Create new frame
        JFrame pieFrame = new JFrame("Runtime Distribution");
        pieFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

// Add chart to frame
        ChartPanel pieChartPanel = new ChartPanel(pieChart);
        pieChartPanel.setPreferredSize(new Dimension(500, 400));
        pieFrame.add(pieChartPanel);

// Add summary label
        JLabel summaryLabel = new JLabel(String.format(
                "Normal search was faster in %d out of %d runs (%.1f%%)",
                normalFasterCount, numberOfRuns,
                (normalFasterCount * 100.0 / numberOfRuns)
        ));
        summaryLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        pieFrame.add(summaryLabel, BorderLayout.SOUTH);

        pieFrame.pack();
        pieFrame.setLocationRelativeTo(null);
        pieFrame.setVisible(true);

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