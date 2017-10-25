package simulator.utils;

import simulator.graph.Edge;
import simulator.graph.Graph;

import java.util.Random;

public class DynamicGenerator extends Thread {

    private double magnitude;

    private long frequency;

    private Graph graph;

    private Random random = new Random(3);

    private DynamicListener dynamicListener;

    public DynamicGenerator(Graph graph, double magnitude, long frequency) {
        this.graph = graph;
        this.magnitude = magnitude;
        this.frequency = frequency;
    }

    @Override
    public void run() {
        long nextTime = System.currentTimeMillis() + frequency;
        while(true) {
            if(System.currentTimeMillis() > nextTime) {
                for(Edge edge : graph.getEdges()) {
                    double prop = (-1 + (2 * random.nextDouble())) * magnitude;
                    edge.setSpeed(edge.getOriginalSpeed() + edge.getOriginalSpeed() * prop);
                }
                nextTime = System.currentTimeMillis() + frequency;
                if(this.getDynamicListener() != null) {
                    this.getDynamicListener().updatedWeights();
                }
            }
            try { Thread.sleep(100); } catch (Exception e) { e.printStackTrace(); }
        }
    }

    public DynamicListener getDynamicListener() {
        return dynamicListener;
    }

    public void setDynamicListener(DynamicListener dynamicListener) {
        this.dynamicListener = dynamicListener;
    }
}
