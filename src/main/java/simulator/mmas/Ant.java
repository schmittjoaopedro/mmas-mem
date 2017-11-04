package simulator.mmas;

import simulator.graph.Node;

import java.util.*;

public class Ant {

    private Globals _globals;

    private Stack<Node> tour;

    private Set<Node> visited;

    public static Stack<Node> fixed = new Stack<>();

    private double cost;

    private static Random random = new Random();

    public Ant(Globals globals) {
        tour = new Stack<>();
        visited = new HashSet<>();
        _globals = globals;
        cost = Double.MAX_VALUE;
    }

    public static synchronized Stack<Node> getFixed(Node node, Collection<Node> toCopy) {
        if(node != null) {
            Ant.fixed.add(node);
        }
        if(toCopy != null) {
            toCopy.addAll(Ant.fixed);
        }
        return Ant.fixed;
    }

    public void nnTour() {
        tour = new Stack<>();
        visited = new HashSet<>();
        Ant.getFixed(null, tour);
        Ant.getFixed(null, visited);
        Node currentNode = _globals.sourceNode;
        while(tour.size() != _globals.targetNodes.size()) {
            Node nextNode = selectNextNearNode(currentNode);
            if(nextNode == null) {
                cost = Double.MAX_VALUE;
                break;
            } else {
                tour.push(nextNode);
                visited.add(nextNode);
                currentNode = nextNode;
            }
        }
        tour.push(_globals.sourceNode);
        computeCost();
    }

    private Node selectNextNearNode(Node currentNode) {
        Route[] routes = _globals.routeManager.getRoutes(currentNode.getId()).toArray(new Route[] {});
        Route selectedRoute = null;
        for(int i = 0; i < routes.length; i++) {
            if(!visited.contains(routes[i].getTargetNode()) &&
                    (selectedRoute == null || routes[i].getBestCost() < selectedRoute.getBestCost())) {
               selectedRoute = routes[i];
            }
        }
        return selectedRoute.getTargetNode();
    }

    public void heuristicTour() {
        tour = new Stack<>();
        visited = new HashSet<>();
        Ant.getFixed(null, tour);
        Ant.getFixed(null, visited);
        Node currentNode = tour.lastElement();
        while(tour.size() != _globals.targetNodes.size()) {
            Node nextNode = selectNextHeuristicNode(currentNode);
            if(nextNode == null) {
                cost = Double.MAX_VALUE;
                return;
            } else {
                tour.push(nextNode);
                visited.add(nextNode);
                currentNode = nextNode;
            }
        }
        tour.push(_globals.sourceNode);
        computeCost();
    }

    private Node selectNextHeuristicNode(Node currentNode) {
        //Route[] routes = new Route[_globals.nnListSize];
        //for(int i = 0; i < _globals.nnListSize; i++) {
        //    routes[i] = _globals.routeManager.getRoute(currentNode.getId(), _globals.nnList.get(currentNode).get(i).getId());
        //}
        Route[] routes = _globals.routeManager.getRoutes(currentNode.getId()).toArray(new Route[] {});
        Double[] probabilities = new Double[routes.length];
        Double cumulativeSum = 0.0;
        for(int i = 0; i < routes.length; i++) {
            if(visited.contains(routes[i].getTargetNode())) {
                probabilities[i] = 0.0;
            } else {
                probabilities[i] = routes[i].getTotal();
                cumulativeSum += probabilities[i];
            }
        }
        if(cumulativeSum <= 0.0) {
            //return selectNextNearNode(currentNode);
            return null;
        } else {
            double rand = random.nextDouble() * cumulativeSum;
            int i = 0;
            double partialSum = probabilities[i];
            while (partialSum <= rand) {
                i++;
                partialSum += probabilities[i];
            }
            if(i == routes.length) {
                return null;
            } else {
                return routes[i].getTargetNode();
            }
        }
    }

    public void randomWalk() {
        tour = new Stack<>();
        tour.push(_globals.sourceNode);
        visited.add(_globals.sourceNode);
        for(Node node : _globals.targetNodes) {
            if(!tour.contains(node)) {
                tour.push(node);
                visited.add(node);
            }
        }
        tour.push(_globals.sourceNode);
        for(int i = 1; i < tour.size() - 1; i++) {
            int pos = (int) (random.nextDouble() * tour.size());
            pos = Math.max(1, pos);
            pos = Math.min(tour.size() - 2, pos);
            Node aux = tour.get(pos);
            tour.set(pos, tour.get(i));
            tour.set(i, aux);
        }
        computeCost();
    }

    public void computeCost() {
        cost = 0.0;
        if(tour.size() < _globals.targetNodes.size() - 1) {
            cost = Double.MAX_VALUE;
        } else {
            for (int i = 0; i < tour.size() - 1; i++) {
                cost += _globals.routeManager.getRoute(tour.get(i).getId(), tour.get(i + 1).getId()).getBestCost();
            }
        }
    }

    public Stack<Node> getTour() {
        return tour;
    }

    public Set<Node> getVisited() {
        return visited;
    }

    public double getCost() {
        return cost;
    }

    public void setTour(Stack<Node> tour) {
        this.tour = tour;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public Ant clone() {
        Ant ant = new Ant(_globals);
        ant.setCost(this.getCost());
        for(int i = 0; i < getTour().size(); i++) {
            ant.getTour().push(getTour().get(i));
        }
        for(Node node : getVisited()) {
            ant.getVisited().add(node);
        }
        return ant;
    }

    @Override
    public String toString() {
        return "Cost = " + cost;
    }

}
