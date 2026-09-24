package br.com.estudo.tarefa.servico;

import br.com.estudo.tarefa.api.TaskRequest;
import br.com.estudo.tarefa.api.TaskResponse;
import br.com.estudo.tarefa.dominio.Task;
import br.com.estudo.tarefa.repositorio.TaskRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class TaskService {
    @Inject
    TaskRepository repositorio;

    public List<TaskResponse> listarTodas() {
        return repositorio.listAll().stream().map(this::converterParaResposta).toList();
    }

    public  List<TaskResponse> listar(Boolean status) {
        if (status == null){
             return listarTodas();
        }
        return repositorio.list("concluida", status).stream().map(this::converterParaResposta).toList(); 
    }

    public TaskResponse buscarPorId(Long id) {
        return converterParaResposta(buscarEntidade(id));
    }

    @Transactional
    public TaskResponse criar(TaskRequest requisicao) {
        Task tarefa = new Task();
        copiarDados(requisicao, tarefa);
        repositorio.persist(tarefa);
        return converterParaResposta(tarefa);
    }

    @Transactional
    public TaskResponse atualizar(Long id, TaskRequest requisicao) {
        Task tarefa = buscarEntidade(id);
        copiarDados(requisicao, tarefa);
        return converterParaResposta(tarefa);
    }


    @Transactional
    public void excluir(Long id) {
        if (!repositorio.deleteById(id)) {
            throw new TaskNotFoundException(id);
        }
    }


    private Task buscarEntidade(Long id) {
        return repositorio.findByIdOptional(id).orElseThrow(() -> new TaskNotFoundException(id));
    }

    private void copiarDados(TaskRequest requisicao, Task tarefa) {
        tarefa.titulo = requisicao.titulo();
        tarefa.descricao = requisicao.descricao();
        tarefa.concluida = requisicao.concluida();
    }

    private TaskResponse converterParaResposta(Task tarefa) {
        return new TaskResponse(tarefa.id, tarefa.titulo, tarefa.descricao, tarefa.concluida);
    }


}
