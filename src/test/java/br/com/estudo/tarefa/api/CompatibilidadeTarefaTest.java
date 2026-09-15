package br.com.estudo.tarefa.api;

import br.com.estudo.tarefa.dominio.Tarefa;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Confere os contratos externos preservados durante a tradução do código. */
@QuarkusTest
class CompatibilidadeTarefaTest {
    @Inject
    ObjectMapper conversorJson;

    @Inject
    EntityManager gerenciadorEntidades;

    @Test
    void devePreservarCamposJsonDaTarefa() throws Exception {
        var requisicao = conversorJson.readValue(
                "{\"title\":\"Estudar\",\"description\":\"Java\",\"completed\":true}",
                RequisicaoTarefa.class);
        assertEquals("Estudar", requisicao.titulo());
        assertEquals("Java", requisicao.descricao());
        assertTrue(requisicao.concluida());

        var resposta = new RespostaTarefa(1L, requisicao.titulo(), requisicao.descricao(), requisicao.concluida());
        var json = conversorJson.readTree(conversorJson.writeValueAsString(resposta));
        assertEquals(conversorJson.readTree(
                "{\"id\":1,\"title\":\"Estudar\",\"description\":\"Java\",\"completed\":true}"), json);
    }

    @Test
    void devePreservarCamposJsonDoErro() throws Exception {
        var resposta = new MapeadorExcecaoApi.RespostaErro("NOT_FOUND", "Tarefa não encontrada");
        var json = conversorJson.readTree(conversorJson.writeValueAsString(resposta));
        assertEquals(conversorJson.readTree(
                "{\"code\":\"NOT_FOUND\",\"message\":\"Tarefa não encontrada\"}"), json);
    }

    @Test
    @TestTransaction
    void devePreservarTabelaColunasENomeDaEntidade() {
        var tarefa = new Tarefa();
        tarefa.titulo = "Conferir persistência";
        tarefa.descricao = "Manter o banco compatível";
        tarefa.concluida = false;
        gerenciadorEntidades.persist(tarefa);
        gerenciadorEntidades.flush();
        gerenciadorEntidades.clear();

        var registro = (Object[]) gerenciadorEntidades.createNativeQuery(
                "select title, description, completed from tasks where id = :id")
                .setParameter("id", tarefa.id).getSingleResult();
        assertEquals(tarefa.titulo, registro[0]);
        assertEquals(tarefa.descricao, registro[1]);
        assertFalse((Boolean) registro[2]);
        var encontrada = gerenciadorEntidades.createQuery(
                "select t from Task t where t.id = :id", Tarefa.class)
                .setParameter("id", tarefa.id).getSingleResult();
        assertEquals(tarefa.titulo, encontrada.titulo);
    }
}
