package simulator.utils;

import simulator.aco.Route;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class DynamicRouteGenerator {

    private double magnitude;

    private long frequency;

    private double upperBound;

    private double lowerBound;

    private Set<Route> routes;

    long nextTime;

    private boolean cycle = false;

    private int periodLimit;

    private int period;

    private Random random = new Random();

    private Map<Integer, Map<Route, Double>> cycles;

    private Map<Route, Double> originalCost;

    public DynamicRouteGenerator(double magnitude, long frequency, double lowerBound, double upperBound, Set<Route> routes) {
        this.magnitude = magnitude;
        this.frequency = frequency;
        this.upperBound = upperBound;
        this.lowerBound = lowerBound;
        this.routes = routes;
        this.originalCost = new HashMap<>();
        for(Route route : routes) {
            originalCost.put(route, route.getBestCost());
        }
    }

    public void loop(int t) {
        if(t > nextTime) {
            if(cycle) {
                for(Map.Entry<Route, Double> routeCost : cycles.get(period).entrySet()) {
                    routeCost.getKey().setBestCost(routeCost.getValue());
                }
                period++;
                if(period >= periodLimit) {
                    period = 0;
                }
            } else {
                for (Route route : routes) {
                    if (random.nextDouble() < magnitude) {
                        double prop = lowerBound + (random.nextDouble() * (upperBound - lowerBound));
                        route.setBestCost(originalCost.get(route) * prop);
                    } else {
                        route.setBestCost(originalCost.get(route));
                    }
                }
            }
            nextTime = t + frequency;
        }
    }

    public void setCycle(boolean cycle, int periodLimit) {
        this.cycle = cycle;
        this.cycles = new HashMap<>();
        this.period = 0;
        this.periodLimit = periodLimit;
        for(int i = 0; i < periodLimit; i++) {
            cycles.put(i, new HashMap<>());
            for(Route route : routes) {
                if(random.nextDouble() < magnitude) {
                    double prop = lowerBound + (random.nextDouble() * (upperBound - lowerBound));
                    cycles.get(i).put(route, originalCost.get(route) * prop);
                } else {
                    cycles.get(i).put(route, originalCost.get(route));
                }
            }
        }
    }

}
