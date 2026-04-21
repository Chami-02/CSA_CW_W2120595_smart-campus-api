package uk.ac.westminster.smartcampus.api.model;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Room {
    private String id;
    private String name;
    private int capacity;
    // Using CopyOnWriteArrayList to ensure thread-safety during concurrent additions/removals
    private List<String> sensorIds = new CopyOnWriteArrayList<>();

    public Room() {
    }

    public Room(String id, String name, int capacity) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public List<String> getSensorIds() {
        return sensorIds;
    }

    public void setSensorIds(List<String> sensorIds) {
        // Enforce thread-safety if updated entirely
        this.sensorIds = new CopyOnWriteArrayList<>(sensorIds);
    }
}
