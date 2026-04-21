package uk.ac.westminster.smartcampus.api.exception;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;

@Provider
public class LinkedResourceNotFoundExceptionMapper implements ExceptionMapper<LinkedResourceNotFoundException> {
    @Override
    public Response toResponse(LinkedResourceNotFoundException ex) {
        Map<String, String> errorEntity = new HashMap<>();
        errorEntity.put("error", ex.getMessage());
        // 422 Unprocessable Entity
        return Response.status(422)
                .type(MediaType.APPLICATION_JSON)
                .entity(errorEntity)
                .build();
    }
}
