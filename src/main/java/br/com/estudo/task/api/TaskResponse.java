package br.com.estudo.task.api;

public record TaskResponse(Long id, String title, String description, boolean completed) {
}
