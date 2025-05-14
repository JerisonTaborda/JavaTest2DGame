import Item.GameItemRegistry;
import Item.Item;
import Market.Markets;
import Players.Inventory.InventorySlot;
import Players.Player;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.*;

public class GamePanel extends JPanel implements KeyListener, MouseListener, MouseMotionListener {
    private Player player;
    private boolean isInventoryOpen = false;
    private boolean isMarketOpen = false;
    private Markets currentMarket = null;
    private ArrayList<InventorySlot> hotbarSlots = new ArrayList<>();
    private ArrayList<InventorySlot> inventorySlots = new ArrayList<>();
    private final int SLOT_SIZE = 50;
    private final int SLOT_GAP = 5;
    private final int HOTBAR_Y_POSITION = 500;
    private final int INVENTORY_Y_POSITION = 150;
    private BaseRoom currentRoom;
    private InventorySlot hoveredSlot = null;
    private InventorySlot draggedSlot = null;
    private Item draggedItem = null;
    private int draggedQuantity = 0;
    private Point dragOffset = new Point(0, 0);

    public GamePanel(Player player) {
        this.player = player;
        this.currentRoom = new SpawnRoom();
        setPreferredSize(new Dimension(800, 600));
        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
        setFocusable(true);
        requestFocusInWindow();
        initializeInventory();
        startGameLoop();
    }

    private void initializeInventory() {
        int hotbarStartX = (800 - (5 * SLOT_SIZE + 4 * SLOT_GAP)) / 2; // 265
        for (int i = 0; i < 5; i++) {
            int x = hotbarStartX + i * (SLOT_SIZE + SLOT_GAP);
            hotbarSlots.add(new InventorySlot(x, HOTBAR_Y_POSITION, SLOT_SIZE));
        }
        int inventoryStartX = (800 - (6 * SLOT_SIZE + 5 * SLOT_GAP)) / 2; // 238
        for (int i = 0; i < 30; i++) {
            int x = inventoryStartX + (i % 6) * (SLOT_SIZE + SLOT_GAP);
            int y = INVENTORY_Y_POSITION + (i / 6) * (SLOT_SIZE + SLOT_GAP);
            inventorySlots.add(new InventorySlot(x, y, SLOT_SIZE));
        }
        updateInventorySlots();
    }

    private void updateInventorySlots() {
        for (InventorySlot slot : inventorySlots) {
            slot.clear();
        }
        int slotIndex = 0;
        for (Map.Entry<String, Integer> entry : player.getInventory().entrySet()) {
            if (slotIndex >= inventorySlots.size()) break;
            Item item = GameItemRegistry.getItem(entry.getKey().toLowerCase());
            if (item != null) {
                inventorySlots.get(slotIndex).setItem(item, entry.getValue());
                slotIndex++;
            }
        }
    }

