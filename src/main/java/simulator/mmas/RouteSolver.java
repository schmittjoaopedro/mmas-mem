package simulator.mmas;

import simulator.graph.Graph;
import simulator.graph.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public class RouteSolver extends Thread {

    private Globals _globals;

    private Statistics statistics;

    private boolean execute = true;

    public RouteSolver(Graph graph, Node sourceNode, List<Node> targetNodes) {
        _globals = new Globals();
        _globals.graph = graph;
        _globals.sourceNode = sourceNode;
        _globals.targetNodes = targetNodes;
        Ant.fixed.add(sourceNode);
        if(!targetNodes.contains(sourceNode)) {
            throw new RuntimeException("TargetNodes must contains SourceNode");
        }
        for(int i = 0; i < _globals.targetNodes.size(); i++) {
            for(int j = i + 1; j < _globals.targetNodes.size(); j++) {
                if(_globals.targetNodes.get(i).getId() != _globals.targetNodes.get(j).getId()) {
                    _globals.routeManager.addRoute(_globals.targetNodes.get(i).getId(), _globals.targetNodes.get(j).getId());
                    _globals.routeManager.addRoute(_globals.targetNodes.get(j).getId(), _globals.targetNodes.get(i).getId());
                }
            }
        }
        _globals.routeManager.updateRoutes();
        statistics = new Statistics(_globals);
    }

    @Override
    public void run() {
        allocateAnts();
        restartMatrices();
        initTry();
        while(execute) {
            constructSolutions();
            updateStatistics();
            pheromoneTrailUpdate();
            searchControl();
            _globals.iteration++;
        }
    }

    private void allocateAnts() {
        _globals.ants = new Ant[_globals.numberAnts];
        for(int i = 0; i < _globals.numberAnts; i++) {
            _globals.ants[i] = new Ant(_globals);
        }
        _globals.bestSoFar = new Ant(_globals);
        _globals.restartBestAnt = new Ant(_globals);
    }

    private void restartMatrices() {
        for(Route route : _globals.routeManager.getRoutes()) {
            route.setPheromone(0.0);
            route.setTotal(0.0);
        }
    }

    private void initTry() {
        _globals.iteration = 0;
        _globals.restartFoundBestIteration = 0;
        _globals.foundBestIteration = 0;
        _globals.ants[0].nnTour();
        _globals.bestSoFar = _globals.ants[0].clone();
        _globals.trailMax = 1.0 / (_globals.rho * _globals.ants[0].getCost());
        _globals.trailMin = _globals.trailMax / (2.0 * _globals.graph.getNodes().size());
        initPheromoneTrails(_globals.trailMax);
        computeTotalInformation();
    }

    private void initPheromoneTrails(double trail) {
        for(Route route : _globals.routeManager.getRoutes()) {
            route.setPheromone(trail);
            route.setTotal(trail);
        }
    }

    private void computeTotalInformation() {
        for(Route route : _globals.routeManager.getRoutes()) {
            double value = Math.pow(route.getPheromone(), _globals.alpha) * Math.pow(_globals.HEURISTIC(route), _globals.beta);
            route.setTotal(value);
        }
    }

    private void constructSolutions() {
        for(Ant ant : _globals.ants) {
            ant.heuristicTour();
        }
    }

    public void updateStatistics() {
        Ant iterationBestAnt = findBestAnt();
        if(iterationBestAnt.getCost() < _globals.bestSoFar.getCost()) {
            _globals.bestSoFar = iterationBestAnt.clone();
            _globals.restartBestAnt = iterationBestAnt.clone();
            _globals.foundBestIteration = _globals.iteration;
            _globals.restartFoundBestIteration = _globals.iteration;
            _globals.trailMax = 1.0 / (_globals.rho * _globals.bestSoFar.getCost());
            _globals.trailMin = _globals.trailMax / (2.0 * _globals.graph.getNodes().size());
            String message = String.format("Best tour found %05d, at iteration %05d",
                    (int) _globals.bestSoFar.getCost(),
                    _globals.iteration);
            message += "\t\t[" + _globals.bestSoFar.getTour().get(0).getId();
            for(int i = 1; i < _globals.bestSoFar.getTour().size(); i++) {
                message += "->" + _globals.bestSoFar.getTour().get(i).getId();
            }
            message += "]";
            System.out.println(message);
        }
        if(iterationBestAnt.getCost() < _globals.restartBestAnt.getCost()) {
            _globals.restartBestAnt = iterationBestAnt.clone();
            _globals.restartFoundBestIteration = _globals.iteration;
        }
    }

    public Ant findBestAnt() {
        Ant bestAnt = _globals.ants[0];
        for(int i = 1; i < _globals.numberAnts; i++) {
            if(_globals.ants[i].getCost() < bestAnt.getCost()) {
                bestAnt = _globals.ants[i];
            }
        }
        return bestAnt;
    }

    private void pheromoneTrailUpdate() {
        pheromoneEvaporation();
        if(_globals.iteration % _globals.uGb == 0) {
            pheromoneUpdate(findBestAnt());
        } else if (_globals.uGb == 1 && (_globals.iteration - _globals.restartFoundBestIteration) > 50) {
            pheromoneUpdate(_globals.bestSoFar);
        } else {
            pheromoneUpdate(_globals.restartBestAnt);
        }
        checkPheromoneTrails();
        computeTotalInformation();
    }

    private void pheromoneEvaporation() {
        for(Route route : _globals.routeManager.getRoutes()) {
            route.setPheromone((1.0 - _globals.rho) * route.getPheromone());
        }
    }

    private void pheromoneUpdate(Ant ant) {
        double dTau = 1.0 / ant.getCost();
        for(int i = 0; i < ant.getTour().size() - 1; i++) {
            int fromId = ant.getTour().get(i).getId();
            int toId = ant.getTour().get(i + 1).getId();
            _globals.routeManager.getRoute(fromId, toId).setPheromone(dTau);
        }
    }

    private void checkPheromoneTrails() {
        for(Route route : _globals.routeManager.getRoutes()) {
            if(route.getPheromone() < _globals.trailMin) {
                route.setPheromone(_globals.trailMin);
            }
            if(route.getPheromone() > _globals.trailMax) {
                route.setPheromone(_globals.trailMax);
            }
        }
    }

    private void searchControl() {
        if(_globals.iteration % 100 == 0) {
            double branchFactor = calculateBranchingFactor();
            //System.out.println("Branch factor = " + branchFactor + " at iteration " + _globals.iteration);
            if(branchFactor < _globals.branchFactor && (_globals.iteration - _globals.restartFoundBestIteration) > 250) {
                //System.out.println("Restarting System!");
                _globals.restartBestAnt = new Ant(_globals);
                initPheromoneTrails(_globals.trailMax);
                computeTotalInformation();
                _globals.restartFoundBestIteration = _globals.iteration;
            }
        }
    }

    private double calculateBranchingFactor() {
        double min, max, cutoff, avg = 0.0;
        List<Double> numBranches = new ArrayList<>();
        for(Node node : _globals.targetNodes) {
            Set<Route> routes = _globals.routeManager.getRoutes(node.getId());
            if(routes != null && !routes.isEmpty()) {
                max = Double.MAX_VALUE * -1.0;
                min = Double.MAX_VALUE;
                for(Route route : routes) {
                    if(route.getPheromone() > max) {
                        max = route.getPheromone();
                    }
                    if(route.getPheromone() < min) {
                        min = route.getPheromone();
                    }
                }
                cutoff = min + _globals.lambda * (max - min);
                double count = 0.0;
                for(Route route : routes) {
                    if(route.getPheromone() >= cutoff) {
                        count += 1.0;
                    }
                }
                numBranches.add(count);
            }
        }
        for(Double branch : numBranches) {
            avg += branch;
        }
        return (avg / (_globals.targetNodes.size() * 2.0));
    }

    public Stack<Node> getResultRoute() {
        return _globals.bestSoFar.getTour();
    }

    public Route getRoute(Node source, Node target) {
        return _globals.routeManager.getRoute(source.getId(), target.getId());
    }

    public double getResultCost() {
        return _globals.bestSoFar.getCost();
    }

    public Ant getBestSoFar() {
        return _globals.bestSoFar;
    }

    public RouteManager getRouteManager() {
        return _globals.routeManager;
    }

    public void finish() {
        execute = false;
    }

    public void addVisited(Node node) {
        Ant.fixed.add(node);
    }
}
