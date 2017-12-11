package schmitt.joao;

import org.junit.Test;

import java.util.Random;

public class LS2Opt {

    static boolean dlb_flag = true;

    private static Random random = new Random(1);

    @Test
    public void test2Opt() {

        int[][] graph = {
            { 0, 8, 5, 12, 4, 8, 10, 11, 15, 12 },
            { 7, 0, 4, 3, 7, 6, 9, 7, 12, 13},
            { 3, 3, 0, 6, 1, 6, 10, 7, 11 ,12 },
            { 14, 4, 7, 0, 8, 6, 8, 5, 11, 8 },
            { 6, 5, 2, 3, 0, 6, 7, 4, 9, 10 },
            { 8, 7, 7, 4, 5, 0, 2, 4, 8, 15 },
            { 9, 7, 9, 9, 3, 7, 0, 5, 6, 7 },
            { 12, 5, 5, 6, 9, 9, 4, 0, 5, 9},
            { 5, 10, 9, 9, 8, 5, 5, 2, 0, 2},
            { 14, 9, 10, 12, 13, 11, 3, 7, 6, 0}
        };

        /*int[][] graph = {
                { 0,  8,  5,  12, 4,  8,  10, 11, 15, 12 },
                { 8,  0,  4,  3,  7,  6,  9,  7,  12, 13 },
                { 5,  4,  0,  6,  1,  6,  10, 7,  11 ,12 },
                { 12, 3,  6,  0,  8,  6,  8,  5,  11, 8  },
                { 4,  7,  1,  8,  0,  6,  7,  4,  9,  10 },
                { 8,  6,  6,  6,  6,  0,  2,  4,  8,  15 },
                { 10, 9,  10, 8,  7,  2,  0,  5,  6,  7  },
                { 11, 7,  7,  5,  4,  4,  5,  0,  5,  9  },
                { 15, 12, 11, 11, 9,  8,  6,  5,  0,  2  },
                { 12, 13, 12, 8,  10, 15, 7,  9,  2,  0  }
        };*/

        int tour[] = { 0, 3, 7, 5, 8, 9, 6, 4, 2, 1, 0 };
        int nn_ls = 6;
        int nn_list[][] = getNNList(graph.length, nn_ls, graph);
        showCost(tour, graph);
        two_opt_first(tour, graph, graph.length, nn_ls, nn_list);
        showCost(tour, graph);
    }

    static void showCost(int[] tour, int[][] graph) {
        int total = 0;
        for(int i = 0; i < graph.length; i++) {
            total += graph[tour[i]][tour[i+1]];
            System.out.print(tour[i] + "->");
        }
        System.out.print(tour[graph.length] + " = ");
        System.out.println(total);
    }

    static int[][] getNNList(int n, int ls, int[][] graph) {
        int[][] nn_ls = new int[n][ls];
        for(int i = 0; i < n; i++) {
            boolean[] visited = new boolean[n];
            visited[i] = true;
            for(int j = 0; j < ls; j++) {
                int best = -1;
                for(int k = n - 1; k >= 0; k--) {
                    if (!visited[k] && (best == -1 || graph[i][k] <= graph[i][best])) {
                        best = k;
                    }
                }
                visited[best] = true;
                nn_ls[i][j] = best;
            }
        }
        return nn_ls;
    }

    static int[] generate_random_permutation(int n) {
        int i, help, node, tot_assigned = 0;
        double rnd;
        int[] r;
        r = new int[n];
        for (i = 0; i < n; i++)
            r[i] = i;
        for (i = 0; i < n; i++) {
            rnd = random.nextDouble();
            node = (int) (rnd * (n - tot_assigned));
            assert (i + node < n);
            help = r[i];
            r[i] = r[i + node];
            r[i + node] = help;
            tot_assigned++;
        }
        return r;
    }

