package Item;

import Players.Player;

public class Item {
    private String id;
    private String name;
    private ItemType type;

    public Item(String id, String name, ItemType type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ItemType getType() {
        return type;
    }

    public boolean isStackable() {
        return type == ItemType.CURRENCY || type == ItemType.FOOD || type == ItemType.DRINK;
    }

    public void useEffect(Player player) {
        if (type == ItemType.FOOD && id.equals("apple")) {
            player.decreaseHunger(2.0f);
            System.out.println("Used Apple: Hunger -" + 2.0);
        } else if (type == ItemType.DRINK && id.equals("water")) {
            player.decreaseThirst(2.0f);
            System.out.println("Used Water: Thirst -" + 2.0);
        }
    }
}