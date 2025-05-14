import java.awt.*;
import java.util.ArrayList;
import Players.Player;
import Market.Markets;

public abstract class BaseRoom {
    protected ArrayList<Rectangle> solidObjects;
    protected ArrayList<Rectangle> doors;
    protected ArrayList<Markets> markets;
    private int width, height;

    public BaseRoom(int width, int height) {
        this.width = width;
        this.height = height;
        solidObjects = new ArrayList<>();
        doors = new ArrayList<>();
        markets = new ArrayList<>();
    }

    public void draw(Graphics g, int camX, int camY) {
        g.setColor(Color.GRAY);
        for (Rectangle rect : solidObjects) {
            g.fillRect(rect.x - camX, rect.y - camY, rect.width, rect.height);
        }
        g.setColor(Color.GREEN);
        for (Rectangle rect : doors) {
            g.fillRect(rect.x - camX, rect.y - camY, rect.width, rect.height);
        }
        for (Markets market : markets) {
            market.draw(g, camX, camY);
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public ArrayList<Rectangle> getSolidObjects() {
        return solidObjects;
    }

    public ArrayList<Rectangle> getDoors() {
        return doors;
    }

    public ArrayList<Markets> getMarkets() {
        return markets;
    }

    public boolean checkDoorCollision(Player player) {
        Rectangle playerBounds = new Rectangle(player.getX(), player.getY(), player.getSize(), player.getSize());
        for (Rectangle door : doors) {
            if (playerBounds.intersects(door)) {
                return true;
            }
        }
        return false;
    }

    public Markets checkMarketCollision(Player player) {
        Rectangle playerBounds = new Rectangle(player.getX(), player.getY(), player.getSize(), player.getSize());
        for (Markets market : markets) {
            if (playerBounds.intersects(market.getBounds())) {
                return market;
            }
        }
        return null;
    }

    public abstract int getSpawnX();
    public abstract int getSpawnY();
}