package br.com.estudo.task.service;

import br.com.estudo.task.api.TaskRequest;
import br.com.estudo.task.api.TaskResponse;
import br.com.estudo.task.domain.Task;
import br.com.estudo.task.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    TaskRepository repository;

    TaskService service;

    @BeforeEach
    void setUp() {
        service = new TaskService();
        service.repository = repository;
    }

    @Test
    void shouldListTasks() {
        Task first = task(1L, "Estudar Quarkus", false);
        Task second = task(2L, "Criar pipeline", true);
        when(repository.listAll()).thenReturn(List.of(first, second));

        List<TaskResponse> result = service.findAll();

        assertEquals(2, result.size());
        assertEquals("Estudar Quarkus", result.getFirst().title());
        assertEquals(2L, result.get(1).id());
    }

    @Test
    void shouldFindTaskById() {
        when(repository.findByIdOptional(1L)).thenReturn(Optional.of(task(1L, "Estudar testes", false)));

        TaskResponse result = service.findById(1L);

        assertEquals(1L, result.id());
        assertEquals("Estudar testes", result.title());
    }

    @Test
    void shouldFailWhenTaskDoesNotExist() {
        when(repository.findByIdOptional(99L)).thenReturn(Optional.empty());

        TaskNotFoundException exception = assertThrows(TaskNotFoundException.class, () -> service.findById(99L));

        assertEquals("Tarefa 99 não encontrada", exception.getMessage());
    }

    @Test
    void shouldCreateTask() {
        TaskRequest request = new TaskRequest("Aprender Docker", "Criar uma imagem", false);
        ArgumentCaptor<Task> capturedTask = ArgumentCaptor.forClass(Task.class);

        TaskResponse result = service.create(request);

        verify(repository).persist(capturedTask.capture());
        assertEquals("Aprender Docker", capturedTask.getValue().title);
        assertEquals("Criar uma imagem", result.description());
        assertFalse(result.completed());
    }

    @Test
    void shouldUpdateTask() {
        Task existing = task(3L, "Antigo", false);
        when(repository.findByIdOptional(3L)).thenReturn(Optional.of(existing));

        TaskResponse result = service.update(3L, new TaskRequest("Novo", "Atualizada", true));

        assertEquals("Novo", existing.title);
        assertEquals("Atualizada", result.description());
        assertEquals(true, result.completed());
    }

    @Test
    void shouldDeleteTask() {
        when(repository.deleteById(4L)).thenReturn(true);

        service.delete(4L);

        verify(repository).deleteById(4L);
    }

    @Test
    void shouldFailWhenDeletingMissingTask() {
        when(repository.deleteById(5L)).thenReturn(false);

        assertThrows(TaskNotFoundException.class, () -> service.delete(5L));
    }

    private Task task(Long id, String title, boolean completed) {
        Task task = new Task();
        task.id = id;
        task.title = title;
        task.description = "Descrição";
        task.completed = completed;
        return task;
    }
}
