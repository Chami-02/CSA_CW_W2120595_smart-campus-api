package uk.ac.westminster.smartcampus.api.dao;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import uk.ac.westminster.smartcampus.api.model.Room;
import uk.ac.westminster.smartcampus.api.model.Sensor;
import uk.ac.westminster.smartcampus.api.model.SensorReading;

public class DataStore {

    private static final DataStore INSTANCE = new DataStore();

    // Primary collections using ConcurrentHashMap for thread-safe operations
    private final Map<String, Room> rooms = new ConcurrentHashMap<>();
    private final Map<String, Sensor> sensors = new ConcurrentHashMap<>();
    
    // Mapping of SensorID to their respective list of readings
    private final Map<String, List<SensorReading>> sensorReadings = new ConcurrentHashMap<>();

    private DataStore() {
        // Seed some initial data if necessary. Not strictly required but helpful for testing out of the box.
    }

    public static DataStore getInstance() {
        return INSTANCE;
    }

    public Map<String, Room> getRooms() {
        return rooms;
    }

    public Map<String, Sensor> getSensors() {
        return sensors;
    }

    public Map<String, List<SensorReading>> getSensorReadings() {
        return sensorReadings;
    }
}
