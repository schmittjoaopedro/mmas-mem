package simulator.utils;

import simulator.graph.Edge;
import simulator.graph.Graph;

import java.util.Random;

public class DynamicGenerator extends Thread {

    private double magnitude;

    private long frequency;

    private double upperBound;

    private double lowerBound;

    private Graph graph;

    private Random random = new Random(1);

    private DynamicListener dynamicListener;

    long nextTime;

    public DynamicGenerator(Graph graph, double magnitude, long frequency, double lowerBound, double upperBound) {
        this.graph = graph;
        this.magnitude = magnitude;
        this.frequency = frequency;
        this.upperBound = upperBound;
        this.lowerBound = lowerBound;
    }

    @Override
    public void run() {
        nextTime = System.currentTimeMillis();
        while(true) {
            if(System.currentTimeMillis() > nextTime) {
                for(Edge edge : graph.getEdges()) {
                    if(random.nextDouble() < magnitude) {
                        double prop = lowerBound + (random.nextDouble() * (upperBound - lowerBound));
                        edge.setSpeed(prop);
                    } else {
                        edge.setSpeed(edge.getOriginalSpeed());
                    }
                }
                nextTime = System.currentTimeMillis() + frequency;
                if(this.getDynamicListener() != null) {
                    this.getDynamicListener().updatedWeights();
                }
            }
            try { Thread.sleep(100); } catch (Exception e) { e.printStackTrace(); }
        }
    }

    public void loop(int t) {
        if(t > nextTime) {
            for(Edge edge : graph.getEdges()) {
                if(random.nextDouble() < magnitude) {
                    double prop = lowerBound + (random.nextDouble() * (upperBound - lowerBound));
                    edge.setSpeed(prop);
                } else {
                    edge.setSpeed(edge.getOriginalSpeed());
                }
            }
            nextTime = t + frequency;
            if(this.getDynamicListener() != null) {
                this.getDynamicListener().updatedWeights();
            }
        }
    }

    public DynamicListener getDynamicListener() {
        return dynamicListener;
    }

    public void setDynamicListener(DynamicListener dynamicListener) {
        this.dynamicListener = dynamicListener;
    }
}