    static void two_opt_first(int[] tour, int[][] distances, int problem_size, int nn_ls, int[][] nn_list) {
        boolean gotoExchange = false;

        int c1, c2; /* cities considered for an exchange */
        int s_c1, s_c2; /* successor cities of c1 and c2 */
        int p_c1, p_c2; /* predecessor cities of c1 and c2 */
        int pos_c1, pos_c2; /* positions of cities c1, c2 */
        int i, j, h, l;
        int help;
        boolean improvement_flag;
        int h1 = 0, h2 = 0, h3 = 0, h4 = 0;
        int radius; /* radius of nn-search */
        int gain = 0;
        int[] random_vector;
        int[] pos; /* positions of cities in tour */
        boolean[] dlb; /* vector containing don't look bits */

        pos = new int[problem_size];
        dlb = new boolean[problem_size];
        for (i = 0; i < problem_size; i++) {
            pos[tour[i]] = i;
            dlb[i] = false;
        }

        improvement_flag = true;
        random_vector = generate_random_permutation(problem_size);

        while (improvement_flag) {

            improvement_flag = false;

            for (l = 0; l < problem_size; l++) {

                c1 = random_vector[l];
                // DEBUG ( assert ( c1 < problem_size && c1 >= 0); )
                if (dlb_flag && dlb[c1])
                    continue;
                pos_c1 = pos[c1];
                s_c1 = tour[pos_c1 + 1];
                radius = distances[c1][s_c1];

		        /* First search for c1's nearest neighbours, use successor of c1 */
                for (h = 0; h < nn_ls; h++) {
                    c2 = nn_list[c1][h]; /* exchange partner, determine its position */
                    if (radius > distances[c1][c2]) {
                        s_c2 = tour[pos[c2] + 1];
                        //gain = -radius + distances[c1][c2] + distances[s_c1][s_c2] - distances[c2][s_c2];
                        gain = calculate_gain(c1, c2, s_c1, s_c2, tour, distances, pos);
                        if (gain < 0) {
                            System.out.printf("select -(%d->%d)=%d + (%d->%d)=%d + (%d->%d)=%d - (%d->%d)=%d\n",
                                    c1, s_c1, radius,
                                    c1, c2, distances[c1][c2],
                                    s_c1, s_c2, distances[s_c1][s_c2],
                                    c2, s_c2, distances[c2][s_c2]);
                            h1 = c1;
                            h2 = s_c1;
                            h3 = c2;
                            h4 = s_c2;
                            gotoExchange = true;
                            break;
                        }
                    } else
                        break;
                }

                if (gotoExchange) {
                    /* Search one for next c1's h-nearest neighbours, use predecessor c1 */
                    if (pos_c1 > 0)
                        p_c1 = tour[pos_c1 - 1];
                    else
                        p_c1 = tour[problem_size - 1];
                    radius = distances[p_c1][c1];
                    for (h = 0; h < nn_ls; h++) {
                        c2 = nn_list[c1][h]; /* exchange partner, determine its position */
                        if (radius > distances[c1][c2]) {
                            pos_c2 = pos[c2];
                            if (pos_c2 > 0)
                                p_c2 = tour[pos_c2 - 1];
                            else
                                p_c2 = tour[problem_size - 1];
                            if (p_c2 == c1)
                                continue;
                            if (p_c1 == c2)
                                continue;
                            //gain = -radius + distances[c1][c2] + distances[p_c1][p_c2] - distances[p_c2][c2];
                            gain = calculate_gain(p_c1, p_c2, c1, c2, tour, distances, pos);
                            if (gain < 0) {
                                System.out.printf("select -(%d->%d)=%d + (%d->%d)=%d + (%d->%d)=%d - (%d->%d)=%d\n",
                                        p_c1, c1, radius,
                                        c1, c2, distances[c1][c2],
                                        p_c1, p_c2, distances[p_c1][p_c2],
                                        p_c2, c2, distances[p_c2][c2]);
                                h1 = p_c1;
                                h2 = c1;
                                h3 = p_c2;
                                h4 = c2;
                                gotoExchange = true;
                                break;
                            }
                        } else
                            break;
                    }
                }

                if (!gotoExchange) {
                    /* No exchange */
                    dlb[c1] = true;
                    continue;
                }

                if (gotoExchange) {
                    gotoExchange = false;
                    improvement_flag = true;
                    dlb[h1] = false;
                    dlb[h2] = false;
                    dlb[h3] = false;
                    dlb[h4] = false;
                    /* Now perform move */
                    if (pos[h3] < pos[h1]) {
                        help = h1;
                        h1 = h3;
                        h3 = help;
                        help = h2;
                        h2 = h4;
                        h4 = help;
                    }
                    if (pos[h3] - pos[h2] < problem_size / 2 + 1) {
                        /* reverse inner part from pos[h2] to pos[h3] */
                        i = pos[h2];
                        j = pos[h3];
                        while (i < j) {
                            c1 = tour[i];
                            c2 = tour[j];
                            tour[i] = c2;
                            tour[j] = c1;
                            pos[c1] = j;
                            pos[c2] = i;
                            i++;
                            j--;
                        }
                    } else {
			            /* reverse outer part from pos[h4] to pos[h1] */
                        i = pos[h1];
                        j = pos[h4];
                        if (j > i)
                            help = problem_size - (j - i) + 1;
                        else
                            help = (i - j) + 1;
                        help = help / 2;
                        for (h = 0; h < help; h++) {
                            c1 = tour[i];
                            c2 = tour[j];
                            tour[i] = c2;
                            tour[j] = c1;
                            pos[c1] = j;
                            pos[c2] = i;
                            i--;
                            j++;
                            if (i < 0)
                                i = problem_size - 1;
                            if (j >= problem_size)
                                j = 0;
                        }
                        tour[problem_size] = tour[0];
                    }
                    showCost(tour, distances);
                } else {
                    dlb[c1] = true;
                }

            }
        }
    }

    static int calculate_gain(int c1, int c2, int s_c1, int s_c2, int[] tour, int[][] distances, int[] pos) {
        int curr_gain = distances[c1][s_c1] + distances[c2][s_c2];
        int new_gain = distances[c1][c2] + distances[s_c1][s_c2];
        if(s_c2 != c1 && s_c1 != c2) {
            int p_c = pos[s_c1];
            while (p_c != pos[c2]) {
                int p_n = p_c + 1;
                boolean reset = false;
                if (p_n == tour.length - 1) {
                    p_c = 0;
                    reset = true;
                }
                try {
                    curr_gain += distances[tour[p_c]][tour[p_n]];
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(!reset)
                    p_c++;
            }
            p_c = pos[c2];
            while(p_c != pos[s_c1]) {
                int p_n = p_c - 1;
                boolean reset = false;
                if(p_n == 0) {
                    p_c = tour.length - 1;
                    reset = true;
                }
                new_gain += distances[tour[p_c]][tour[p_n]];
                if(!reset)
                    p_c--;
            }
        }
        return new_gain - curr_gain;
    }

}
