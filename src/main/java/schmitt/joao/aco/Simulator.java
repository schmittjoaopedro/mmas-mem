package schmitt.joao.aco;

import java.util.Random;

public class Simulator {

    private double magnitude = 0.75;

    private double frequency = 100;

    private double[][] original;

    private Environment environment;

    private Random random = new Random(1);

    public Simulator(Environment environment) {
        this.environment = environment;
        original = new double[environment.getNodesSize()][environment.getNodesSize()];
        for (int i = 0; i < environment.getNodesSize(); i++) {
            for (int j = 0; j < environment.getNodesSize(); j++) {
                original[i][j] = environment.getCost(i, j);
            }
        }
    }

    public boolean simulateScenario(int phase) {
        if(phase > 1 && phase % frequency == 0) {
            for (int i = 0; i < environment.getNodesSize(); i++) {
                for (int j = i; j < environment.getNodesSize(); j++) {
                    if(i == j) continue;
                    double current = original[i][j];
                    double rand = random.nextDouble();
                    double mag = -(magnitude / 2.0) + (rand * magnitude);
                    environment.setCost(i, j, current + mag * current);
                    environment.setCost(j, i, current + mag * current);
                }
            }
            return true;
        } else {
            return false;
        }
    }

}