    private void startGameLoop() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                player.update(currentRoom.getSolidObjects(), currentRoom.getWidth(), currentRoom.getHeight());
                repaint();
            }
        }, 0, 1000 / 60);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        int camX = Math.max(0, Math.min(player.getX() - getWidth() / 2, currentRoom.getWidth() - getWidth()));
        int camY = Math.max(0, Math.min(player.getY() - getHeight() / 2, currentRoom.getHeight() - getHeight()));

        currentRoom.draw(g, camX, camY);
        player.draw(g, camX, camY);
        player.drawStats(g);

        drawHotbar(g);
        if (isInventoryOpen) {
            drawInventory(g);
        }
        if (isMarketOpen && currentMarket != null) {
            drawMarket(g);
        }

        if (draggedItem != null) {
            g.setColor(Color.GRAY);
            g.fillRect(dragOffset.x, dragOffset.y, SLOT_SIZE, SLOT_SIZE);
            g.setColor(Color.BLACK);
            g.drawRect(dragOffset.x, dragOffset.y, SLOT_SIZE, SLOT_SIZE);
            g.setColor(Color.WHITE);
            g.drawString(draggedItem.getName() + ": " + draggedQuantity, dragOffset.x + 5, dragOffset.y + 20);
        }

        if (hoveredSlot != null && hoveredSlot.getItem() != null) {
            g.setColor(Color.WHITE);
            g.drawString(hoveredSlot.getItem().getName(), hoveredSlot.getX(), hoveredSlot.getY() - 5);
        }
    }

    private void drawHotbar(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        Font originalFont = g2.getFont();
        g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));

        for (InventorySlot slot : hotbarSlots) {
            slot.draw(g);
            if (slot.getItem() != null) {
                g.setColor(Color.WHITE);
                g.drawString(slot.getItem().getName() + ": " + slot.getQuantity(), slot.getX() + 5, slot.getY() + 20);
            }
        }

        g2.setFont(originalFont);
    }

    private void drawInventory(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        Font originalFont = g2.getFont();
        g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));

        for (InventorySlot slot : inventorySlots) {
            slot.draw(g);
            if (slot.getItem() != null) {
                g.setColor(Color.WHITE);
                g.drawString(slot.getItem().getName() + ": " + slot.getQuantity(), slot.getX() + 5, slot.getY() + 20);
            }
        }

        g2.setFont(originalFont);
    }

    private void drawMarket(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        Font originalFont = g2.getFont();
        g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));

        g.setColor(Color.DARK_GRAY);
        g.fillRect(200, 200, 400, 300);
        g.setColor(Color.WHITE);
        g.drawString("Market (1-9 to buy, Shift+1-9 to sell)", 210, 220);
        int gold = player.getInventory().getOrDefault("Gold Coin", 0);
        g.drawString("Your Gold: " + gold, 210, 240);
        int y = 260;
        int index = 1;
        for (Map.Entry<String, Integer> entry : currentMarket.getItemsForSale().entrySet()) {
            String itemId = entry.getKey();
            Item item = GameItemRegistry.getItem(itemId);
            if (item == null) continue;
            String itemName = item.getName();
            int sellPrice = entry.getValue();
            int buyPrice = currentMarket.getItemsToBuy().getOrDefault(itemId, 0);
            g.drawString(index + ": Buy " + itemName + " (" + sellPrice + " gold), Sell (" + buyPrice + " gold)", 210, y);
            y += 20;
            index++;
            if (index > 9) break;
        }

        g2.setFont(originalFont);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_E) {
            isInventoryOpen = !isInventoryOpen;
            if (isInventoryOpen) isMarketOpen = false;
            updateInventorySlots();
        }
        if (e.getKeyCode() == KeyEvent.VK_F) {
            Markets market = currentRoom.checkMarketCollision(player);
            if (market != null) {
                isMarketOpen = !isMarketOpen;
                currentMarket = isMarketOpen ? market : null;
                if (isMarketOpen) isInventoryOpen = false;
                updateInventorySlots();
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_W) {
            player.setUp(true);
        }
        if (e.getKeyCode() == KeyEvent.VK_S) {
            player.setDown(true);
        }
        if (e.getKeyCode() == KeyEvent.VK_A) {
            player.setLeft(true);
        }
        if (e.getKeyCode() == KeyEvent.VK_D) {
            player.setRight(true);
        }
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (currentRoom.checkDoorCollision(player)) {
                System.out.println("Door collision detected, switching room");
                if (currentRoom instanceof SpawnRoom) {
                    currentRoom = new Village();
                    player.setX(currentRoom.getSpawnX());
                    player.setY(currentRoom.getSpawnY());
                } else if (currentRoom instanceof Village) {
                    currentRoom = new SpawnRoom();
                    player.setX(currentRoom.getSpawnX());
                    player.setY(currentRoom.getSpawnY());
                }
            }
        }
        if (isMarketOpen && currentMarket != null) {
            if (e.getKeyCode() >= KeyEvent.VK_1 && e.getKeyCode() <= KeyEvent.VK_9) {
                int index = e.getKeyCode() - KeyEvent.VK_1;
                String[] items = currentMarket.getItemsForSale().keySet().toArray(new String[0]);
                if (index < items.length) {
                    String itemId = items[index];
                    if (e.isShiftDown()) {
                        if (currentMarket.sellItem(player, itemId, 1)) {
                            System.out.println("Sold 1 " + GameItemRegistry.getItem(itemId).getName());
                            updateInventorySlots();
                        } else {
                            System.out.println("Cannot sell " + GameItemRegistry.getItem(itemId).getName());
                        }
                    } else {
                        if (currentMarket.buyItem(player, itemId, 1)) {
                            System.out.println("Bought 1 " + GameItemRegistry.getItem(itemId).getName());
                            updateInventorySlots();
                        } else {
                            System.out.println("Cannot buy " + GameItemRegistry.getItem(itemId).getName());
                        }
                    }
                }
            }
        }
        if (e.getKeyCode() >= KeyEvent.VK_1 && e.getKeyCode() <= KeyEvent.VK_5 && !isMarketOpen) {
            int slotIndex = e.getKeyCode() - KeyEvent.VK_1;
            InventorySlot slot = hotbarSlots.get(slotIndex);
            if (slot.getItem() != null) {
                player.useItem(slot.getItem());
                updateInventorySlots();
            }
        }
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_W) {
            player.setUp(false);
        }
        if (e.getKeyCode() == KeyEvent.VK_S) {
            player.setDown(false);
        }
        if (e.getKeyCode() == KeyEvent.VK_A) {
            player.setLeft(false);
        }
        if (e.getKeyCode() == KeyEvent.VK_D) {
            player.setRight(false);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {
        if (!isInventoryOpen && !isMarketOpen) return;
        int mouseX = e.getX();
        int mouseY = e.getY();
        for (InventorySlot slot : hotbarSlots) {
            if (slot.containsPoint(mouseX, mouseY) && slot.getItem() != null) {
                draggedSlot = slot;
                draggedItem = slot.getItem();
                draggedQuantity = slot.getQuantity();
                dragOffset = new Point(mouseX - slot.getX(), mouseY - slot.getY());
                return;
            }
        }
        if (isInventoryOpen) {
            for (InventorySlot slot : inventorySlots) {
                if (slot.containsPoint(mouseX, mouseY) && slot.getItem() != null) {
                    draggedSlot = slot;
                    draggedItem = slot.getItem();
                    draggedQuantity = slot.getQuantity();
                    dragOffset = new Point(mouseX - slot.getX(), mouseY - slot.getY());
                    return;
                }
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (draggedSlot == null || draggedItem == null) return;
        int mouseX = e.getX();
        int mouseY = e.getY();
        InventorySlot targetSlot = null;
        for (InventorySlot slot : hotbarSlots) {
            if (slot.containsPoint(mouseX, mouseY)) {
                targetSlot = slot;
                break;
            }
        }
        if (isInventoryOpen && targetSlot == null) {
            for (InventorySlot slot : inventorySlots) {
                if (slot.containsPoint(mouseX, mouseY)) {
                    targetSlot = slot;
                    break;
                }
            }
        }
        if (targetSlot != null) {
            if (targetSlot.getItem() == null) {
                targetSlot.setItem(draggedItem, draggedQuantity);
                draggedSlot.clear();
            } else {
                // Swap items
                Item tempItem = targetSlot.getItem();
                int tempQuantity = targetSlot.getQuantity();
                targetSlot.setItem(draggedItem, draggedQuantity);
                draggedSlot.setItem(tempItem, tempQuantity);
            }
        } else {
            // Return to original slot
            draggedSlot.setItem(draggedItem, draggedQuantity);
        }
        draggedSlot = null;
        draggedItem = null;
        draggedQuantity = 0;
        updatePlayerInventory();
        repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseMoved(MouseEvent e) {
        hoveredSlot = null;
        int mouseX = e.getX();
        int mouseY = e.getY();
        for (InventorySlot slot : hotbarSlots) {
            if (slot.containsPoint(mouseX, mouseY)) {
                hoveredSlot = slot;
                break;
            }
        }
        if (isInventoryOpen && hoveredSlot == null) {
            for (InventorySlot slot : inventorySlots) {
                if (slot.containsPoint(mouseX, mouseY)) {
                    hoveredSlot = slot;
                    break;
                }
            }
        }
        repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (draggedItem != null) {
            dragOffset = new Point(e.getX() - SLOT_SIZE / 2, e.getY() - SLOT_SIZE / 2);
            repaint();
        }
    }

    private void updatePlayerInventory() {
        Map<String, Integer> newInventory = new HashMap<>();
        for (InventorySlot slot : inventorySlots) {
            if (slot.getItem() != null) {
                newInventory.merge(slot.getItem().getName(), slot.getQuantity(), Integer::sum);
            }
        }
        for (InventorySlot slot : hotbarSlots) {
            if (slot.getItem() != null) {
                newInventory.merge(slot.getItem().getName(), slot.getQuantity(), Integer::sum);
            }
        }
        player.getInventory().clear();
        player.getInventory().putAll(newInventory);
        updateInventorySlots();
        System.out.println("Updated inventory: " + player.getInventory());
    }
}