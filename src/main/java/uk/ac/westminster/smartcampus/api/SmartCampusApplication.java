package uk.ac.westminster.smartcampus.api;

import org.glassfish.jersey.server.ResourceConfig;
import javax.ws.rs.ApplicationPath;

@ApplicationPath("/api/v1")
public class SmartCampusApplication extends ResourceConfig {
    public SmartCampusApplication() {
        // Explicitly register packages to ensure `@Provider`s and `@Path`s are 
        // always discovered regardless of the servlet container or scanning mechanism used.
        packages("uk.ac.westminster.smartcampus.api");
    }
}
