package uk.ac.westminster.smartcampus.api.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import uk.ac.westminster.smartcampus.api.model.Room;

public class RoomDao {

    private static final RoomDao INSTANCE = new RoomDao();
    private final Map<String, Room> rooms = DataStore.getInstance().getRooms();

    private RoomDao() {
    }

    public static RoomDao getInstance() {
        return INSTANCE;
    }

    public List<Room> getAllRooms() {
        return new ArrayList<>(rooms.values());
    }

    public Room getRoomById(String id) {
        return rooms.get(id);
    }

    public void addRoom(Room room) {
        if (room != null && room.getId() != null) {
            rooms.put(room.getId(), room);
        }
    }

    public void updateRoom(Room room) {
        if (room != null && rooms.containsKey(room.getId())) {
            rooms.put(room.getId(), room);
        }
    }

    public void removeRoom(String id) {
        rooms.remove(id);
    }

    public boolean roomExists(String id) {
        return rooms.containsKey(id);
    }
}
