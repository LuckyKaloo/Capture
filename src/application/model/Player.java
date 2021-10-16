package application.model;

public class Player  {
    private static int numPlayers = 0;

    private final String name;
    private final int id;
    private Vertex source;
    private final Bot bot1;
    private final Bot bot2;
    private final Bot[] bots;
    private Bot selectedBot;
    private double area;

    public Player(String name, Vertex startVertex) {
        this.name = name;
        this.bot1 = new Bot(startVertex);
        this.bot2 = new Bot(startVertex);
        this.selectedBot = bot1;
        this.bots = new Bot[]{bot1, bot2};

        this.id = numPlayers;
        numPlayers++;

        this.source = startVertex;
        this.area = 0;
    }

    public String getName() {
        return this.name;
    }

    public int getId() {
        return this.id;
    }

    public double getArea() {
        return this.area;
    }

    public void setArea(double area) {
        this.area = area;
    }

    public Vertex getSource() {
        return this.source;
    }

    public Bot[] getBots() {
        return this.bots;
    }

    public void changeBot() {
        if (this.selectedBot == this.bot1) {
            this.selectedBot = this.bot2;
        } else if (this.selectedBot == this.bot2) {
            this.selectedBot = this.bot1;
        }
    }

    public Bot getSelectedBot() {
        return this.selectedBot;
    }

    public boolean formsClosedLoop() {
        if (bot1.getVisitedVertices().size() + bot2.getVisitedVertices().size() >= 2) {
            return bot1.equals(bot2);
        } else {
            return false;
        }
    }

    public void closeLoop() {
        if (formsClosedLoop()) {
            this.source = bot1.toVertex();
            bot1.reset();
            bot1.setSource(this.source);
            bot2.reset();
            bot2.setSource(this.source);
        }
    }
}
