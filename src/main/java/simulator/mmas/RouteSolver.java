package simulator.mmas;

import simulator.graph.Graph;
import simulator.graph.Node;
import simulator.utils.DynamicListener;
import simulator.utils.GenericStatistics;
import simulator.utils.Utils;

import java.util.*;

public class RouteSolver extends Thread implements DynamicListener {

    private Globals _globals;

    private Statistics statistics;

    private boolean execute = true;

    private Memory memory;

    private boolean useMemory = true;

    public RouteSolver(Graph graph, Node sourceNode, List<Node> targetNodes, int trial, GenericStatistics genericStatistics, boolean useMemory) {
        this.useMemory = useMemory;
        _globals = new Globals();
        _globals.graph = graph;
        _globals.sourceNode = sourceNode;
        _globals.targetNodes = targetNodes;
        Ant.fixed.clear();
        Ant.getFixed(sourceNode, null);
        if (!targetNodes.contains(sourceNode)) {
            throw new RuntimeException("TargetNodes must contains SourceNode");
        }
        for (int i = 0; i < _globals.targetNodes.size(); i++) {
            for (int j = i + 1; j < _globals.targetNodes.size(); j++) {
                if (_globals.targetNodes.get(i).getId() != _globals.targetNodes.get(j).getId()) {
                    _globals.routeManager.addRoute(_globals.targetNodes.get(i).getId(), _globals.targetNodes.get(j).getId());
                    _globals.routeManager.addRoute(_globals.targetNodes.get(j).getId(), _globals.targetNodes.get(i).getId());
                }
            }
        }
        _globals.routeManager.updateRoutes(false, 0);
        statistics = new Statistics(_globals, this);
        statistics.setTrial(trial);
        statistics.setGenericStatistics(genericStatistics);
        if(useMemory)
            memory = new Memory(_globals);
    }

    @Override
    public void run() {
        allocateAnts();
        restartMatrices();
        initTry();
        while (execute) {
            //computeNNList();
            constructSolutions();
            updateStatistics();
            pheromoneTrailUpdate();
            searchControl();
            statistics.calculateStatistics();
            repairSolutions();
            _globals.iteration++;
        }
    }

    public void setup() {
        allocateAnts();
        restartMatrices();
        initTry();
    }

    public void loop(int t) {
        constructSolutions();
        updateStatistics();
        pheromoneTrailUpdate();
        searchControl();
        statistics.calculateStatistics();
        repairSolutions();
        _globals.iteration = t;
    }

    private void allocateAnts() {
        _globals.ants = new Ant[_globals.numberAnts];
        for (int i = 0; i < _globals.numberAnts; i++) {
            _globals.ants[i] = new Ant(_globals);
        }
        _globals.bestSoFar = new Ant(_globals);
        _globals.restartBestAnt = new Ant(_globals);
    }

    private void restartMatrices() {
        for (Route route : _globals.routeManager.getRoutes()) {
            route.setPheromone(0.0);
            route.setTotal(0.0);
        }
        if (_globals.nnListSize > _globals.targetNodes.size()) {
            _globals.nnListSize = _globals.targetNodes.size();
        }
        _globals.nnList = new HashMap<>();
    }

    private void initTry() {
        _globals.iteration = 1;
        _globals.restartFoundBestIteration = 1;
        _globals.foundBestIteration = 1;
        _globals.ants[0].nnTour();
        _globals.bestSoFar = new Ant(_globals);
        _globals.trailMax = 1.0 / (_globals.rho * _globals.ants[0].getCost());
        _globals.trailMin = _globals.trailMax / (2.0 * _globals.targetNodes.size());
        initPheromoneTrails(_globals.trailMax);
        computeTotalInformation();
        if(useMemory)
            memory.initMemoryRandomly();
    }

    private void initPheromoneTrails(double trail) {
        for (Route route : _globals.routeManager.getRoutes()) {
            route.setPheromone(trail);
            route.setTotal(trail);
        }
    }

