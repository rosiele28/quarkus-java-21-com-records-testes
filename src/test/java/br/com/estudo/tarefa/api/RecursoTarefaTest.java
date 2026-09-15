package br.com.estudo.tarefa.api;

import br.com.estudo.tarefa.servico.TarefaNaoEncontradaException;
import br.com.estudo.tarefa.servico.ServicoTarefa;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@QuarkusTest
class RecursoTarefaTest {
    @InjectMock
    ServicoTarefa servico;

    @Test
    void deveRetornarTodasAsTarefas() {
        when(servico.listarTodas()).thenReturn(List.of(new RespostaTarefa(1L, "Quarkus", "API", false)));

        given().when().get("/tasks")
                .then().statusCode(200).body("", hasSize(1)).body("[0].title", equalTo("Quarkus"));
    }

    @Test
    void deveRetornarUmaTarefa() {
        when(servico.buscarPorId(1L)).thenReturn(new RespostaTarefa(1L, "Java 21", "Records", true));

        given().when().get("/tasks/1")
                .then().statusCode(200).body("completed", equalTo(true));
    }

    @Test
    void deveCriarTarefa() {
        when(servico.criar(any())).thenReturn(new RespostaTarefa(10L, "Docker", "Imagem", false));

        given().contentType("application/json")
                .body("{\"title\":\"Docker\",\"description\":\"Imagem\",\"completed\":false}")
                .when().post("/tasks")
                .then().statusCode(201).header("Location", endsWith("/tasks/10"))
                .body("id", equalTo(10));
    }

    @Test
    void deveAtualizarTarefa() {
        when(servico.atualizar(eq(1L), any())).thenReturn(new RespostaTarefa(1L, "CI", "Ações", true));

        given().contentType("application/json")
                .body("{\"title\":\"CI\",\"description\":\"Ações\",\"completed\":true}")
                .when().put("/tasks/1")
                .then().statusCode(200).body("title", equalTo("CI"));
    }

    @Test
    void deveExcluirTarefa() {
        doNothing().when(servico).excluir(1L);

        given().when().delete("/tasks/1").then().statusCode(204);
    }

    @Test
    void deveRetornar404ParaTarefaInexistente() {
        doThrow(new TarefaNaoEncontradaException(99L)).when(servico).buscarPorId(99L);

        given().when().get("/tasks/99")
                .then().statusCode(404).body("code", equalTo("NOT_FOUND"));
    }
}
