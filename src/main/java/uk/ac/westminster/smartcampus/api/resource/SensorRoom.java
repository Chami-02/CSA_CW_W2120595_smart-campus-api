package uk.ac.westminster.smartcampus.api.resource;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import uk.ac.westminster.smartcampus.api.dao.RoomDao;
import uk.ac.westminster.smartcampus.api.model.Room;
import uk.ac.westminster.smartcampus.api.exception.RoomNotEmptyException;

@Path("/rooms")
public class SensorRoom {
    
    private final RoomDao roomDao = RoomDao.getInstance();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllRooms() {
        return Response.ok(roomDao.getAllRooms()).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createRoom(Room room) {
        roomDao.addRoom(room);
        return Response.status(Response.Status.CREATED).entity(room).build();
    }

    @GET
    @Path("/{roomId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRoom(@PathParam("roomId") String roomId) {
        Room room = roomDao.getRoomById(roomId);
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(room).build();
    }

    @DELETE
    @Path("/{roomId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = roomDao.getRoomById(roomId);
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        
        // Safety lock mapping logic requirement
        if (room.getSensorIds() != null && !room.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException("Room still has active sensors assigned.");
        }
        
        roomDao.removeRoom(roomId);
        return Response.noContent().build();
    }
}