    private void computeTotalInformation() {
        for (Route route : _globals.routeManager.getRoutes()) {
            double value = Math.pow(route.getPheromone(), _globals.alpha) * Math.pow(_globals.HEURISTIC(route), _globals.beta);
            route.setTotal(value);
        }
    }

    private void constructSolutions() {
        for (Ant ant : _globals.ants) {
            ant.heuristicTour();
        }
    }

    public void updateStatistics() {
        Ant iterationBestAnt = findBestAnt();
        if (iterationBestAnt.getCost() < _globals.bestSoFar.getCost()) {
            _globals.bestSoFar = iterationBestAnt.clone();
            _globals.restartBestAnt = iterationBestAnt.clone();
            _globals.foundBestIteration = _globals.iteration;
            _globals.restartFoundBestIteration = _globals.iteration;
            _globals.trailMax = 1.0 / (_globals.rho * _globals.bestSoFar.getCost());
            _globals.trailMin = _globals.trailMax / (2.0 * _globals.targetNodes.size());
            printBestSoFar();
        }
        if (iterationBestAnt.getCost() < _globals.restartBestAnt.getCost()) {
            _globals.restartBestAnt = iterationBestAnt.clone();
            _globals.restartFoundBestIteration = _globals.iteration;
        }
    }

    public void computeNNList() {
        _globals.nnList = new HashMap<>();
        for (int i = 0; i < _globals.targetNodes.size(); i++) {
            Map<Integer, Double> positions = new HashMap<>();
            for (int j = 0; j < _globals.targetNodes.size() - 1; j++) {
                if (_globals.targetNodes.get(i) != _globals.targetNodes.get(j)) {
                    int id = _globals.targetNodes.get(j).getId();
                    positions.put(id, _globals.routeManager.getRoute(_globals.targetNodes.get(i).getId(), id).getBestCost());
                }
            }
            LinkedHashMap<Integer, Double> sortedPositions = Utils.sortHashMapByValues(positions);
            Integer[] keys = sortedPositions.keySet().toArray(new Integer[] {});
            _globals.nnList.put(_globals.targetNodes.get(i), new ArrayList<>());
            for (int r = 0; r < _globals.nnListSize; r++) {
                _globals.nnList.get(_globals.targetNodes.get(i)).add(_globals.graph.getNode(keys[r]));
            }
        }
    }

    public void printBestSoFar() {
//        String message = String.format("BestSoFar %05d adjusted BestSoFar %05d, at iteration %05d, diversity %.2f and branch factor %.2f",
//                (int) _globals.bestSoFar.getCost(), (int) statistics.getCost(), _globals.iteration, statistics.getDiversity(), calculateBranchingFactor());
//        message += "\t\t[" + _globals.bestSoFar.getTour().get(0).getId();
//        for (int i = 1; i < _globals.bestSoFar.getTour().size(); i++) {
//            message += "->" + _globals.bestSoFar.getTour().get(i).getId();
//        }
//        message += "]";
//        System.out.println(message);
//        statistics.printStatistics();
    }

    public Ant findBestAnt() {
        Ant bestAnt = _globals.ants[0];
        for (int i = 1; i < _globals.numberAnts; i++) {
            if (_globals.ants[i].getCost() < bestAnt.getCost()) {
                bestAnt = _globals.ants[i];
            }
        }
        return bestAnt;
    }

    private void pheromoneTrailUpdate() {
        pheromoneEvaporation();
        if (_globals.iteration % _globals.uGb == 0) {
            pheromoneUpdate(findBestAnt());
        } else if (_globals.uGb == 1 && (_globals.iteration - _globals.restartFoundBestIteration) > 50) {
            pheromoneUpdate(_globals.bestSoFar);
        } else {
            if(useMemory) {
                memory.updateLongTermMemory();
                memory.updateShortTermMemory();
                for(Ant ant : memory.shortMemory) {
                    pheromoneUpdate(ant);
                }
            } else {
                pheromoneUpdate(_globals.restartBestAnt);
            }
        }
        checkPheromoneTrails();
        computeTotalInformation();
    }

