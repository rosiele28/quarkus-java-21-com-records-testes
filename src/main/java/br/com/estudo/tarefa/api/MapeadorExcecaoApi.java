package br.com.estudo.tarefa.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import br.com.estudo.tarefa.servico.TarefaNaoEncontradaException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class MapeadorExcecaoApi implements ExceptionMapper<TarefaNaoEncontradaException> {
    @Override
    public Response toResponse(TarefaNaoEncontradaException excecao) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity(new RespostaErro("NOT_FOUND", excecao.getMessage()))
                .build();
    }

    public record RespostaErro(@JsonProperty("code") String codigo, @JsonProperty("message") String mensagem) {
    }
}
