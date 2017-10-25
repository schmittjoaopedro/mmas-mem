package simulator;

import simulator.graph.Graph;
import simulator.graph.Node;
import simulator.mmas.RouteSolver;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Simulator extends Thread {

    private Graph graph;

    private Node currentNode;

    private Node nextNode;

    private Set<Node> notVisitedPoints;

    private Set<Node> visitedPoints;

    private Long endTime;

    private RouteSolver routeSolver;

    public Simulator(Graph graph, Node sourceNode, List<Node> targetNodes, RouteSolver routeSolver) {
        this.notVisitedPoints = new HashSet<>();
        this.notVisitedPoints.addAll(targetNodes);
        this.notVisitedPoints.remove(sourceNode);
        this.visitedPoints = new HashSet<>();
        currentNode = sourceNode;
        this.graph = graph;
        this.routeSolver = routeSolver;
    }

    @Override
    public void run() {
        long timePass = System.currentTimeMillis() + 5000;
        visitedPoints.add(currentNode);
        while (!notVisitedPoints.isEmpty()) {
            if(System.currentTimeMillis() > timePass) {
                if(nextNode != null) {
                    routeSolver.addVisited(nextNode);
                    currentNode = nextNode;
                    visitedPoints.add(currentNode);
                    nextNode = null;
                }
                if(nextNode == null) {
                    for(Node node : routeSolver.getResultRoute()) {
                        if(!visitedPoints.contains(node)) {
                            nextNode = node;
                            notVisitedPoints.remove(node);
                            break;
                        }
                    }
                }
                timePass = System.currentTimeMillis() + routeSolver.getRoute(currentNode, nextNode).getBestCost().longValue();
                System.out.println("Going to: " + nextNode.getId());
            }
            try {
                Thread.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        routeSolver.finish();
    }

}
