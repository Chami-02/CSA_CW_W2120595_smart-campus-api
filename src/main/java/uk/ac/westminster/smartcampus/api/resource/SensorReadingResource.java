package uk.ac.westminster.smartcampus.api.resource;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import uk.ac.westminster.smartcampus.api.dao.SensorReadingDao;
import uk.ac.westminster.smartcampus.api.dao.SensorDao;
import uk.ac.westminster.smartcampus.api.model.SensorReading;
import uk.ac.westminster.smartcampus.api.model.Sensor;
import uk.ac.westminster.smartcampus.api.exception.SensorUnavailableException;

// Note: No @Path here dynamically as this is a Sub-Resource managed dynamically via Locator
public class SensorReadingResource {
    
    private final String sensorId;
    private final SensorReadingDao readingDao = SensorReadingDao.getInstance();
    private final SensorDao sensorDao = SensorDao.getInstance();

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReadings() {
        return Response.ok(readingDao.getReadingsForSensor(sensorId)).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addReading(SensorReading reading) {
        Sensor sensor = sensorDao.getSensorById(sensorId);
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus()) || "OFFLINE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException("Sensor is currently unavailable for accepting readings.");
        }
        
        // DAO addReading handles the thread-safe cascade update to Sensor's currentValue
        readingDao.addReading(sensorId, reading);
        return Response.status(Response.Status.CREATED).entity(reading).build();
    }
}
