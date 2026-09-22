package br.com.estudo.tarefa.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import br.com.estudo.tarefa.servico.TaskNotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ApiExceptionMapper implements ExceptionMapper<TaskNotFoundException> {
    @Override
    public Response toResponse(TaskNotFoundException excecao) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorResponse("NOT_FOUND", excecao.getMessage()))
                .build();
    }

    public record ErrorResponse(@JsonProperty("code") String codigo, @JsonProperty("message") String mensagem) {
    }
}
