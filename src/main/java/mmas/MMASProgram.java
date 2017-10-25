package mmas;

import schmitt.joao.aco.TspReader;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class MMASProgram {

    private static vertex initial_coordinates[];

    private static vertex coordinates[];

    private static double distance[][];

    private static int n;

    private static int nnList[][];

    private static int nnListSize = 20;

    private static double[][] pheromone;

    private static double[][] total;

    private static int maxIterations = 1000;

    private static int currentIteration;

    private static double lambda = 0.05;

    private static int nAnts = 50;

    private static double alpha = 1.0;

    private static double beta = 2.0;

    private static double rho = 0.02;

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

    private static boolean useMIACO = false;

    public static void main(String[] args) {

        String path = "/home/joao/projects/master-degree/aco-tsp-algorithm/tsp/";
        String file = "lin318.tsp";
        double[][] coord = TspReader.getCoordinates(path, file);
        initial_coordinates = new vertex[coord.length];
        coordinates = new vertex[coord.length];
        for(int i = 0; i < coord.length; i++) {
            initial_coordinates[i] = new vertex();
            initial_coordinates[i].id = i;
            initial_coordinates[i].x = coord[i][0];
            initial_coordinates[i].y = coord[i][1];
            coordinates[i] = new vertex();
            coordinates[i].id = i;
            coordinates[i].x = coord[i][0];
            coordinates[i].y = coord[i][1];
        }
        n = coordinates.length;
        distance = new double[n][n];
        computeDistances();
        System.out.println("TSP Loaded");

        allocateAnts();
        allocateStructures();
        computeNNList();
        pheromone = new double[n][n];
        total = new double[n][n];
        initTry();
        initialize_environment();
        while (currentIteration < maxIterations) {
            construct_solutions();
            update_statistics();
            pheromone_trail_update();
            search_control_and_statistics();
            if (currentIteration % change_speed == 0) {
                System.out.println("Best at change " + bestSoFarAnt.tourLength + " iteration " + currentIteration + " cycle " + cyclic_base_count + " diversity " + calc_diversity());
                change_environment();
                apply_to_algorithm();
            }
            currentIteration++;
        }

        System.out.println("Finish " + bestSoFarAnt.tourLength);
    }

    static double euclideanDistance(int i, int j) {
        double xd = coordinates[i].x - coordinates[j].x;
        double yd = coordinates[i].y - coordinates[j].y;
        return (int) (Math.sqrt(xd * xd + yd * yd) + 0.5);
    }

    static void computeDistances() {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if(i == j) {
                    distance[i][j] = 0;
                } else {
                    distance[i][j] = euclideanDistance(i, j);
                }
            }
        }
    }

    static double calc_diversity() {
        double div = 0.0;
        for (int i = 0; i < nAnts; i++) {
            for (int j = 0; j < nAnts; j++) {
                if (i != j) {
                    div += distance_between_ants(ants[i], ants[j]);
                }
            }
        }
        return (1.0 / (nAnts * (nAnts - 1.0))) * div;
    }

    static void search_control_and_statistics() {
        if ((currentIteration % 100) == 0) {
            population_statistics();
            branchingFactor = node_branching(lambda);
            //System.out.println("best so far " + bestSoFarAnt.tourLength + ", iteration: " + currentIteration + ", b_fac " + branchingFactor);

            if (branchingFactor < branch_fac && (currentIteration - restartFoundBest > 250)) {
                //System.out.println("INIT TRAILS!!!\n");
                restartBestAnt.tourLength = Integer.MAX_VALUE;
                initPheromoneTrails(trailMax);
                compute_total_information();
            }
            //System.out.println("iteration " + currentIteration + ", b-fac " + branchingFactor);
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
        //System.out.println(msg);
    }

    static double distance_between_ants(ant a1, ant a2) {
        int i, j, h, pos, pred;
        double distance = 0.0;
        int[] pos2;
        pos2 = new int[n];
        for (i = 0; i < n; i++) {
            pos2[a2.tour[i]] = i;
        }
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
        return 1.0 - (distance / (double) n);
    }

    static void pheromone_trail_update() {
        if(useMIACO) {
            initPheromoneTrails(trailMin);
        } else {
            evaporation();
        }
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
        if(useMIACO) {
            update_long_term_memory();
            update_short_term_memory();
        }
        if (currentIteration % u_gb == 0) {
            if(useMIACO) {
                for (int i = 0; i < shortMemorySize; i++) {
                    global_update_pheromone(shortMemory[i]);
                }
            } else {
                int iteration_best_ant = find_best();
                global_update_pheromone(ants[iteration_best_ant]);
            }
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
            //System.out.println("best " + bestSoFarAnt.tourLength + ", iteration: " + currentIteration);
        }
        if (ants[iteration_best_ant].tourLength < restartBestAnt.tourLength) {
            copy_from_to(ants[iteration_best_ant], restartBestAnt);
            restartFoundBest = currentIteration;
            //System.out.println("restart best: " + restartBestAnt.tourLength + " restart_found_best " + restartFoundBest);
        }
        if (useMIACO) {
            copy_from_to(bestSoFarAnt, previousBestSoFarAnt);
            copy_from_to(previousBest[1], previousBest[0]);
            copy_from_to(previousBestSoFarAnt, previousBest[1]);
            copy_from_to(previousBest[0], previousBestSoFarAnt);
            if (currentIteration == 1) copy_from_to(bestSoFarAnt, previousBestSoFarAnt);
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

        if(useMIACO) {
            previousBest = new ant[2];
            for (int i = 0; i < 2; i++) {
                previousBest[i] = new ant();
                previousBest[i].tour = new int[n + 1];
                previousBest[i].visited = new boolean[n];
            }

            previousBestSoFarAnt = new ant();
            previousBestSoFarAnt.tour = new int[n + 1];
            previousBestSoFarAnt.visited = new boolean[n];
        }

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
        if(useMIACO) initMemoryRandomly();
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


    /**
     *
     * MIACO CODE
     *
     */

    private static int cyclic_base_count;

    private static int change_speed = 100;

    private static double change_degree = 0.75;

    private static int cyclic_states = 4;

    private static int[][] cyclic_random_vector;

    private static int[][] cyclic_re_random_vector;

    private static int[] random_vector;

    private static int[] re_random_vector;

    //Miaco
    private static ant previousBestSoFarAnt;

    private static ant[] previousBest;

    private static ant[] shortMemory;

    private static ant[] longMemory;

    private static double immigrantRate = 0.4;

    private static double pMi = 0.01;

    private static int shortMemorySize = 6;

    private static int longMemorySize = 4;

    private static int tM;

    private static boolean[] randomPoint;

    private static Random randomEnv = new Random(2);

    static void initialize_environment() {
        for (int i = 0; i < n; i++) {
            coordinates[i].id = initial_coordinates[i].id;
            coordinates[i].x = initial_coordinates[i].x;
            coordinates[i].y = initial_coordinates[i].y;
        }
        generate_cyclic_environment();
        cyclic_base_count = 0;
        add_cyclic_change(cyclic_base_count);
        apply_to_algorithm();
    }

    static void generate_cyclic_environment() {
        int changes = (int) Math.abs(change_degree * n);
        cyclic_random_vector = new int[cyclic_states][changes];
        cyclic_re_random_vector = new int[cyclic_states][changes];
        for (int i = 0; i < cyclic_states; i++) {
            random_vector = generate_random_vector(n);
            re_random_vector = generate_reordered_random_vector();
            for (int j = 0; j < changes; j++) {
                cyclic_random_vector[i][j] = random_vector[j];
                cyclic_re_random_vector[i][j] = re_random_vector[j];
            }
        }
    }

    static int[] generate_random_vector(int size) {
        int tot_assigned = 0;
        int r[] = new int[size];
        for (int i = 0; i < size; i++) {
            r[i] = i;
        }
        for (int i = 0; i < size; i++) {
            double rnd = randomEnv.nextDouble();
            int node = (int) (rnd * (size - tot_assigned));
            int help = r[i];
            r[i] = r[i + node];
            r[i + node] = help;
            tot_assigned++;
        }
        return r;
    }

    static int[] generate_reordered_random_vector() {
        int changes = (int) Math.abs(change_degree * n);
        int[] r = new int[changes];
        for (int i = 0; i < changes; i++) {
            r[i] = random_vector[i];
        }
        for (int i = 0; i < changes; i++) {
            int help = r[i];
            int r_index = (int) (randomEnv.nextDouble() * (double) changes);
            r[i] = r[r_index];
            r[r_index] = help;
        }
        return r;
    }

    static void add_cyclic_change(int state) {
        int changes = (int) Math.abs(change_degree * n);
        for (int i = 0; i < changes; i++) {
            int obj1 = coordinates[cyclic_random_vector[state][i]].id;
            int obj2 = coordinates[cyclic_re_random_vector[state][i]].id;
            random_vector[i] = cyclic_random_vector[state][i];
            re_random_vector[i] = cyclic_re_random_vector[state][i];
            swap_masked_objects(obj1, obj2);
        }
    }

    static void swap_masked_objects(int obj1, int obj2) {
        double help1 = coordinates[obj1].x;
        double help2 = coordinates[obj1].y;
        coordinates[obj1].x = coordinates[obj2].x;
        coordinates[obj1].y = coordinates[obj2].y;
        coordinates[obj2].x = help1;
        coordinates[obj2].y = help2;
    }

    static void change_environment() {
        if (cyclic_base_count == cyclic_states - 1)
            cyclic_base_count = 0;
        else
            cyclic_base_count++;
        reverse_changes();
        add_cyclic_change(cyclic_base_count);
        computeDistances();
    }

    static void reverse_changes() {
        int changes = (int) Math.abs(change_degree * n);
        for (int i = changes - 1; i >= 0; i--) {
            int obj1 = coordinates[re_random_vector[i]].id;
            int obj2 = coordinates[random_vector[i]].id;
            swap_masked_objects(obj1, obj2);
        }
    }

    static void apply_to_algorithm() {
        computeNNList();
        compute_total_information();
        bestSoFarAnt.tourLength = Integer.MAX_VALUE;
    }

    static void allocateStructures() {
        shortMemory = new ant[shortMemorySize];
        longMemory = new ant[longMemorySize];
        randomPoint = new boolean[longMemorySize];
    }

    static void initMemoryRandomly() {
        for (int i = 0; i < longMemorySize; i++) {
            longMemory[i] = new ant();
            longMemory[i].tour = generate_random_immigrant();
            longMemory[i].tourLength = computeTourLength(longMemory[i].tour);
            randomPoint[i] = true;
        }
        tM = 5 + ((int) (random.nextDouble() * 6.0));
    }

    static int[] generate_random_immigrant() {
        int[] random_immigrant = new int[n + 1];
        int tot_assigned = 0;
        for (int i = 0; i < n; i++) {
            random_immigrant[i] = i;
        }
        random_immigrant[n] = random_immigrant[0];
        for (int i = 0; i < n; i++) {
            int object = (int) (random.nextDouble() * (double) (n - tot_assigned));
            int help = random_immigrant[i];
            random_immigrant[i] = random_immigrant[i + object];
            random_immigrant[i + object] = help;
            tot_assigned++;
        }
        random_immigrant[n] = random_immigrant[0];
        return random_immigrant;
    }

    static void update_long_term_memory() {
        boolean flag = detect_change();
        if (flag == true) {
            update_memory_every_change();
        }
        if (currentIteration == tM && flag == false) {
            update_memory_dynamically();
            int rnd = 5 + ((int) (random.nextDouble() * 6.0));
            tM = currentIteration + rnd;
        }
        if (currentIteration == tM && flag == true) {
            int rnd = 5 + ((int) (random.nextDouble() * 6.0));
            tM = currentIteration + rnd;
        }
    }

    static void update_memory_dynamically() {
        int index = -1;
        for (int i = 0; i < longMemorySize; i++) {
            if (randomPoint[i] == true) {
                index = i;
                randomPoint[i] = false;
                break;
            }
        }
        if (index != -1) {
            copy_from_to(bestSoFarAnt, longMemory[index]);
        } else {
            int closest_ind = -1;
            double closest = Integer.MAX_VALUE;
            for (int i = 0; i < longMemorySize; i++) {
                double d = distance_between_ants(bestSoFarAnt, longMemory[i]);
                if (closest > d) {
                    closest = d;
                    closest_ind = i;
                }
            }
            if (bestSoFarAnt.tourLength < longMemory[closest_ind].tourLength) {
                copy_from_to(bestSoFarAnt, longMemory[closest_ind]);
            }
        }
    }

    static void update_memory_every_change() {
        int index = -1;
        for (int i = 0; i < longMemorySize; i++) {
            if (randomPoint[i] == true) {
                index = i;
                randomPoint[i] = false;
                break;
            }
        }
        previousBestSoFarAnt.tourLength = computeTourLength(previousBestSoFarAnt.tour);
        if (index != -1) {
            copy_from_to(previousBestSoFarAnt, longMemory[index]);
        } else {
            double closest = Integer.MAX_VALUE;
            int closest_ind = -1;
            for (int i = 0; i < longMemorySize; i++) {
                double d = distance_between_ants(previousBestSoFarAnt, longMemory[i]);
                if (closest > d) {
                    closest = d;
                    closest_ind = i;
                }
            }
            if (previousBestSoFarAnt.tourLength < longMemory[closest_ind].tourLength) {
                copy_from_to(previousBestSoFarAnt, longMemory[closest_ind]);
            }
        }
    }

    static boolean detect_change() {
        int i, total_before, total_after;
        total_before = total_after = 0;
        for (i = 0; i < longMemorySize; i++) {
            total_before += longMemory[i].tourLength;
        }
        for (i = 0; i < longMemorySize; i++) {
            longMemory[i].tourLength = computeTourLength(longMemory[i].tour);
            total_after += longMemory[i].tourLength;
        }
        if (total_after == total_before)
            return false;
        else
            return true;
    }

    static void update_short_term_memory() {
        int im_size = (int) (shortMemorySize * immigrantRate);
        Integer[] tours = new Integer[nAnts];
        Integer[] id = new Integer[nAnts];
        ant[] immigrants = new ant[im_size];
        for (int i = 0; i < im_size; i++) {
            immigrants[i] = new ant();
            immigrants[i].tour = generate_memory_based_immigrant();
            immigrants[i].tourLength = computeTourLength(immigrants[i].tour);
        }
        for (int i = 0; i < nAnts; i++) {
            tours[i] = ants[i].tourLength;
            id[i] = i;
        }
        Arrays.sort(id, new Comparator<Integer>() {
            public int compare(final Integer o1, final Integer o2) {
                return Double.compare(tours[o1], tours[o2]);
            }
        });
        for (int i = 0; i < shortMemorySize; i++) {
            shortMemory[i] = ants[id[i]];
        }
        for (int i = shortMemorySize - 1; i > shortMemorySize - im_size - 1; i--) {
            copy_from_to(immigrants[shortMemorySize - 1 - i], shortMemory[i]);
        }
    }

    static int[] generate_memory_based_immigrant() {
        int[] memory_immigrant = new int[n + 1];
        int mem_ind = find_memory_best();
        for (int i = 0; i < n; i++) {
            memory_immigrant[i] = longMemory[mem_ind].tour[i];
        }
        memory_immigrant[n] = memory_immigrant[0];
        for (int i = 0; i < n; i++) {
            if (random.nextDouble() <= pMi) {
                int object = (int) (random.nextDouble() * (double) (n - 1));
                int help = memory_immigrant[i];
                memory_immigrant[i] = memory_immigrant[object];
                memory_immigrant[object] = help;
            }
        }
        memory_immigrant[n] = memory_immigrant[0];
        return memory_immigrant;
    }

    static int find_memory_best() {
        int k, k_min, min;
        min = longMemory[0].tourLength;
        k_min = 0;
        for (k = 1; k < longMemorySize; k++) {
            if (longMemory[k].tourLength < min) {
                min = longMemory[k].tourLength;
                k_min = k;
            }
        }
        return k_min;
    }
}