    private void pheromoneEvaporation() {
        for (Route route : _globals.routeManager.getRoutes()) {
            route.setPheromone((1.0 - _globals.rho) * route.getPheromone());
        }
    }

    private void pheromoneUpdate(Ant ant) {
        double dTau = 1.0 / ant.getCost();
        for (int i = 0; i < ant.getTour().size() - 1; i++) {
            int fromId = ant.getTour().get(i).getId();
            int toId = ant.getTour().get(i + 1).getId();
            Route route = _globals.routeManager.getRoute(fromId, toId);
            route.setPheromone(route.getPheromone() + dTau);
        }
    }

    private void checkPheromoneTrails() {
        for (Route route : _globals.routeManager.getRoutes()) {
            if (route.getPheromone() < _globals.trailMin) {
                route.setPheromone(_globals.trailMin);
            }
            if (route.getPheromone() > _globals.trailMax) {
                route.setPheromone(_globals.trailMax);
            }
        }
    }

    private void searchControl() {
        _globals.branchFactorValue = calculateBranchingFactor();
        if (_globals.iteration % 100 == 0) {
            //System.out.println("Branch factor = " + branchFactor + " at iteration " + _globals.iteration);
            if (_globals.branchFactorValue < _globals.branchFactor && (_globals.iteration - _globals.restartFoundBestIteration) > 250) {
                //System.out.println(" ================== Restarting System! ================");
                _globals.restartBestAnt = new Ant(_globals);
                _globals.restartBestAnt.randomWalk();
                initPheromoneTrails(_globals.trailMax);
                computeTotalInformation();
                _globals.restartFoundBestIteration = _globals.iteration;
            }
        }
    }

    public double calculateBranchingFactor() {
        double min, max, cutoff, avg = 0.0;
        List<Double> numBranches = new ArrayList<>();
        for (Node node : _globals.targetNodes) {
            if (Ant.fixed.contains(node)) continue;
            Set<Route> routes = _globals.routeManager.getRoutes(node.getId());
            if (routes != null && !routes.isEmpty()) {
                max = Double.MAX_VALUE * -1.0;
                min = Double.MAX_VALUE;
                for (Route route : routes) {
                    if (route.getPheromone() > max) {
                        max = route.getPheromone();
                    }
                    if (route.getPheromone() < min) {
                        min = route.getPheromone();
                    }
                }
                cutoff = min + _globals.lambda * (max - min);
                double count = 0.0;
                for (Route route : routes) {
                    if (route.getPheromone() >= cutoff) {
                        count += 1.0;
                    }
                }
                numBranches.add(count);
            }
        }
        for (Double branch : numBranches) {
            avg += branch;
        }
        if(_globals.targetNodes.size() - Ant.fixed.size() == 0) return 0.0;
        return (avg / ((_globals.targetNodes.size() - Ant.fixed.size()) * 2.0));
    }

    public void repairSolutions() {
        double cost = _globals.bestSoFar.getCost();
        _globals.bestSoFar.computeCost();
        if (cost != _globals.bestSoFar.getCost()) {
            printBestSoFar();
        }
        _globals.restartBestAnt.computeCost();
    }

    public Stack<Node> getResultTour() {
        Stack<Node> tour = new Stack<>();
        for (Node node : _globals.bestSoFar.getTour()) {
            tour.add(node);
        }
        return tour;
    }

    public double getCost() {
        return statistics.getCost();
    }

    public Route getRoute(Node source, Node target) {
        return _globals.routeManager.getRoute(source.getId(), target.getId());
    }

    public void finish() {
        execute = false;
    }

    public void addVisited(Node node) {
        Ant.getFixed(node, null);
    }

    @Override
    public void updatedWeights(boolean cycled, int phase) {
        //System.out.println("Dynamic environment changed, starting updating routes....");
        this._globals.routeManager.updateRoutes(cycled, phase);
        //System.out.println("Dynamic environment changed, ending updating routes....");
    }

    public Integer getIteration() {
        return _globals.iteration;
    }
}
