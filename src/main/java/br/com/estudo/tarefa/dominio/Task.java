package br.com.estudo.tarefa.dominio;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.Table;

@Entity(name = "Task")
@Table(name = "tasks")
public class Task extends PanacheEntity {
    @Column(name = "title")
    public String titulo;
    @Column(name = "description")
    public String descricao;
    @Column(name = "completed")
    public boolean concluida;
}
