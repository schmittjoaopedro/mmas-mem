package simulator.graph;

import java.util.*;

public class Dijkstra {


    public static void computePaths(Node source) {
        source.setMinDistance(0.0);
        PriorityQueue<Node> vertexQueue = new PriorityQueue<Node>();
        vertexQueue.add(source);
        while (!vertexQueue.isEmpty()) {
            Node u = vertexQueue.poll();
            for (Edge e : u.getEdges()) {
                Node v = e.getTo();
                double weight = e.getDistance();
                double distanceThroughU = u.getMinDistance() + weight;
                if (distanceThroughU < v.getMinDistance()) {
                    vertexQueue.remove(v);

                    v.setMinDistance(distanceThroughU);
                    v.setPrevious(u);
                    vertexQueue.add(v);
                }
            }
        }
    }

    public List<Node> getShortestPathTo(Node target) {
        List<Node> path = new ArrayList<Node>();
        for (Node vertex = target; vertex != null; vertex = vertex.getPrevious())
            path.add(vertex);

        Collections.reverse(path);
        return path;
    }

    public int[] execute(Node from, Node to) {
        computePaths(from);
        List<Node> path = getShortestPathTo(to);
        int[] route = new int[path.size()];
        for(int i = 0; i < path.size(); i++) {
            route[i] = path.get(i).getId();
        }
        return route;
    }

}
