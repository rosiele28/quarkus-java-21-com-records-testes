package br.com.estudo.tarefa.servico;

public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(Long id) {
        super("Tarefa %d não encontrada".formatted(id));
    }
}
