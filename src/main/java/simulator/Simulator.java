package simulator;

import simulator.graph.Graph;
import simulator.graph.Node;
import simulator.mmas.RouteSolver;
import simulator.visualizer.Visualizer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Simulator extends Thread {

    private Graph graph;

    private Node currentNode;

    private Node lastNode;

    private Set<Node> notVisitedPoints;

    private Set<Node> visitedPoints;

    private Visualizer visualizer;

    private RouteSolver routeSolver;

    public Simulator(Graph graph, Node sourceNode, List<Node> targetNodes, RouteSolver routeSolver) {
        this.notVisitedPoints = new HashSet<>();
        this.notVisitedPoints.addAll(targetNodes);
        this.notVisitedPoints.remove(sourceNode);
        this.visitedPoints = new HashSet<>();
        currentNode = sourceNode;
        this.graph = graph;
        this.routeSolver = routeSolver;
        Set<Integer> ids = targetNodes.stream().map(Node::getId).collect(Collectors.toSet());
        this.visualizer = new Visualizer(graph, ids);
    }

    @Override
    public void run() {
        long timePass = System.currentTimeMillis() + 5000;
        visitedPoints.add(currentNode);
        notVisitedPoints.remove(currentNode);
        while (!notVisitedPoints.isEmpty()) {
            if(System.currentTimeMillis() > timePass) {
                for(Node node : routeSolver.getResultRoute()) {
                    if(!visitedPoints.contains(node)) {
                        routeSolver.addVisited(node);
                        notVisitedPoints.remove(node);
                        visitedPoints.add(node);
                        lastNode = currentNode;
                        currentNode = node;
                        break;
                    }
                }
                timePass = System.currentTimeMillis() + routeSolver.getRoute(lastNode, currentNode).getBestCost().longValue();
                System.out.println("Going to: " + currentNode.getId());
                visualizer.draw(
                        routeSolver.getResultRoute().stream().map(Node::getId).collect(Collectors.toList()).toArray(new Integer[] {}),
                        visitedPoints.stream().map(Node::getId).collect(Collectors.toSet()));
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
