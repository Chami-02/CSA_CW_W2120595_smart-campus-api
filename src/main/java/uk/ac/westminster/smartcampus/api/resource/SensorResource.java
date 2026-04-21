package uk.ac.westminster.smartcampus.api.resource;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import uk.ac.westminster.smartcampus.api.dao.SensorDao;
import uk.ac.westminster.smartcampus.api.dao.RoomDao;
import uk.ac.westminster.smartcampus.api.model.Sensor;
import uk.ac.westminster.smartcampus.api.exception.LinkedResourceNotFoundException;

@Path("/sensors")
public class SensorResource {
    
    private final SensorDao sensorDao = SensorDao.getInstance();
    private final RoomDao roomDao = RoomDao.getInstance();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSensors(@QueryParam("type") String type) {
        if (type != null && !type.trim().isEmpty()) {
            return Response.ok(sensorDao.getSensorsByType(type)).build();
        }
        return Response.ok(sensorDao.getAllSensors()).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerSensor(Sensor sensor) {
        // Validate room reference
        if (sensor.getRoomId() == null || !roomDao.roomExists(sensor.getRoomId())) {
            throw new LinkedResourceNotFoundException("Room with ID " + sensor.getRoomId() + " does not exist.");
        }
        
        sensorDao.addSensor(sensor);
        return Response.status(Response.Status.CREATED).entity(sensor).build();
    }

    // Sub-Resource Locator Pattern implementation dynamically resolving to ReadingResource
    @Path("/{sensorId}/readings")
    public SensorReadingResource getSensorReadingResource(@PathParam("sensorId") String sensorId) {
        // Returning instance context without HTTP verb annotations
        return new SensorReadingResource(sensorId);
    }
}
