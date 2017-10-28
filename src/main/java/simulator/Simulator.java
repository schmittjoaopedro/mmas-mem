package simulator;

import simulator.graph.Graph;
import simulator.graph.Node;
import simulator.mmas.Route;
import simulator.mmas.RouteSolver;
import simulator.visualizer.Visualizer;

import java.util.*;
import java.util.stream.Collectors;

public class Simulator extends Thread {

    private Graph graph;

    private Node currentNode;

    private Node lastNode;

    private Set<Node> notVisitedPoints;

    private Set<Node> visitedPoints;

    private Visualizer visualizer;

    private RouteSolver routeSolver;

    private Map<String, List<Node>> routesVisited;

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
        this.routesVisited = new HashMap<>();
    }

    @Override
    public void run() {
        long timePass = System.currentTimeMillis() + 10000;
        visitedPoints.add(currentNode);
        notVisitedPoints.remove(currentNode);
        while (!notVisitedPoints.isEmpty()) {
            if(System.currentTimeMillis() > timePass) {
                for(Node node : routeSolver.getResultTour()) {
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
                //System.out.println(" ---> Going to: " + currentNode.getId());
                drawFull();
            }
            try {
                Thread.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        routeSolver.finish();
    }

    private void drawSimple() {
        visualizer.draw(
            routeSolver.getResultTour().stream().map(Node::getId).collect(Collectors.toList()).toArray(new Integer[] {}),
            visitedPoints.stream().map(Node::getId).collect(Collectors.toSet()), new HashSet<>());
    }


    private void drawFull() {
        List<Integer> routeTour = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();
        Stack<Node> tour = routeSolver.getResultTour();
        Set<Integer> traversed = new HashSet<>();
        for(int i = 1; i < tour.size(); i++) {
            Route route = routeSolver.getRoute(tour.get(i - 1), tour.get(i));
            if(visitedPoints.contains(tour.get(i)) && visitedPoints.contains(tour.get(i - 1))) {
                String key = tour.get(i - 1).getId() + "->" + tour.get(i).getId();
                if(routesVisited.containsKey(key)) {
                    for (Node path : routesVisited.get(key)) {
                        visited.add(path.getId());
                        routeTour.add(path.getId());
                        traversed.add(path.getId());
                    }
                } else {
                    routesVisited.put(key, new ArrayList<>());
                    for (Node path : route.getBestRoute()) {
                        routeTour.add(path.getId());
                        visited.add(path.getId());
                        routesVisited.get(key).add(path);
                    }
                }
            } else {
                for (Node path : route.getBestRoute()) {
                    routeTour.add(path.getId());
                }
            }
        }
        visualizer.draw(routeTour.toArray(new Integer[] {}), visited, traversed);
    }

}
