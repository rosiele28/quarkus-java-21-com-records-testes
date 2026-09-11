package br.com.estudo.task.api;

import br.com.estudo.task.service.TaskService;
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
public class TaskResource {
    @Inject
    TaskService service;

    @GET
    public List<TaskResponse> findAll() {
        return service.findAll();
    }

    @GET
    @Path("/{id}")
    public TaskResponse findById(@PathParam("id") Long id) {
        return service.findById(id);
    }

    @POST
    public Response create(@Valid TaskRequest request) {
        TaskResponse created = service.create(request);
        return Response.created(URI.create("/tasks/" + created.id())).entity(created).build();
    }

    @PUT
    @Path("/{id}")
    public TaskResponse update(@PathParam("id") Long id, @Valid TaskRequest request) {
        return service.update(id, request);
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        service.delete(id);
        return Response.noContent().build();
    }
}
