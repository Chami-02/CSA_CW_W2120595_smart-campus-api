package uk.ac.westminster.smartcampus.api.exception;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.WebApplicationException;
import java.util.HashMap;
import java.util.Map;

@Provider
public class GenericExceptionMapper implements ExceptionMapper<Exception> {
    @Override
    public Response toResponse(Exception ex) {
        if (ex instanceof WebApplicationException) {
            return ((WebApplicationException) ex).getResponse();
        }

        Map<String, String> errorEntity = new HashMap<>();
        // Important: We do not leak the stack trace ex.getMessage() here to avoid cybersecurity enumeration
        errorEntity.put("error", "An internal server error occurred. Please contact the administrator.");
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON)
                .entity(errorEntity)
                .build();
    }
}
