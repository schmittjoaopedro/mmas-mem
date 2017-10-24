package schmitt.joao.aco;

/**
 * Calculate the statistics of the algorithm evolution
 */
public class Statistics {

    /**
     * Environment to be analysed
     */
    private Environment environment;

    /**
     * The cost of the best so far tour
     */
    private double bestSoFar = Double.MAX_VALUE;

    /**
     * The route of the best so far tour
     */
    private int[] bestTourSoFar;

    /**
     * A swing component to visualize graphically the algorithm evolution
     */
    private Visualizer visualizer;

    /**
     * Current tsp file being solved
     */
    private String tspFile;
    /**
     * Needs an environment and the coordinates of the vertices to be drawn
     *
     * @param environment
     * @param coordinates
     */
    public Statistics(String tspFile, Environment environment, double[][] coordinates) {
        this.environment = environment;
        //this.visualizer = new Visualizer(coordinates);
        this.tspFile = tspFile;
    }

    /**
     * For each iteration get the best, the worst and the mean tour cost
     * of all tours constructed by the ants, if a improvement was detected
     * show show the values.
     *
     * @param phase
     */
    public void calculateStatistics(int phase) {
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        double total = 0.0;
        Ant bestAnt = null;
        for(Ant ant : environment.getAnts()) {
            if(ant.getTourCost() < min) {
                min = ant.getTourCost();
                bestAnt = ant;
            }
            if(ant.getTourCost() > max) {
                max = ant.getTourCost();
            }
            total += ant.getTourCost();
        }
        boolean foundBest = false;
        if(min < bestSoFar) {
            bestSoFar = min;
            bestTourSoFar = bestAnt.getTour().clone();
            foundBest = true;
        }
        if(foundBest || phase % 1000 == 0) {
            double diversity = calculateDiversity();
            String stats = String.format("%s -> Min(%.1f) Phase(%d) Max(%.1f) Mean(%.1f) Div(%.2f)\n",
                    tspFile, min, phase, max, (total / environment.getAntPopSize()), diversity);
            String message = "[" + bestTourSoFar[0];
            for(int i = 1; i < bestTourSoFar.length - 1; i++) {
                message += "->" + bestTourSoFar[i];
            }
            message += "]";
            System.out.print(stats);
            if(foundBest && false) {
                visualizer.setStat(stats);
                visualizer.draw(bestTourSoFar);
                try {
                    Thread.sleep(1000);
                } catch (Exception ex) {
                }
            }
        }
    }

    /**
     * Calculate diversity between ants
     *
     * @return diversity
     */
    public double calculateDiversity() {
        double diversity = 0.0;
        for(int i = 0; i < environment.getAntPopSize(); i++) {
            for(int j = 0; j < environment.getAntPopSize(); j++) {
                if(i != j) {
                    diversity += distanceBetween(environment.getAnts()[i], environment.getAnts()[j]);
                }
            }
        }
        return (1.0 / (environment.getAntPopSize() * (environment.getAntPopSize() - 1.0))) * diversity;
    }

    /**
     * Calculate distance between two ants
     * @param a1
     * @param a2
     * @return distance
     */
    public double distanceBetween(Ant a1, Ant a2) {
        double distance = 0.0;
        int pos2[] = new int[a2.getTour().length];
        for(int i = 0; i < pos2.length; i++) {
            pos2[a2.getTour()[i]] = i;
        }
        for(int i = 0; i < pos2.length - 1; i++) {
            int j = a1.getTour()[i];
            int h = a1.getTour()[i + 1];
            if(pos2[j] + 1 < pos2.length && a2.getTour()[pos2[j] + 1] == h) {
                distance++;
            }
        }
        return 1.0 - (distance / pos2.length);
    }

    /**
     * End visualization
     */
    public void close() {
        //this.visualizer.dispose();
    }

    /**
     * Reset best so far
     */
    public void reset() {
        this.bestSoFar = Double.MAX_VALUE;
        this.bestTourSoFar = null;
    }
}
