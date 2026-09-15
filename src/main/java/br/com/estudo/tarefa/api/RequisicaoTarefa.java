package br.com.estudo.tarefa.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RequisicaoTarefa(
        @NotBlank(message = "O título é obrigatório")
        @Size(max = 120, message = "O título deve ter no máximo 120 caracteres")
        @JsonProperty("title") String titulo,
        @Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres")
        @JsonProperty("description") String descricao,
        @JsonProperty("completed") boolean concluida) {
}
