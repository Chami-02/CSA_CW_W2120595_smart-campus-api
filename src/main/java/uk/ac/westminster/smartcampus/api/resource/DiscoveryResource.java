package uk.ac.westminster.smartcampus.api.resource;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
public class DiscoveryResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDiscoveryMap() {
        Map<String, Object> discoveryData = new HashMap<>();
        discoveryData.put("version", "v1.0.0");
        discoveryData.put("adminContact", "admin@smartcampus.westminster.ac.uk");

        Map<String, String> resources = new HashMap<>();
        resources.put("rooms", "/api/v1/rooms");
        resources.put("sensors", "/api/v1/sensors");

        discoveryData.put("resources", resources);

        return Response.ok(discoveryData).build();
    }
}
