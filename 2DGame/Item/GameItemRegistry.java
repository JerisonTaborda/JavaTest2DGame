package Item;

import java.util.HashMap;
import java.util.Map;

public class GameItemRegistry {
    private static final Map<String, Item> items = new HashMap<>();

    static {
        items.put("apple", new Item("apple", "Apple", ItemType.FOOD));
        items.put("water", new Item("water", "Water", ItemType.DRINK));
        items.put("steak", new Item("steak", "Steak", ItemType.FOOD));
        items.put("soda", new Item("soda", "Soda", ItemType.DRINK));
        items.put("water_jug", new Item("water_jug", "Water Jug", ItemType.DRINK));
        items.put("iron_sword", new Item("iron_sword", "Iron Sword", ItemType.WEAPON));
        items.put("leather_armor", new Item("leather_armor", "Leather Armor", ItemType.ARMOR));
        items.put("gold_coin", new Item("gold_coin", "Gold Coin", ItemType.CURRENCY));
        
    }

    public static Item getItem(String id) {
        return items.get(id.toLowerCase());
    }
}