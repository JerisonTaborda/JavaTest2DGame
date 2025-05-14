package Market;

import Item.GameItemRegistry;
import Item.Item;
import Players.Player;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Markets {
    private int x, y, width, height;
    private Map<String, Integer> itemsForSale; // Item ID to price
    private Map<String, Integer> itemsToBuy;   // Item ID to buy price

    public Markets(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.itemsForSale = new HashMap<>();
        this.itemsToBuy = new HashMap<>();
    }

    public void addItemForSale(String itemId, int price) {
        Item item = GameItemRegistry.getItem(itemId);
        if (item != null) {
            itemsForSale.put(itemId, price);
            itemsToBuy.put(itemId, (int) (price * 0.8)); // Buy at 80% of sell price
        }
    }

    public boolean buyItem(Player player, String itemId, int quantity) {
        Integer price = itemsForSale.get(itemId);
        Item item = GameItemRegistry.getItem(itemId);
        if (price == null || item == null || quantity <= 0) return false;
        int totalCost = price * quantity;
        if (player.removeItem("Gold Coin", totalCost) && player.addItem(item.getName(), quantity)) {
            return true;
        }
        // Roll back if addItem fails
        if (player.removeItem("Gold Coin", totalCost)) {
            player.addItem("Gold Coin", totalCost);
        }
        return false;
    }

    public boolean sellItem(Player player, String itemId, int quantity) {
        Integer price = itemsToBuy.get(itemId);
        Item item = GameItemRegistry.getItem(itemId);
        if (price == null || item == null || quantity <= 0) return false;
        int totalGain = price * quantity;
        if (player.removeItem(item.getName(), quantity)) {
            player.addItem("Gold Coin", totalGain);
            return true;
        }
        return false;
    }

    public void draw(Graphics g, int camX, int camY) {
        g.setColor(Color.YELLOW);
        g.fillRect(x - camX, y - camY, width, height);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public Map<String, Integer> getItemsForSale() {
        return itemsForSale;
    }

    public Map<String, Integer> getItemsToBuy() {
        return itemsToBuy;
    }
}