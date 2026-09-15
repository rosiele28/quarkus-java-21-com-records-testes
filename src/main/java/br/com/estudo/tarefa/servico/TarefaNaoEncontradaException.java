package br.com.estudo.tarefa.servico;

public class TarefaNaoEncontradaException extends RuntimeException {
    public TarefaNaoEncontradaException(Long id) {
        super("Tarefa %d não encontrada".formatted(id));
    }
}
