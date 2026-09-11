package br.com.estudo.task.api;

import br.com.estudo.task.service.TaskNotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ApiExceptionMapper implements ExceptionMapper<TaskNotFoundException> {
    @Override
    public Response toResponse(TaskNotFoundException exception) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorResponse("NOT_FOUND", exception.getMessage()))
                .build();
    }

    public record ErrorResponse(String code, String message) {
    }
}
