import java.awt.*;
import Market.Markets;

public class Village extends BaseRoom {
    public Village() {
        super(2000, 2000);
        solidObjects.add(new Rectangle(100, 100, 100, 50));
        doors.add(new Rectangle(50, 1000, 40, 40));
        // Add a market
        Markets market = new Markets(300, 300, 50, 50);
        market.addItemForSale("apple", 10);  // Buy for 10 gold, sell for 8
        market.addItemForSale("water", 8);   // Buy for 8 gold, sell for 6
        market.addItemForSale("steak", 20);  // Buy for 20 gold, sell for 16
        markets.add(market);
    }

    @Override
    public int getSpawnX() {
        return 1200;
    }

    @Override
    public int getSpawnY() {
        return 200;
    }
}