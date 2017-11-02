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

        int trialSize = 3;
        int iterationSize = 100;
        GenericStatistics genericStatistics = new GenericStatistics(iterationSize, trialSize);
        for(int t = 0; t < trialSize; t++) {

            System.out.println("Trial = " + t);
            double defaultSpeed = 1000.0;
            ClassLoader classLoader = Run.class.getClassLoader();
            String jsonFile = (new File("maps")).getAbsolutePath() + "/joinville.json";
            Graph graph = JSONConverter.readGraph(jsonFile);
            graph.setDefaultSpeed(defaultSpeed);

            List<Node> targets = new ArrayList<>();
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

            RouteSolver routeSolver = new RouteSolver(graph, graph.getNode(553), targets, t, genericStatistics);
            Simulator simulator = new Simulator(graph, graph.getNode(553), targets, routeSolver);
            DynamicGenerator dynamicGenerator = new DynamicGenerator(graph, 0.75, 1000, defaultSpeed * 0.3, defaultSpeed * 1.7);
            dynamicGenerator.setDynamicListener(routeSolver);
            //simulator.start();
            //routeSolver.start();
            //dynamicGenerator.start();

            routeSolver.setup();
            simulator.setup();
            for (int i = 0; i < iterationSize; i++) {
                dynamicGenerator.loop(i);
                routeSolver.loop(i);
                simulator.loop(i);
            }
        }
        genericStatistics.dispose();
        System.out.println("Finished");
    }

}
