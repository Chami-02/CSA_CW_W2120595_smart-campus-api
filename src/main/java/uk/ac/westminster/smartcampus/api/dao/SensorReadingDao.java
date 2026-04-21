package uk.ac.westminster.smartcampus.api.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import uk.ac.westminster.smartcampus.api.model.SensorReading;
import uk.ac.westminster.smartcampus.api.model.Sensor;

public class SensorReadingDao {

    private static final SensorReadingDao INSTANCE = new SensorReadingDao();
    private final Map<String, List<SensorReading>> sensorReadings = DataStore.getInstance().getSensorReadings();
    private final SensorDao sensorDao = SensorDao.getInstance();

    private SensorReadingDao() {
    }

    public static SensorReadingDao getInstance() {
        return INSTANCE;
    }

    public List<SensorReading> getReadingsForSensor(String sensorId) {
        List<SensorReading> list = sensorReadings.get(sensorId);
        if (list == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(list);
    }

    public void addReading(String sensorId, SensorReading reading) {
        // Enforce thread-safety creation dynamically
        sensorReadings.putIfAbsent(sensorId, new CopyOnWriteArrayList<>());
        List<SensorReading> list = sensorReadings.get(sensorId);
        list.add(reading);

        // Update current value of the sensor atomically according to business rules
        Sensor sensor = sensorDao.getSensorById(sensorId);
        if (sensor != null) {
            synchronized (sensor) {
                sensor.setCurrentValue(reading.getValue());
            }
        }
    }
}
