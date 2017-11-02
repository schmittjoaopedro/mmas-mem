package simulator.mmas;

import simulator.graph.Node;
import simulator.utils.Utils;

import java.util.*;

public class Memory {

    public static int shortMemorySize = 6;

    public static int longMemorySize = 10;

    public static double immigrantRate = 0.4;

    public static double pMi = 0.01;

    public int tM;

    private boolean[] randomPoint;

    private Ant[] shortMemory;

    private Ant[] longMemory;

    private Globals _globals;

    private Random random = new Random(5);

    public Memory(Globals globals) {
        super();
        shortMemory = new Ant[shortMemorySize];
        longMemory = new Ant[longMemorySize];
        randomPoint = new boolean[longMemorySize];
        _globals = globals;
    }

    public void initMemoryRandomly() {
        for (int i = 0; i < longMemorySize; i++) {
            longMemory[i] = new Ant(_globals);
            longMemory[i].randomWalk();
            longMemory[i].computeCost();
            randomPoint[i] = true;
        }
        tM = 5 + ((int) (random.nextDouble() * 6.0));
    }

    public void updateLongTermMemory() {
        boolean flag = detectChange();
        if (flag == true) {
            updateMemoryEveryChange();
        }
        if (_globals.iteration == tM && flag == false) {
            updateMemoryDynamically();
            int rnd = 5 + ((int) (random.nextDouble() * 6.0));
            tM = _globals.iteration + rnd;
        }
        if (_globals.iteration == tM && flag == true) {
            int rnd = 5 + ((int) (random.nextDouble() * 6.0));
            tM = _globals.iteration + rnd;
        }
    }

    public void updateMemoryDynamically() {
        int index = -1;
        for (int i = 0; i < longMemorySize; i++) {
            if(_globals.bestSoFar.getCost() == longMemory[i].getCost()) return;
            if (randomPoint[i] == true) {
                index = i;
                randomPoint[i] = false;
                break;
            }
        }
        if (index != -1) {
            longMemory[index] = _globals.bestSoFar.clone();
        } else {
            int closestInd = -1;
            double closest = Integer.MAX_VALUE;
            for (int i = 0; i < longMemorySize; i++) {
                double d = distanceBetweenAnts(_globals.bestSoFar, longMemory[i]);
                if (closest > d) {
                    closest = d;
                    closestInd = i;
                }
            }
            if (_globals.bestSoFar.getCost() < longMemory[closestInd].getCost()) {
                longMemory[closestInd] = _globals.bestSoFar.clone();
            }
        }
    }

    public void updateMemoryEveryChange() {
        int index = -1;
        for (int i = 0; i < longMemorySize; i++) {
            if (randomPoint[i] == true) {
                index = i;
                randomPoint[i] = false;
                break;
            }
        }
        _globals.restartBestAnt.computeCost();
        if (index != -1) {
            longMemory[index] = _globals.restartBestAnt.clone();
        } else {
            double closest = Integer.MAX_VALUE;
            int closestInd = -1;
            for (int i = 0; i < longMemorySize; i++) {
                double d = distanceBetweenAnts(_globals.restartBestAnt, longMemory[i]);
                if (closest > d) {
                    closest = d;
                    closestInd = i;
                }
            }
            if (_globals.restartBestAnt.getCost() < longMemory[closestInd].getCost()) {
                longMemory[closestInd] = _globals.restartBestAnt;
            }
        }
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

    public boolean detectChange() {
        double totalBefore = 0.0;
        double totalAfter = 0.0;
        for (int i = 0; i < longMemorySize; i++) {
            totalBefore += longMemory[i].getCost();
        }
        for (int i = 0; i < longMemorySize; i++) {
            longMemory[i].computeCost();
            totalAfter += longMemory[i].getCost();
        }
        if (totalBefore == totalAfter)
            return false;
        else
            return true;
    }


    public void updateShortTermMemory() {
        int imSize = (int) (immigrantRate * shortMemorySize);
        Ant[] immigrants = new Ant[imSize];
        for (int i = 0; i < imSize; i++) {
            immigrants[i] = generateMemoryBasedImmigrant();

        }
        Ant[] antsPopulation = _globals.ants.clone();
        Utils.sortAntArray(antsPopulation);
        for (int i = 0; i < shortMemorySize; i++) {
            shortMemory[i] = antsPopulation[i].clone();
        }
        for (int i = shortMemorySize - 1; i > shortMemorySize - imSize - 1; i--) {
            shortMemory[i] = immigrants[shortMemorySize - 1 - i];
        }
    }

    public Ant generateMemoryBasedImmigrant() {
        Ant longMemoryBest = findLongTermBest().clone();
        for(int i = 1; i < longMemoryBest.getTour().size() - 1; i++) {
            if(random.nextDouble() < pMi) {
                int index = (int) ((random.nextDouble() * (longMemoryBest.getTour().size() - 2)) + 1);
                Node temp = longMemoryBest.getTour().get(i);
                longMemoryBest.getTour().set(i, longMemoryBest.getTour().get(index));
                longMemoryBest.getTour().set(index, temp);
            }
        }
        longMemoryBest.computeCost();
        return longMemoryBest;
    }

    public Ant findLongTermBest() {
        int index = 0;
        double cost = longMemory[index].getCost();
        for(int i = 1; i < longMemorySize; i++) {
            if(longMemory[i].getCost() < cost) {
                cost = longMemory[i].getCost();
                index = i;
            }
        }
        return longMemory[index];
    }

}
