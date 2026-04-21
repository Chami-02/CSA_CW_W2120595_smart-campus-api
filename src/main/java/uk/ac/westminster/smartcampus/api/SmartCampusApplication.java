package uk.ac.westminster.smartcampus.api;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/api/v1")
public class SmartCampusApplication extends Application {
    // This subclass is auto-discovered by the servlet container.
    // An empty class body commands the runtime to automatically scan
    // for all classes annotated with @Path and @Provider within the application.
}
