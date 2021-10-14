package application.model;

import java.util.ArrayList;

public class Player {
    private final String name;
    private Vertex source;
    private final ArrayList<Bot> bots;
    private double area;

    public Player(String name, Vertex startVertex) {
        this.name = name;
        this.bots = new ArrayList<>();
        this.bots.add(new Bot(startVertex));
        this.bots.add(new Bot(startVertex));
        this.source = startVertex.copy();
        this.area = 0;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Bot> getBots() {
        return bots;
    }

    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }

    public void setSource(Vertex source) {
        this.source = source;
    }

    public Vertex getSource() {
        return this.source;
    }

    public boolean formsClosedLoop() {
        if (bots.get(0).getVisitedVertices().size() + bots.get(1).getVisitedVertices().size() >= 2) {
            return bots.get(0).equals(bots.get(1));
        } else {
            return false;
        }
    }

    public void closeLoop() {
        if (formsClosedLoop()) {
            this.source = bots.get(0).toVertex();
            for (Bot bot : bots) {
                bot.reset();
                bot.setSource(this.source);
            }
        }
    }
}
