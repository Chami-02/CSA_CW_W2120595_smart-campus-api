package uk.ac.westminster.smartcampus.api.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import uk.ac.westminster.smartcampus.api.model.Sensor;
import uk.ac.westminster.smartcampus.api.model.Room;

public class SensorDao {

    private static final SensorDao INSTANCE = new SensorDao();
    private final Map<String, Sensor> sensors = DataStore.getInstance().getSensors();
    // We also need access to the RoomDao to update logic
    private final RoomDao roomDao = RoomDao.getInstance();

    private SensorDao() {
    }

    public static SensorDao getInstance() {
        return INSTANCE;
    }

    public List<Sensor> getAllSensors() {
        return new ArrayList<>(sensors.values());
    }
    
    public List<Sensor> getSensorsByType(String type) {
        return sensors.values().stream()
                .filter(s -> type.equalsIgnoreCase(s.getType()))
                .collect(Collectors.toList());
    }

    public Sensor getSensorById(String id) {
        return sensors.get(id);
    }

    public void addSensor(Sensor sensor) {
        if (sensor != null && sensor.getId() != null) {
            sensors.put(sensor.getId(), sensor);
            
            // Maintain bi-directional relationship gracefully
            if (sensor.getRoomId() != null) {
                Room room = roomDao.getRoomById(sensor.getRoomId());
                if (room != null && !room.getSensorIds().contains(sensor.getId())) {
                    room.getSensorIds().add(sensor.getId());
                }
            }
        }
    }

    public void updateSensor(Sensor sensor) {
        if (sensor != null && sensors.containsKey(sensor.getId())) {
            sensors.put(sensor.getId(), sensor);
        }
    }

    public void removeSensor(String id) {
        Sensor s = sensors.remove(id);
        if (s != null && s.getRoomId() != null) {
            Room room = roomDao.getRoomById(s.getRoomId());
            if (room != null) {
                room.getSensorIds().remove(id);
            }
        }
    }
    
    public boolean sensorExists(String id) {
        return sensors.containsKey(id);
    }
}
