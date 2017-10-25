package simulator.graph;

public class Edge {

    private Node from;

    private Node to;

    private double distance; //m

    private double timeSeconds; //s

    private double speed; //m/s

    private Double originalSpeed = null;

    public Edge() {
        super();
    }

    public Node getFrom() {
        return from;
    }

    public void setFrom(Node from) {
        this.from = from;
    }

    public Node getTo() {
        return to;
    }

    public void setTo(Node to) {
        this.to = to;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public Double getTimeSeconds() {
        return timeSeconds;
    }

    public void setTimeSeconds(Double timeSeconds) {
        this.timeSeconds = timeSeconds;
    }

    public double getSpeed() {
        return speed;
    }

    public void setOriginalSpeed(double speed) {
        this.originalSpeed = speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
        timeSeconds = (distance / speed) * 1000;

    }

}
