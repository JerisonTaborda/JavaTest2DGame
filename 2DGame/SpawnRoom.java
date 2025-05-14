import java.awt.Rectangle;

public class SpawnRoom extends BaseRoom {
    public SpawnRoom() {
        super(1600, 1200);  // Room size

        // Example solid objects (walls, obstacles)
        solidObjects.add(new Rectangle(200, 200, 100, 50)); // Wall
        solidObjects.add(new Rectangle(500, 300, 200, 40)); // Wall

        // Doors
        doors.add(new Rectangle(1500, 1000, 40, 40)); // Door to Village
    }

    // Define spawn location for this room
    public int getSpawnX() {
        return 100; // X coordinate for spawn
    }

    public int getSpawnY() {
        return 100; // Y coordinate for spawn
    }
}
