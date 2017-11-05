package simulator.aco;

import simulator.graph.Graph;
import simulator.graph.Node;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Route extends Thread {

    private Graph graph;

    private Node sourceNode;

    private Node targetNode;

    private List<Node> bestRoute;

    private Double bestCost;

    private double pheromone;

    private double total;

    private Map<Integer, List<Node>> routeMemory;

    private Map<Integer, Double> costMemory;

    public Route(Graph graph, Node sourceNode, Node targetNode) {
        this.graph = graph;
        this.sourceNode = sourceNode;
        this.targetNode = targetNode;
        this.routeMemory = new HashMap<>();
        this.costMemory = new HashMap<>();
    }

    public List<Node> getBestRoute() {
        return bestRoute;
    }

    public void setBestRoute(List<Node> bestRoute) {
        this.bestRoute = bestRoute;
    }

    public Double getBestCost() {
        return bestCost;
    }

    public void setBestCost(double bestCost) {
        this.bestCost = bestCost;
    }

    public double getPheromone() {
        return pheromone;
    }

    public void setPheromone(double pheromone) {
        this.pheromone = pheromone;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public Node getTargetNode() {
        return targetNode;
    }

    public void calculateCost(boolean cycled, int phase) {
        if(cycled) {
            if(!routeMemory.containsKey(phase)) {
                this.setBestRoute(graph.getBestRoute(sourceNode, targetNode));
                this.setBestCost(graph.getTravelTime(this.getBestRoute()));
                this.routeMemory.put(phase, this.getBestRoute());
                this.costMemory.put(phase, this.getBestCost());
            } else {
                this.setBestRoute(this.routeMemory.get(phase));
                this.setBestCost(this.costMemory.get(phase));
            }
        } else {
            this.setBestRoute(graph.getBestRoute(sourceNode, targetNode));
            this.setBestCost(graph.getTravelTime(this.getBestRoute()));
        }

    }
}
