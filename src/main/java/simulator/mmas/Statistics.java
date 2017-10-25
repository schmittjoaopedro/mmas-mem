package simulator.mmas;


import java.util.HashMap;
import java.util.Map;

public class Statistics {

    private Globals _globals;

    private Map<Integer, Double> iterationMean;

    private Map<Integer, Double> iterationBest;

    private Map<Integer, Double> iterationWorst;

    private Map<Integer, Double> iterationBestSoFar;

    public Statistics(Globals globals) {
        this._globals = globals;
        iterationMean = new HashMap<>();
        iterationBest = new HashMap<>();
        iterationWorst = new HashMap<>();
        iterationBestSoFar = new HashMap<>();
    }

    public void calculateStatistics() {
        if(_globals.iteration % 100 == 0 && _globals.bestSoFar != null) {
            double[] costs = new double[_globals.numberAnts];
            for (int a = 0; a < _globals.numberAnts; a++) {
                costs[a] = _globals.ants[a].getCost();
            }
            iterationMean.put(_globals.iteration, mean((costs)));
            iterationBest.put(_globals.iteration, getBest(costs));
            iterationWorst.put(_globals.iteration, getWorst(costs));
            iterationBestSoFar.put(_globals.iteration, _globals.bestSoFar.getCost());
            //Iteration, Mean, Best, Worst, Best so Far
            /*String message = String.format("%05d, %05d, %05d, %05d, %05d,",
                    _globals.iteration,
                    (int) mean(costs),
                    (int) getBest(costs),
                    (int) getWorst(costs),
                    (int) _globals.bestSoFar.getCost());*/
            //System.out.println(message);
            int iter = _globals.iteration;
            int mean = (int) mean(costs);
            int best = (int) getBest(costs);
            int worst = (int) getWorst(costs);
            int bsf = (int) _globals.bestSoFar.getCost();
            double div = getDiversity();
            String message = String.format("(Iter = %08d), (Mean = %08d), (Best = %08d), (Worst = %08d), (Bsf = %08d), (Div = %.2f)",
                    iter, mean, best, worst, bsf, div);
            //System.out.println(message);
        }
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
}
