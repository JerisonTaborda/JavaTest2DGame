package Players.Inventory;

import Item.Item;
import java.awt.*;

public class InventorySlot {
    private int x, y, size;
    private Item item;
    private int quantity;

    public InventorySlot(int x, int y, int size) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.item = null;
        this.quantity = 0;
    }

    public void draw(Graphics g) {
        g.setColor(Color.GRAY);
        g.fillRect(x, y, size, size);
        g.setColor(Color.BLACK);
        g.drawRect(x, y, size, size);
    }

    public boolean containsPoint(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + size && mouseY >= y && mouseY <= y + size;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item, int quantity) {
        this.item = item;
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void clear() {
        this.item = null;
        this.quantity = 0;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}