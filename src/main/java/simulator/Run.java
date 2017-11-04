package simulator;

import simulator.graph.Graph;
import simulator.graph.Node;
import simulator.mmas.RouteSolver;
import simulator.reader.JSONConverter;
import simulator.utils.DynamicGenerator;
import simulator.utils.GenericStatistics;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Run {

    public static void main(String[] args) throws Exception {
        runSimulation(false, 0.75, 500, true, 4, 46);
        runSimulation(true, 0.75, 500, true, 4, 46);
        runSimulation(false, 0.75, 500, true, 4, 78);
        runSimulation(true, 0.75, 500, true, 4, 78);
        runSimulation(false, 0.75, 500, true, 4, 125);
        runSimulation(true, 0.75, 500, true, 4, 125);

        runSimulation(false, 0.5, 500, true, 4, 46);
        runSimulation(true, 0.5, 500, true, 4, 46);
        runSimulation(false, 0.5, 500, true, 4, 78);
        runSimulation(true, 0.5, 500, true, 4, 78);
        runSimulation(false, 0.5, 500, true, 4, 125);
        runSimulation(true, 0.5, 500, true, 4, 125);

        runSimulation(false, 0.1, 500, true, 4, 46);
        runSimulation(true, 0.1, 500, true, 4, 46);
        runSimulation(false, 0.1, 500, true, 4, 78);
        runSimulation(true, 0.1, 500, true, 4, 78);
        runSimulation(false, 0.1, 500, true, 4, 125);
        runSimulation(true, 0.1, 500, true, 4, 125);

        runSimulation(false, 0.75, 1000, true, 4, 46);
        runSimulation(true, 0.75, 1000, true, 4, 46);
        runSimulation(false, 0.75, 1000, true, 4, 78);
        runSimulation(true, 0.75, 1000, true, 4, 78);
        runSimulation(false, 0.75, 1000, true, 4, 125);
        runSimulation(true, 0.75, 1000, true, 4, 125);

        runSimulation(false, 0.5, 1000, true, 4, 46);
        runSimulation(true, 0.5, 1000, true, 4, 46);
        runSimulation(false, 0.5, 1000, true, 4, 78);
        runSimulation(true, 0.5, 1000, true, 4, 78);
        runSimulation(false, 0.5, 1000, true, 4, 125);
        runSimulation(true, 0.5, 1000, true, 4, 125);

        runSimulation(false, 0.1, 1000, true, 4, 46);
        runSimulation(true, 0.1, 1000, true, 4, 46);
        runSimulation(false, 0.1, 1000, true, 4, 78);
        runSimulation(true, 0.1, 1000, true, 4, 78);
        runSimulation(false, 0.1, 1000, true, 4, 125);
        runSimulation(true, 0.1, 1000, true, 4, 125);

    }

    public static void runSimulation(boolean useMemory, double magnitude, int frequency, boolean cycle, int periodLimit, int tspSize) {

        int trialSize = 30;
        int iterationSize = 5000;
        double defaultSpeed = 1000.0;

//        boolean useMemory = false;
//        double magnitude = 0.75;
//        int frequency = 500;
//        boolean cycle = true;
//        int periodLimit = 8;
//        int tspSize = 125;
        double lowerBound = defaultSpeed * 0.3;
        double upperBound = defaultSpeed * 1.7;

        String fileName = "MMAS" +
                "_MEM-" + (useMemory ? "T" : "F") +
                "_TSP-" + tspSize +
                "_MAG-" + magnitude +
                "_FREQ-" + frequency +
                "_PERIOD-" + periodLimit;
        System.out.println("Analysis = " + fileName);
        GenericStatistics genericStatistics = new GenericStatistics(iterationSize, trialSize, fileName);
        for(int t = 0; t < trialSize; t++) {

            System.out.println("Trial = " + t);
            ClassLoader classLoader = Run.class.getClassLoader();
            String jsonFile = (new File("maps")).getAbsolutePath() + "/joinville.json";
            Graph graph = JSONConverter.readGraph(jsonFile);
            graph.setDefaultSpeed(defaultSpeed);

            List<Node> targets = new ArrayList<>();
            if(tspSize == 46)
                tsp46(graph, targets);
            if(tspSize == 78)
                tsp78(graph, targets);
            if(tspSize == 125)
                tsp125(graph, targets);


            RouteSolver routeSolver = new RouteSolver(graph, graph.getNode(553), targets, t, genericStatistics, useMemory);
//            Simulator simulator = new Simulator(graph, graph.getNode(553), targets, routeSolver);
            DynamicGenerator dynamicGenerator = new DynamicGenerator(graph, magnitude, frequency, lowerBound, upperBound);
            dynamicGenerator.setDynamicListener(routeSolver);
            dynamicGenerator.setCycle(cycle, periodLimit);
            //simulator.start();
            //routeSolver.start();
            //dynamicGenerator.start();

//            simulator.setup();
            routeSolver.setup();
            for (int i = 1; i < iterationSize; i++) {
                dynamicGenerator.loop(i);
                routeSolver.loop(i);
//                simulator.loop(i);
                if(i % 1000 == 0) {
                    System.out.println("Iteration = " + i);
                }
            }
        }
        genericStatistics.dispose();
        System.out.println("Finished");
        System.gc();
    }

    private static void tsp46(Graph graph, List<Node> targets) {
        targets.add(graph.getNode(553));
        targets.add(graph.getNode(1201));
        targets.add(graph.getNode(43));
        targets.add(graph.getNode(171));
        targets.add(graph.getNode(103));
        targets.add(graph.getNode(1980));
        targets.add(graph.getNode(155));
        targets.add(graph.getNode(2336));
        targets.add(graph.getNode(420));
        targets.add(graph.getNode(310));
        targets.add(graph.getNode(1500));
        targets.add(graph.getNode(2100));
        targets.add(graph.getNode(16));
        targets.add(graph.getNode(720));
        targets.add(graph.getNode(1700));
        //
        targets.add(graph.getNode(1414));
        targets.add(graph.getNode(500));
        targets.add(graph.getNode(113));
        targets.add(graph.getNode(1800));
        targets.add(graph.getNode(1920));
        targets.add(graph.getNode(250));
        targets.add(graph.getNode(125));
        targets.add(graph.getNode(456));
        targets.add(graph.getNode(1285));
        targets.add(graph.getNode(2232));
        targets.add(graph.getNode(209));
        targets.add(graph.getNode(543));
        targets.add(graph.getNode(876));
        targets.add(graph.getNode(905));
        targets.add(graph.getNode(120));
        targets.add(graph.getNode(341));
        targets.add(graph.getNode(1863));
        targets.add(graph.getNode(468));
        targets.add(graph.getNode(1025));
        targets.add(graph.getNode(1902));
        targets.add(graph.getNode(1374));
        targets.add(graph.getNode(2001));
        targets.add(graph.getNode(1572));
        targets.add(graph.getNode(1932));
        targets.add(graph.getNode(1233));
        targets.add(graph.getNode(1356));
        targets.add(graph.getNode(621));
        targets.add(graph.getNode(217));
        targets.add(graph.getNode(842));
        targets.add(graph.getNode(183));
        targets.add(graph.getNode(474));
        targets.add(graph.getNode(4));
    }

    private static void tsp78(Graph graph, List<Node> targets) {
        targets.add(graph.getNode(553));
        targets.add(graph.getNode(1201));
        targets.add(graph.getNode(43));
        targets.add(graph.getNode(171));
        targets.add(graph.getNode(103));
        targets.add(graph.getNode(1980));
        targets.add(graph.getNode(155));
        targets.add(graph.getNode(2336));
        targets.add(graph.getNode(420));
        targets.add(graph.getNode(310));
        targets.add(graph.getNode(1500));
        targets.add(graph.getNode(2100));
        targets.add(graph.getNode(16));
        targets.add(graph.getNode(720));
        targets.add(graph.getNode(1700));
        //
        targets.add(graph.getNode(1414));
        targets.add(graph.getNode(500));
        targets.add(graph.getNode(113));
        targets.add(graph.getNode(1800));
        targets.add(graph.getNode(1920));
        targets.add(graph.getNode(250));
        targets.add(graph.getNode(125));
        targets.add(graph.getNode(456));
        targets.add(graph.getNode(1285));
        targets.add(graph.getNode(2232));
        targets.add(graph.getNode(209));
        targets.add(graph.getNode(543));
        targets.add(graph.getNode(876));
        targets.add(graph.getNode(905));
        targets.add(graph.getNode(120));
        targets.add(graph.getNode(341));
        targets.add(graph.getNode(1863));
        targets.add(graph.getNode(468));
        targets.add(graph.getNode(1025));
        targets.add(graph.getNode(1902));
        targets.add(graph.getNode(1374));
        targets.add(graph.getNode(2001));
        targets.add(graph.getNode(1572));
        targets.add(graph.getNode(1932));
        targets.add(graph.getNode(1233));
        targets.add(graph.getNode(1356));
        targets.add(graph.getNode(621));
        targets.add(graph.getNode(217));
        targets.add(graph.getNode(842));
        targets.add(graph.getNode(183));
        targets.add(graph.getNode(474));
        targets.add(graph.getNode(4));
        //
        targets.add(graph.getNode(1230));
        targets.add(graph.getNode(520));
        targets.add(graph.getNode(333));
        targets.add(graph.getNode(315));
        targets.add(graph.getNode(610));
        targets.add(graph.getNode(1396));
        targets.add(graph.getNode(900));
        targets.add(graph.getNode(2346));
        targets.add(graph.getNode(1235));
        targets.add(graph.getNode(102));
        targets.add(graph.getNode(752));
        targets.add(graph.getNode(246));
        targets.add(graph.getNode(985));
        targets.add(graph.getNode(2032));
        targets.add(graph.getNode(1203));
        targets.add(graph.getNode(1750));
        targets.add(graph.getNode(302));
        targets.add(graph.getNode(564));
        targets.add(graph.getNode(795));
        targets.add(graph.getNode(1865));
        targets.add(graph.getNode(1132));
        targets.add(graph.getNode(1436));
        targets.add(graph.getNode(935));
        targets.add(graph.getNode(1289));
        targets.add(graph.getNode(678));
        targets.add(graph.getNode(712));
        targets.add(graph.getNode(1956));
        targets.add(graph.getNode(650));
        targets.add(graph.getNode(1550));
        targets.add(graph.getNode(25));
        targets.add(graph.getNode(489));
        targets.add(graph.getNode(368));
    }

    private static void tsp125(Graph graph, List<Node> targets) {
        targets.add(graph.getNode(553));
        targets.add(graph.getNode(1201));
        targets.add(graph.getNode(43));
        targets.add(graph.getNode(171));
        targets.add(graph.getNode(103));
        targets.add(graph.getNode(1980));
        targets.add(graph.getNode(155));
        targets.add(graph.getNode(2336));
        targets.add(graph.getNode(420));
        targets.add(graph.getNode(310));
        targets.add(graph.getNode(1500));
        targets.add(graph.getNode(2100));
        targets.add(graph.getNode(16));
        targets.add(graph.getNode(720));
        targets.add(graph.getNode(1700));
        targets.add(graph.getNode(1414));
        targets.add(graph.getNode(500));
        targets.add(graph.getNode(113));
        targets.add(graph.getNode(1800));
        targets.add(graph.getNode(1920));
        targets.add(graph.getNode(250));
        targets.add(graph.getNode(125));
        targets.add(graph.getNode(456));
        targets.add(graph.getNode(1285));
        targets.add(graph.getNode(2232));
        targets.add(graph.getNode(209));
        targets.add(graph.getNode(543));
        targets.add(graph.getNode(876));
        targets.add(graph.getNode(905));
        targets.add(graph.getNode(120));
        targets.add(graph.getNode(341));
        targets.add(graph.getNode(1863));
        targets.add(graph.getNode(468));
        targets.add(graph.getNode(1025));
        targets.add(graph.getNode(1902));
        targets.add(graph.getNode(1374));
        targets.add(graph.getNode(2001));
        targets.add(graph.getNode(1572));
        targets.add(graph.getNode(1932));
        targets.add(graph.getNode(1233));
        targets.add(graph.getNode(1356));
        targets.add(graph.getNode(621));
        targets.add(graph.getNode(217));
        targets.add(graph.getNode(842));
        targets.add(graph.getNode(183));
        targets.add(graph.getNode(474));
        targets.add(graph.getNode(4));
        targets.add(graph.getNode(1230));
        targets.add(graph.getNode(520));
        targets.add(graph.getNode(333));
        targets.add(graph.getNode(315));
        targets.add(graph.getNode(610));
        targets.add(graph.getNode(1396));
        targets.add(graph.getNode(900));
        targets.add(graph.getNode(2346));
        targets.add(graph.getNode(1235));
        targets.add(graph.getNode(102));
        targets.add(graph.getNode(752));
        targets.add(graph.getNode(246));
        targets.add(graph.getNode(985));
        targets.add(graph.getNode(2032));
        targets.add(graph.getNode(1203));
        targets.add(graph.getNode(1750));
        targets.add(graph.getNode(302));
        targets.add(graph.getNode(564));
        targets.add(graph.getNode(795));
        targets.add(graph.getNode(1865));
        targets.add(graph.getNode(1132));
        targets.add(graph.getNode(1436));
        targets.add(graph.getNode(935));
        targets.add(graph.getNode(1289));
        targets.add(graph.getNode(678));
        targets.add(graph.getNode(712));
        targets.add(graph.getNode(1956));
        targets.add(graph.getNode(650));
        targets.add(graph.getNode(1550));
        targets.add(graph.getNode(25));
        targets.add(graph.getNode(489));
        targets.add(graph.getNode(368));
        targets.add(graph.getNode(498));
        targets.add(graph.getNode(484));
        targets.add(graph.getNode(1262));
        targets.add(graph.getNode(1435));
        targets.add(graph.getNode(634));
        targets.add(graph.getNode(245));
        targets.add(graph.getNode(111));
        targets.add(graph.getNode(150));
        targets.add(graph.getNode(222));
        targets.add(graph.getNode(262));
        targets.add(graph.getNode(444));
        targets.add(graph.getNode(734));
        targets.add(graph.getNode(555));
        targets.add(graph.getNode(517));
        targets.add(graph.getNode(666));
        targets.add(graph.getNode(618));
        targets.add(graph.getNode(777));
        targets.add(graph.getNode(754));
        targets.add(graph.getNode(888));
        targets.add(graph.getNode(1842));
        targets.add(graph.getNode(961));
        targets.add(graph.getNode(1011));
        targets.add(graph.getNode(1187));
        targets.add(graph.getNode(1111));
        targets.add(graph.getNode(1222));
        targets.add(graph.getNode(1077));
        targets.add(graph.getNode(1333));
        targets.add(graph.getNode(1544));
        targets.add(graph.getNode(1444));
        targets.add(graph.getNode(1263));
        targets.add(graph.getNode(1555));
        targets.add(graph.getNode(1388));
        targets.add(graph.getNode(1666));
        targets.add(graph.getNode(1314));
        targets.add(graph.getNode(1777));
        targets.add(graph.getNode(1442));
        targets.add(graph.getNode(1888));
        targets.add(graph.getNode(1718));
        targets.add(graph.getNode(1999));
        targets.add(graph.getNode(1200));
        targets.add(graph.getNode(1300));
        targets.add(graph.getNode(1400));
        targets.add(graph.getNode(1501));
        targets.add(graph.getNode(1600));
        targets.add(graph.getNode(2000));
        targets.add(graph.getNode(2101));
    }

}
