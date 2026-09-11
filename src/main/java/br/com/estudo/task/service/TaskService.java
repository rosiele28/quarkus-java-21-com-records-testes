package br.com.estudo.task.service;

import br.com.estudo.task.api.TaskRequest;
import br.com.estudo.task.api.TaskResponse;
import br.com.estudo.task.domain.Task;
import br.com.estudo.task.repository.TaskRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class TaskService {
    @Inject
    TaskRepository repository;

    public List<TaskResponse> findAll() {
        return repository.listAll().stream().map(this::toResponse).toList();
    }

    public TaskResponse findById(Long id) {
        return toResponse(findEntity(id));
    }

    @Transactional
    public TaskResponse create(TaskRequest request) {
        Task task = new Task();
        copy(request, task);
        repository.persist(task);
        return toResponse(task);
    }

    @Transactional
    public TaskResponse update(Long id, TaskRequest request) {
        Task task = findEntity(id);
        copy(request, task);
        return toResponse(task);
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.deleteById(id)) {
            throw new TaskNotFoundException(id);
        }
    }

    private Task findEntity(Long id) {
        return repository.findByIdOptional(id).orElseThrow(() -> new TaskNotFoundException(id));
    }

    private void copy(TaskRequest request, Task task) {
        task.title = request.title();
        task.description = request.description();
        task.completed = request.completed();
    }

    private TaskResponse toResponse(Task task) {
        return new TaskResponse(task.id, task.title, task.description, task.completed);
    }
}
