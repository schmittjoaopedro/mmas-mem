package simulator.aco;


import simulator.graph.Node;
import simulator.utils.GenericStatistics;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;

public class Statistics {

    private Globals _globals;

    private Map<Integer, Double> iterationMean;

    private Map<Integer, Double> iterationBest;

    private Map<Integer, Double> iterationWorst;

    private Map<Integer, Double> iterationBestSoFar;

    private RouteSolver routeSolver;

    private int trial;

    private GenericStatistics genericStatistics;

    private static Map<String, Double> costMap = new HashMap<>();

    public Statistics(Globals globals, RouteSolver routeSolver) {
        this._globals = globals;
        iterationMean = new HashMap<>();
        iterationBest = new HashMap<>();
        iterationWorst = new HashMap<>();
        iterationBestSoFar = new HashMap<>();
        this.routeSolver = routeSolver;
    }

    public void printStatistics() {
        double[] costs = new double[_globals.numberAnts];
        for (int a = 0; a < _globals.numberAnts; a++) {
            costs[a] = _globals.ants[a].getCost();
        }
        iterationMean.put(_globals.iteration, mean((costs)));
        iterationBest.put(_globals.iteration, getBest(costs));
        iterationWorst.put(_globals.iteration, getWorst(costs));
        iterationBestSoFar.put(_globals.iteration, _globals.bestSoFar.getCost());
        int iter = _globals.iteration;
        int mean = (int) mean(costs);
        int best = (int) getBest(costs);
        int worst = (int) getWorst(costs);
        int bsf = (int) _globals.bestSoFar.getCost();
        int adjBsf = (int) getCost();
        double div = getDiversity();
        //String message = String.format(" -------> (Iter = %08d), (Mean = %08d), (Best = %08d), (Worst = %08d), (Bsf = %08d), (Div = %.2f)",
        String message = String.format(Locale.US, "%08d,%08d,%08d,%08d,%08d,%08d,%.2f,%.2f",
                iter, mean, best, worst, bsf, adjBsf, div, routeSolver.calculateBranchingFactor());
        System.out.println(message);
    }

    public void calculateStatistics() {
        double[] costs = new double[_globals.numberAnts];
        for (int a = 0; a < _globals.numberAnts; a++) {
            costs[a] = _globals.ants[a].getCost();
        }
        iterationMean.put(_globals.iteration, mean((costs)));
        iterationBest.put(_globals.iteration, getBest(costs));
        iterationWorst.put(_globals.iteration, getWorst(costs));
        iterationBestSoFar.put(_globals.iteration, _globals.bestSoFar.getCost());
        int iter = _globals.iteration;
        int mean = (int) mean(costs);
        int best = (int) getBest(costs);
        int worst = (int) getWorst(costs);
        int bsf = (int) _globals.bestSoFar.getCost();
        int adjBsf = (int) getCost();
        double div = getDiversity();
        if(_globals.iteration % 100 == 0 && _globals.bestSoFar != null) {
            //String message = String.format(" -------> (Iter = %08d), (Mean = %08d), (Best = %08d), (Worst = %08d), (Bsf = %08d), (Div = %.2f)",
            //String message = String.format(Locale.US, "%08d,%08d,%08d,%08d,%08d,%08d,%.2f,%.2f",
            //        iter, mean, best, worst, bsf, adjBsf, div, routeSolver.calculateBranchingFactor());
            //System.out.println(message);
        }
        genericStatistics.addMean(iter, trial, mean);
        genericStatistics.addBest(iter, trial, best);
        genericStatistics.addWorst(iter, trial, worst);
        genericStatistics.addBestSoFar(iter, trial, bsf);
        genericStatistics.addBestSoFarAdj(iter, trial, adjBsf);
        genericStatistics.addDiv(iter, trial, div);
        genericStatistics.addBranch(iter, trial, _globals.branchFactorValue);
    }

    public double getCost() {
        Stack<Node> tour = routeSolver.getResultTour();
        Double cost = 0.0;
        for(int i = 1; i < tour.size(); i++) {
            if(Ant.fixed.contains(tour.get(i - 1)) && Ant.fixed.contains(tour.get(i))) {
                String key = tour.get(i - 1).getId() + "->" + tour.get(i).getId();
                if(!costMap.containsKey(key)) {
                    costMap.put(key, routeSolver.getRoute(tour.get(i - 1), tour.get(i)).getBestCost());
                }
                cost += costMap.get(key);
            } else {
                cost += routeSolver.getRoute(tour.get(i - 1), tour.get(i)).getBestCost();
            }
        }
        return cost;
    }

    public double mean(double[] values) {
        if(values.length == 0) return 0;
        double sum = values[0];
        for(int i = 1; i < values.length; i++) {
            sum += values[i];
        }
        return sum / values.length;
    }

    public double getDiversity() {
        double div = 0.0;
        for (int i = 0; i < _globals.numberAnts; i++) {
            for (int j = 0; j < _globals.numberAnts; j++) {
                if (i != j) {
                    div += distanceBetweenAnts(_globals.ants[i], _globals.ants[j]);
                }
            }
        }
        return (1.0 / (_globals.numberAnts * (_globals.numberAnts - 1.0))) * div;
    }

    public double distanceBetweenAnts(Ant a1, Ant a2) {
        int pos, n = _globals.targetNodes.size();
        double distance = 0.0;
        Map<Integer, Integer> edges = new HashMap<>();
        for(int i = 0; i < a2.getTour().size() - 1; i++) {
            edges.put(a2.getTour().get(i).getId(), a1.getTour().get(i + 1).getId());
        }
        for(int i = 0; i < a1.getTour().size() - 1; i++) {
            int j = a1.getTour().get(i).getId();
            int h = a1.getTour().get(i + 1).getId();
            pos = edges.get(j);
            if(h == pos) {
                distance++;
            }
        }
        return 1.0 - (distance / (double) n);
    }

    public double getBest(double[] values) {
        if(values.length == 0) return 0;
        double min = values[0];
        for(int i = 0; i < values.length; i++) {
            if(values[i] < min) {
                min = values[i];
            }
        }
        return min;
    }

    public double getWorst(double[] values) {
        if(values.length == 0) return 0;
        double max = values[0];
        for(int i = 0; i < values.length; i++) {
            if(values[i] > max) {
                max = values[i];
            }
        }
        return max;
    }

    public Map<Integer, Double> getIterationMean() {
        return iterationMean;
    }

    public void setIterationMean(Map<Integer, Double> iterationMean) {
        this.iterationMean = iterationMean;
    }

    public Map<Integer, Double> getIterationBest() {
        return iterationBest;
    }

    public void setIterationBest(Map<Integer, Double> iterationBest) {
        this.iterationBest = iterationBest;
    }

    public Map<Integer, Double> getIterationWorst() {
        return iterationWorst;
    }

    public void setIterationWorst(Map<Integer, Double> iterationWorst) {
        this.iterationWorst = iterationWorst;
    }

    public Map<Integer, Double> getIterationBestSoFar() {
        return iterationBestSoFar;
    }

    public void setIterationBestSoFar(Map<Integer, Double> iterationBestSoFar) {
        this.iterationBestSoFar = iterationBestSoFar;
    }

    public int getTrial() {
        return trial;
    }

    public void setTrial(int trial) {
        this.trial = trial;
    }

    public GenericStatistics getGenericStatistics() {
        return genericStatistics;
    }

    public void setGenericStatistics(GenericStatistics genericStatistics) {
        this.genericStatistics = genericStatistics;
    }
}