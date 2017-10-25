package simulator;

import simulator.graph.Graph;
import simulator.graph.Node;
import simulator.mmas.RouteSolver;
import simulator.reader.JSONConverter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Run {

    public static void main(String[] args) throws Exception {

        ClassLoader classLoader = Run.class.getClassLoader();
        String jsonFile = (new File("maps")).getAbsolutePath() + "/joinville.json";
        Graph graph = JSONConverter.readGraph(jsonFile);
        graph.setDefaultSpeed(125.0);

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

        RouteSolver routeSolver = new RouteSolver(graph, graph.getNode(553), targets);
        Simulator simulator = new Simulator(graph, graph.getNode(553), targets, routeSolver);
        simulator.start();
        routeSolver.start();
    }

}
