package br.com.estudo.tarefa.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TaskResponse(Long id, @JsonProperty("title") String titulo, @JsonProperty("description") String descricao, @JsonProperty("completed") boolean concluida) {
}
