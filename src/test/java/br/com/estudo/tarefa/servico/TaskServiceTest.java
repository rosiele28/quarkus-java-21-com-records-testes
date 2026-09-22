package br.com.estudo.tarefa.servico;

import br.com.estudo.tarefa.api.TaskRequest;
import br.com.estudo.tarefa.api.TaskResponse;
import br.com.estudo.tarefa.dominio.Task;
import br.com.estudo.tarefa.repositorio.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {
    @Mock
    TaskRepository repositorio;

    TaskService servico;

    @BeforeEach
    void prepararTeste() {
        servico = new TaskService();
        servico.repositorio = repositorio;
    }

    @Test
    void deveListarTarefas() {
        Task primeira = tarefa(1L, "Estudar Quarkus", false);
        Task segunda = tarefa(2L, "Criar pipeline", true);
        when(repositorio.listAll()).thenReturn(List.of(primeira, segunda));

        List<TaskResponse> resultado = servico.listarTodas();

        assertEquals(2, resultado.size());
        assertEquals("Estudar Quarkus", resultado.getFirst().titulo());
        assertEquals(2L, resultado.get(1).id());
    }

    @Test
    void deveBuscarTarefaPorId() {
        when(repositorio.findByIdOptional(1L)).thenReturn(Optional.of(tarefa(1L, "Estudar testes", false)));

        TaskResponse resultado = servico.buscarPorId(1L);

        assertEquals(1L, resultado.id());
        assertEquals("Estudar testes", resultado.titulo());
    }

    @Test
    void deveFalharQuandoTarefaNaoExiste() {
        when(repositorio.findByIdOptional(99L)).thenReturn(Optional.empty());

        TaskNotFoundException excecao = assertThrows(TaskNotFoundException.class, () -> servico.buscarPorId(99L));

        assertEquals("Tarefa 99 não encontrada", excecao.getMessage());
    }

    @Test
    void deveCriarTarefa() {
        TaskRequest requisicao = new TaskRequest("Aprender Docker", "Criar uma imagem", false);
        ArgumentCaptor<Task> capturadorTarefa = ArgumentCaptor.forClass(Task.class);

        TaskResponse resultado = servico.criar(requisicao);

        verify(repositorio).persist(capturadorTarefa.capture());
        assertEquals("Aprender Docker", capturadorTarefa.getValue().titulo);
        assertEquals("Criar uma imagem", resultado.descricao());
        assertFalse(resultado.concluida());
    }

    @Test
    void deveAtualizarTarefa() {
        Task existente = tarefa(3L, "Antigo", false);
        when(repositorio.findByIdOptional(3L)).thenReturn(Optional.of(existente));

        TaskResponse resultado = servico.atualizar(3L, new TaskRequest("Novo", "Atualizada", true));

        assertEquals("Novo", existente.titulo);
        assertEquals("Atualizada", resultado.descricao());
        assertEquals(true, resultado.concluida());
    }

    @Test
    void deveExcluirTarefa() {
        when(repositorio.deleteById(4L)).thenReturn(true);

        servico.excluir(4L);

        verify(repositorio).deleteById(4L);
    }

    @Test
    void deveFalharAoExcluirTarefaInexistente() {
        when(repositorio.deleteById(5L)).thenReturn(false);

        assertThrows(TaskNotFoundException.class, () -> servico.excluir(5L));
    }

    private Task tarefa(Long id, String titulo, boolean concluida) {
        Task tarefa = new Task();
        tarefa.id = id;
        tarefa.titulo = titulo;
        tarefa.descricao = "Descrição";
        tarefa.concluida = concluida;
        return tarefa;
    }


}
