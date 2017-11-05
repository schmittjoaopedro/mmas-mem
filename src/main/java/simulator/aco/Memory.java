package simulator.aco;

import simulator.graph.Node;
import simulator.utils.Utils;

import java.util.*;

public class Memory {

    public static int shortMemorySize;

    public static int longMemorySize;

    public static double immigrantRate;

    public static double pMi;

    public int tM;

    private boolean[] randomPoint;

    public Ant[] shortMemory;

    public Ant[] longMemory;

    private Globals _globals;

    private Random random = new Random();

    public Memory(Globals globals) {
        super();
        _globals = globals;
        if(_globals.isMMAS_MEM()) {
            shortMemorySize = 4;
            longMemorySize = 4;
            immigrantRate = 0.4;
            pMi = 0.01;
        }
        if(_globals.isMIACO()) {
            shortMemorySize = 10;
            longMemorySize = 4;
            immigrantRate = 0.4;
            pMi = 0.01;
        }
        shortMemory = new Ant[shortMemorySize];
        longMemory = new Ant[longMemorySize];
        randomPoint = new boolean[longMemorySize];
    }

    public void initMemoryRandomly() {
        for (int i = 0; i < longMemorySize; i++) {
            longMemory[i] = new Ant(_globals);
            longMemory[i].randomWalk();
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
        Ant ant = null;
        if(_globals.isMMAS_MEM()) ant = _globals.restartBestAnt;
        if(_globals.isMIACO()) ant = _globals.previousBestSoFarAnt;
        for (int i = 0; i < longMemorySize; i++) {
            if(ant.getCost() == longMemory[i].getCost()) return;
            if (randomPoint[i] == true) {
                index = i;
                randomPoint[i] = false;
                break;
            }
        }
        ant.computeCost();
        if (index != -1) {
            longMemory[index] = ant.clone();
        } else {
            double closest = Integer.MAX_VALUE;
            int closestInd = -1;
            for (int i = 0; i < longMemorySize; i++) {
                double d = distanceBetweenAnts(ant, longMemory[i]);
                if (closest > d) {
                    closest = d;
                    closestInd = i;
                }
            }
            if (ant.getCost() < longMemory[closestInd].getCost()) {
                longMemory[closestInd] = ant;
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
        if(_globals.isMMAS_MEM()) {
            Set<Ant> antsPopulation = new HashSet<>();
            Set<Double> antsCosts = new HashSet<>();
            antsPopulation.add(_globals.restartBestAnt);
            antsCosts.add(_globals.restartBestAnt.getCost());
            Utils.sortAntArray(_globals.ants);
            for (Ant ant : _globals.ants) {
                if(!antsCosts.contains(ant.getCost())) {
                    antsPopulation.add(ant);
                    antsCosts.add(ant.getCost());
                }
            }
            if(antsPopulation.size() < shortMemorySize) {
                for (Ant ant : _globals.ants) {
                    if(!antsPopulation.contains(ant) && antsPopulation.size() < shortMemorySize) {
                        antsPopulation.add(ant);
                    }
                }
            }
            Ant[] ants = antsPopulation.toArray(new Ant[] {});
            Utils.sortAntArray(ants);
            for (int i = 0; i < shortMemorySize; i++) {
                shortMemory[i] = ants[i].clone();
            }
            for (int i = shortMemorySize - 1; i > shortMemorySize - imSize - 1; i--) {
                shortMemory[i] = immigrants[shortMemorySize - 1 - i];
            }
            Utils.sortAntArray(shortMemory);
        }
        if(_globals.isMIACO()) {
            Utils.sortAntArray(_globals.ants);
            for (int i = 0; i < shortMemorySize; i++) {
                shortMemory[i] = _globals.ants[i].clone();
            }
            for (int i = shortMemorySize - 1; i > shortMemorySize - imSize - 1; i--) {
                shortMemory[i] = immigrants[shortMemorySize - 1 - i];
            }
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

    public void generatePheromoneMatrix() {
        double deltaT = (1.0 - _globals.trail0) / (double) shortMemorySize;
        for(Route route : _globals.routeManager.getRoutes()) {
            route.setPheromone(_globals.trail0);
        }
        for (int i = 0; i < shortMemorySize; i++) {
            constantPheromoneDeposit(shortMemory[i], deltaT);
        }
    }

    private void constantPheromoneDeposit(Ant ant, double deltaT) {
        for (int i = 0; i < ant.getTour().size() - 1; i++) {
            int fromId = ant.getTour().get(i).getId();
            int toId = ant.getTour().get(i + 1).getId();
            Route route = _globals.routeManager.getRoute(fromId, toId);
            route.setPheromone(route.getPheromone() + deltaT);
        }
    }
}
