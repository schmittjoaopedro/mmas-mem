package simulator.mmas;

import simulator.graph.Graph;
import simulator.graph.Node;
import simulator.utils.Timer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Globals {

    public Graph graph;

    public Node sourceNode;

    public RouteManager routeManager;

    public List<Node> targetNodes;

    public Map<Node, List<Node>> nnList;

    public int nnListSize;

    public Ant[] ants;

    public Ant bestSoFar;

    public Ant restartBestAnt;

    public int numberAnts;

    public int iteration;

    public int restartFoundBestIteration;

    public int foundBestIteration;

    public double branchFactor;

    public double maxTime; // In seconds

    public double lambda;

    public double trailMax;

    public double trailMin;

    public double alpha;

    public double beta;

    public double rho;

    public int uGb;

    public double branchFactorValue;

    public Globals() {
        routeManager = new RouteManager(this);
        targetNodes = new ArrayList<>();
        numberAnts = 25;
        alpha = 1.0;
        beta = 2.0;
        rho = 0.02;
        maxTime = 10.0;
        branchFactor = 1.0001;
        lambda = 0.05;
        iteration = 0;
        restartFoundBestIteration = 0;
        foundBestIteration = 0;
        uGb = 25;
        nnListSize = 20;
    }

    public double HEURISTIC(Route route) {
        return 1.0 / (route.getBestCost() + 0.00001);
    }

}
