package br.com.estudo.tarefa.api;

import br.com.estudo.tarefa.servico.ServicoTarefa;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

@Path("/tasks")
@Produces(MediaType.APPLICATION_JSON)
public class RecursoTarefa {
    @Inject
    ServicoTarefa servico;

    @GET
    public List<RespostaTarefa> listarTodas() {
        return servico.listarTodas();
    }

    @GET
    @Path("/{id}")
    public RespostaTarefa buscarPorId(@PathParam("id") Long id) {
        return servico.buscarPorId(id);
    }

    @POST
    public Response criar(@Valid RequisicaoTarefa requisicao) {
        RespostaTarefa criada = servico.criar(requisicao);
        return Response.created(URI.create("/tasks/" + criada.id())).entity(criada).build();
    }

    @PUT
    @Path("/{id}")
    public RespostaTarefa atualizar(@PathParam("id") Long id, @Valid RequisicaoTarefa requisicao) {
        return servico.atualizar(id, requisicao);
    }

    @DELETE
    @Path("/{id}")
    public Response excluir(@PathParam("id") Long id) {
        servico.excluir(id);
        return Response.noContent().build();
    }
}
