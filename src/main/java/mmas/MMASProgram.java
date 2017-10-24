package mmas;

import schmitt.joao.aco.TspReader;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class MMASProgram {

    private static double distance[][];

    private static int n;

    private static int nnList[][];

    private static int nnListSize = 20;

    private static double[][] pheromone;

    private static double[][] total;

    private static int maxIterations = 5000;

    private static int currentIteration;

    private static double lambda = 0.05;

    private static int nAnts = 25;

    private static double alpha = 1.0;

    private static double beta = 2.0;

    private static double rho = 0.5;

    private static double q_0 = 0.0;

    private static double branch_fac = 1.00001;

    private static double u_gb = Integer.MAX_VALUE;

    private static ant[] ants;

    private static ant bestSoFarAnt;

    private static ant restartBestAnt;

    private static double trailMax;

    private static double trailMin;

    private static int restartFoundBest;

    private static double foundBranching;

    private static double branchingFactor;

    private static Random random = new Random(1);

    public static void main(String[] args) {

        String path = "/home/joao/projects/master-degree/aco-tsp-algorithm/tsp/";
        String file = "lin318.tsp";
        distance = TspReader.getDistances(path, file);
        n = distance.length;
        System.out.print("TSP Loaded");

        allocateAnts();
        computeNNList();
        pheromone = new double[n][n];
        total = new double[n][n];
        initTry();

        while (currentIteration < maxIterations) {
            construct_solutions();
            update_statistics();
            pheromone_trail_update();
            search_control_and_statistics();
            currentIteration++;
        }

        System.out.println("Finish " + bestSoFarAnt.tourLength);
    }

    static void search_control_and_statistics() {
        if ((currentIteration % 100) == 0) {
            population_statistics();
            branchingFactor = node_branching(lambda);
            System.out.println("best so far " + bestSoFarAnt.tourLength + ", iteration: " + currentIteration + ", b_fac " + branchingFactor);

            if (branchingFactor < branch_fac && (currentIteration - restartFoundBest > 250)) {
                System.out.println("INIT TRAILS!!!\n");
                restartBestAnt.tourLength = Integer.MAX_VALUE;
                initPheromoneTrails(trailMax);
                compute_total_information();
            }
            System.out.println("iteration " + currentIteration + ", b-fac " + branchingFactor);
        }
    }

    static void population_statistics() {
        int j, k;
        int[] l;
        double pop_mean, pop_stddev, avg_distance = 0.0;
        l = new int[nAnts];
        for (k = 0; k < nAnts; k++) {
            l[k] = ants[k].tourLength;
        }
        pop_mean = Utilities.mean(l, nAnts);
        pop_stddev = Utilities.std_deviation(l, nAnts, pop_mean);
        branchingFactor = node_branching(lambda);

        for (k = 0; k < nAnts - 1; k++)
            for (j = k + 1; j < nAnts; j++) {
                avg_distance += (double) distance_between_ants(ants[k], ants[j]);
            }
        avg_distance /= ((double) nAnts * (double) (nAnts - 1) / 2.);

        String msg = "(Iteration = " + currentIteration + " ) (Mean = " + pop_mean + ") (Sd = " + pop_stddev
                 + ") (Branch = " + branchingFactor + ") (Dist = " + avg_distance + ")";
        System.out.println(msg);
    }

    static int distance_between_ants(ant a1, ant a2) {
        int i, j, h, pos, pred;
        int distance;
        int[] pos2;
        pos2 = new int[n];
        for (i = 0; i < n; i++) {
            pos2[a2.tour[i]] = i;
        }
        distance = 0;
        for (i = 0; i < n; i++) {
            j = a1.tour[i];
            h = a1.tour[i + 1];
            pos = pos2[j];
            if (pos - 1 < 0)
                pred = n - 1;
            else
                pred = pos - 1;
            if (a2.tour[pos + 1] == h)
                ;
            else if (a2.tour[pred] == h)
                ;
            else {
                distance++;
            }
        }
        return distance;
    }

    static void pheromone_trail_update() {
        evaporation();
        mmas_update();
        check_pheromone_trail_limits();
        compute_total_information();
    }

    static void check_pheromone_trail_limits() {
        int i, j;
        for (i = 0; i < n; i++) {
            for (j = 0; j < i; j++) {
                if (pheromone[i][j] < trailMin) {
                    pheromone[i][j] = trailMin;
                    pheromone[j][i] = trailMin;
                } else if (pheromone[i][j] > trailMax) {
                    pheromone[i][j] = trailMax;
                    pheromone[j][i] = trailMax;
                }
            }
        }
    }

    static void mmas_update() {
        int iteration_best_ant;
        if (currentIteration % u_gb == 0) {
            iteration_best_ant = find_best();
            global_update_pheromone(ants[iteration_best_ant]);
        } else {
            if (u_gb == 1 && (currentIteration - restartFoundBest > 50))
                global_update_pheromone(bestSoFarAnt);
            else
                global_update_pheromone(restartBestAnt);
        }
        u_gb = 25;
    }

    static void global_update_pheromone(ant a) {
        int i, j, h;
        double d_tau;
        d_tau = 1.0 / (double) a.tourLength;
        for (i = 0; i < n; i++) {
            j = a.tour[i];
            h = a.tour[i + 1];
            pheromone[j][h] += d_tau;
            pheromone[h][j] = pheromone[j][h];
        }
    }

    static void evaporation() {
        int i, j;
        for (i = 0; i < n; i++) {
            for (j = 0; j <= i; j++) {
                pheromone[i][j] = (1 - rho) * pheromone[i][j];
                pheromone[j][i] = pheromone[i][j];
            }
        }
    }

    static void update_statistics() {

        int iteration_best_ant;
        double p_x;

        iteration_best_ant = find_best();

        if (ants[iteration_best_ant].tourLength < bestSoFarAnt.tourLength) {

            copy_from_to(ants[iteration_best_ant], bestSoFarAnt);
            copy_from_to(ants[iteration_best_ant], restartBestAnt);

            restartFoundBest = currentIteration;
            foundBranching = node_branching(lambda);
            branchingFactor = foundBranching;
            p_x = Math.exp(Math.log(0.05) / n);
            trailMin = 1. * (1. - p_x) / (p_x * (double) ((nnListSize + 1) / 2));
            trailMax = 1. / ((rho) * bestSoFarAnt.tourLength);
            trailMin = trailMax * trailMin;
            System.out.println("best " + bestSoFarAnt.tourLength + ", iteration: " + currentIteration);
        }
        if (ants[iteration_best_ant].tourLength < restartBestAnt.tourLength) {
            copy_from_to(ants[iteration_best_ant], restartBestAnt);
            restartFoundBest = currentIteration;
            System.out.println("restart best: " + restartBestAnt.tourLength + " restart_found_best " + restartFoundBest);
        }
    }

    static double node_branching(double l) {
        int i, m;
        double min, max, cutoff;
        double avg;
        double[] num_branches = new double[n];
        for (m = 0; m < n; m++) {
            min = pheromone[m][nnList[m][1]];
            max = pheromone[m][nnList[m][1]];
            for (i = 1; i < nnListSize; i++) {
                if (pheromone[m][nnList[m][i]] > max)
                    max = pheromone[m][nnList[m][i]];
                if (pheromone[m][nnList[m][i]] < min)
                    min = pheromone[m][nnList[m][i]];
            }
            cutoff = min + l * (max - min);

            for (i = 0; i < nnListSize; i++) {
                if (pheromone[m][nnList[m][i]] > cutoff)
                    num_branches[m] += 1.;
            }
        }
        avg = 0.;
        for (m = 0; m < n; m++) {
            avg += num_branches[m];
        }
        return (avg / (double) (n * 2));
    }

    static void copy_from_to(ant a1, ant a2) {
        int i;
        a2.tourLength = a1.tourLength;
        for (i = 0; i < n; i++) {
            a2.tour[i] = a1.tour[i];
        }
        a2.tour[n] = a2.tour[0];
    }

    static int find_best() {
        int min;
        int k, k_min;
        min = ants[0].tourLength;
        k_min = 0;
        for (k = 1; k < nAnts; k++) {
            if (ants[k].tourLength < min) {
                min = ants[k].tourLength;
                k_min = k;
            }
        }
        return k_min;
    }

    static void construct_solutions() {
        int k;
        int step;
        for (k = 0; k < nAnts; k++) {
            antEmptyMemory(ants[k]);
        }
        step = 0;
        for (k = 0; k < nAnts; k++)
            placeAnt(ants[k], step);
        while (step < n - 1) {
            step++;
            for (k = 0; k < nAnts; k++) {
                neighbour_choose_and_move_to_next(ants[k], step);
            }
        }
        step = n;
        for (k = 0; k < nAnts; k++) {
            ants[k].tour[n] = ants[k].tour[0];
            ants[k].tourLength = computeTourLength(ants[k].tour);
        }
    }

    static void neighbour_choose_and_move_to_next(ant a, int phase) {
        int i, help;
        int current_city;
        double rnd, partial_sum = 0., sum_prob = 0.0;
        double prob_ptr[] = new double[nnListSize + 1];
        if ((q_0 > 0.0) && (random.nextDouble() < q_0)) {
            neighbour_choose_best_next(a, phase);
            return;
        }

        current_city = a.tour[phase - 1];
        assert (current_city >= 0 && current_city < n);
        for (i = 0; i < nnListSize; i++) {
            if (a.visited[nnList[current_city][i]])
                prob_ptr[i] = 0.0;
            else {
                prob_ptr[i] = total[current_city][nnList[current_city][i]];
                sum_prob += prob_ptr[i];
            }
        }
        if (sum_prob <= 0.0) {
            choose_best_next(a, phase);
        } else {
            rnd = random.nextDouble();
            rnd *= sum_prob;
            i = 0;
            partial_sum = prob_ptr[i];
            while (partial_sum <= rnd) {
                i++;
                partial_sum += prob_ptr[i];
            }
            if (i == nnListSize) {
                neighbour_choose_best_next(a, phase);
                return;
            }
            help = nnList[current_city][i];
            a.tour[phase] = help;
            a.visited[help] = true;
        }
    }

    static void neighbour_choose_best_next(ant a, int phase) {
        int i, current_city, next_city, help_city;
        double value_best, help;
        next_city = n;
        current_city = a.tour[phase - 1];
        value_best = -1.; /* values in total matix are always >= 0.0 */
        for (i = 0; i < nnListSize; i++) {
            help_city = nnList[current_city][i];
            if (a.visited[help_city])
                ;
            else {
                help = total[current_city][help_city];
                if (help > value_best) {
                    value_best = help;
                    next_city = help_city;
                }
            }
        }
        if (next_city == n)
            choose_best_next(a, phase);
        else {
            a.tour[phase] = next_city;
            a.visited[next_city] = true;
        }
    }

    static void choose_best_next(ant a, int phase) {
        int city, current_city, next_city;
        double value_best;
        next_city = n;
        current_city = a.tour[phase - 1];
        value_best = -1.;
        for (city = 0; city < n; city++) {
            if (a.visited[city])
                ;
            else {
                if (total[current_city][city] > value_best) {
                    next_city = city;
                    value_best = total[current_city][city];
                }
            }
        }
        a.tour[phase] = next_city;
        a.visited[next_city] = true;
    }

    public static void allocateAnts() {
        ants = new ant[nAnts];
        for (int i = 0; i < nAnts; i++) {
            ants[i] = new ant();
            ants[i].tour = new int[n + 1];
            ants[i].visited = new boolean[n];
        }

        bestSoFarAnt = new ant();
        bestSoFarAnt.tour = new int[n + 1];
        bestSoFarAnt.visited = new boolean[n];

        restartBestAnt = new ant();
        restartBestAnt.tour = new int[n + 1];
        restartBestAnt.visited = new boolean[n];

    }

    public static void computeNNList() {
        nnList = new int[n][nnListSize];
        for (int i = 0; i < n; i++) {
            Integer[] nodeIndex = new Integer[n];
            Double[] nodeData = new Double[n];
            for (int j = 0; j < n; j++) {
                nodeIndex[j] = j;
                nodeData[j] = distance[i][j];
            }
            nodeData[i] = Collections.max(Arrays.asList(nodeData));
            Arrays.sort(nodeIndex, new Comparator<Integer>() {
                public int compare(final Integer o1, final Integer o2) {
                    return Double.compare(nodeData[o1], nodeData[o2]);
                }
            });
            for (int r = 0; r < nnListSize; r++) {
                nnList[i][r] = nodeIndex[r];
            }
        }
    }

    public static void initTry() {
        currentIteration = 1;
        lambda = 0.05;
        bestSoFarAnt.tourLength = Integer.MAX_VALUE;

        trailMax = 1. / (rho * nnTour());
        trailMin = trailMax / (2. * n);
        initPheromoneTrails(trailMax);
        compute_total_information();

    }

    static double HEURISTIC(int m, int n) {
        return (1.0 / ((double) distance[m][n] + 0.1));
    }

    static void compute_total_information() {
        int i, j;
        for (i = 0; i < n; i++) {
            for (j = 0; j < i; j++) {
                total[i][j] = Math.pow(pheromone[i][j], alpha) * Math.pow(HEURISTIC(i, j), beta);
                total[j][i] = total[i][j];
            }
        }
    }

    static void initPheromoneTrails(double initial_trail) {
        int i, j;
        for (i = 0; i < n; i++) {
            for (j = 0; j <= i; j++) {
                pheromone[i][j] = initial_trail;
                pheromone[j][i] = initial_trail;
                total[i][j] = initial_trail;
                total[j][i] = initial_trail;
            }
        }
    }

    public static double nnTour() {
        int phase, help;
        antEmptyMemory(ants[0]);
        phase = 0;
        placeAnt(ants[0], phase);

        while (phase < n - 1) {
            phase++;
            chooseClosestNext(ants[0], phase);
        }
        phase = n;
        ants[0].tour[n] = ants[0].tour[0];
        ants[0].tourLength = computeTourLength(ants[0].tour);

        help = ants[0].tourLength;
        antEmptyMemory(ants[0]);
        return help;
    }

    static void antEmptyMemory(ant a) {
        int i;
        for (i = 0; i < n; i++) {
            a.visited[i] = false;
        }
    }

    static void placeAnt(ant a, int step) {
        int rnd = (int) (random.nextDouble() * (double) n);
        a.tour[step] = rnd;
        a.visited[rnd] = true;
    }

    static void chooseClosestNext(ant a, int phase) {
        int city, current_city, next_city;
        double min_distance;
        next_city = n;
        assert (phase > 0 && phase < n);
        current_city = a.tour[phase - 1];
        min_distance = Double.MAX_VALUE;
        for (city = 0; city < n; city++) {
            if (a.visited[city])
                ;
            else {
                if (distance[current_city][city] < min_distance) {
                    next_city = city;
                    min_distance = distance[current_city][city];
                }
            }
        }
        assert (0 <= next_city && next_city < n);
        a.tour[phase] = next_city;
        a.visited[next_city] = true;
    }

    static int computeTourLength(int[] t) {
        int i;
        int tour_length = 0;

        for (i = 0; i < n; i++) {
            tour_length += distance[t[i]][t[i + 1]];
        }
        return tour_length;
    }

}
