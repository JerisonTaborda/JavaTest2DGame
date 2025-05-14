package Players;

import Item.GameItemRegistry;
import Item.Item;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Player {
    private int x, y, size = 50, speed = 5;
    private boolean up, down, left, right;
    private double health = 10.0, hunger = 0.0, thirst = 0.0; // Hunger/thirst start at 0
    private Map<String, Integer> inventory;
    private long lastHealthDecrease, lastHungerIncrease, lastThirstIncrease;

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
        inventory = new HashMap<>();
        addItem("gold_coin", 30); // Initial 30 gold coins
        addItem("apple", 5);      // Test: 5 Apples
        addItem("water", 3);      // Test: 3 Water
        lastHealthDecrease = System.currentTimeMillis();
        lastHungerIncrease = System.currentTimeMillis();
        lastThirstIncrease = System.currentTimeMillis();
    }

    public void update(java.util.List<Rectangle> solidObjects, int roomWidth, int roomHeight) {
        int newX = x, newY = y;
        if (up) newY -= speed;
        if (down) newY += speed;
        if (left) newX -= speed;
        if (right) newX += speed;

        Rectangle newBounds = new Rectangle(newX, newY, size, size);
        boolean collision = false;
        for (Rectangle rect : solidObjects) {
            if (newBounds.intersects(rect)) {
                collision = true;
                break;
            }
        }

        if (!collision && newX >= 0 && newX + size <= roomWidth && newY >= 0 && newY + size <= roomHeight) {
            x = newX;
            y = newY;
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastHungerIncrease >= 10000) { // +0.1 hunger every 10s
            hunger = Math.min(10.0, hunger + 0.1);
            lastHungerIncrease = currentTime;
        }
        if (currentTime - lastThirstIncrease >= 5000) { // +0.1 thirst every 5s
            thirst = Math.min(10.0, thirst + 0.1);
            lastThirstIncrease = currentTime;
        }
        if (currentTime - lastHealthDecrease >= 5000) { // Health check every 5s
            if (hunger >= 10.0 && thirst >= 10.0) {
                health = Math.max(0, health - 1.0); // -1.0 if both maxed
            } else if (hunger >= 10.0 || thirst >= 10.0) {
                health = Math.max(0, health - 0.1); // -0.1 if either maxed
            }
            lastHealthDecrease = currentTime;
        }
    }

    public void draw(Graphics g, int camX, int camY) {
        g.setColor(Color.RED);
        g.fillRect(x - camX, y - camY, size, size);
    }

    public void drawStats(Graphics g) {
        // Health bar
        g.setColor(Color.RED);
        g.fillRect(10, 20, (int) (100 * (health / 10.0)), 10);
        g.setColor(Color.BLACK);
        g.drawRect(10, 20, 100, 10);
        // Hunger bar
        g.setColor(Color.YELLOW);
        g.fillRect(10, 40, (int) (100 * (hunger / 10.0)), 10);
        g.setColor(Color.BLACK);
        g.drawRect(10, 40, 100, 10);
        // Thirst bar
        g.setColor(Color.BLUE);
        g.fillRect(10, 60, (int) (100 * (thirst / 10.0)), 10);
        g.setColor(Color.BLACK);
        g.drawRect(10, 60, 100, 10);
        // Labels
        g.setColor(Color.WHITE);
        g.drawString("\u2764 " + String.format("%.2f", health), 120, 30);
        g.drawString("\uD83C\uDF56 " + String.format("%.2f", hunger), 120, 50);
        g.drawString("\uD83D\uDCA7 " + String.format("%.2f", thirst), 120, 70);
    }

    public boolean addItem(String itemId, int quantity) {
        Item item = GameItemRegistry.getItem(itemId);
        if (item == null || !item.isStackable() && inventory.containsKey(item.getName())) {
            return false;
        }
        inventory.put(item.getName(), inventory.getOrDefault(item.getName(), 0) + quantity);
        return true;
    }

    public boolean removeItem(String itemId, int quantity) {
        Item item = GameItemRegistry.getItem(itemId);
        if (item == null || !inventory.containsKey(item.getName())) {
            return false;
        }
        int current = inventory.get(item.getName());
        if (current < quantity) {
            return false;
        }
        if (current == quantity) {
            inventory.remove(item.getName());
        } else {
            inventory.put(item.getName(), current - quantity);
        }
        return true;
    }

    public void useItem(Item item) {
        if (inventory.containsKey(item.getName())) {
            item.useEffect(this);
            removeItem(item.getId(), 1);
        }
    }

    public void decreaseHunger(float amount) {
        hunger = Math.max(0.0, hunger - amount);
    }

    public void decreaseThirst(float amount) {
        thirst = Math.max(0.0, thirst - amount);
    }

    public void equipItemToHotbar(int slot, Item item) {
        // Placeholder for hotbar equipping
    }

    public Map<String, Integer> getInventory() {
        return inventory;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getSize() {
        return size;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setUp(boolean up) {
        this.up = up;
    }

    public void setDown(boolean down) {
        this.down = down;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public void setRight(boolean right) {
        this.right = right;
    }
}