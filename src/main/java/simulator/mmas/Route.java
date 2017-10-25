package simulator.mmas;

import simulator.graph.Graph;
import simulator.graph.Node;

import java.util.List;

public class Route extends Thread {

    private Graph graph;

    private Node sourceNode;

    private Node targetNode;

    private List<Node> bestRoute;

    private Double bestCost;

    private double pheromone;

    private double total;

    public Route(Graph graph, Node sourceNode, Node targetNode) {
        this.graph = graph;
        this.sourceNode = sourceNode;
        this.targetNode = targetNode;
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

    public Node getSourceNode() {
        return sourceNode;
    }

    public void setSourceNode(Node sourceNode) {
        this.sourceNode = sourceNode;
    }

    public Node getTargetNode() {
        return targetNode;
    }

    public void setTargetNode(Node targetNode) {
        this.targetNode = targetNode;
    }

    public void calculateCost() {
        this.setBestRoute(graph.getBestRoute(sourceNode, targetNode));
        this.setBestCost(graph.getTravelTime(this.getBestRoute()));
    }
}
