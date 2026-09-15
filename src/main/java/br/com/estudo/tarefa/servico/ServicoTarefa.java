package br.com.estudo.tarefa.servico;

import br.com.estudo.tarefa.api.RequisicaoTarefa;
import br.com.estudo.tarefa.api.RespostaTarefa;
import br.com.estudo.tarefa.dominio.Tarefa;
import br.com.estudo.tarefa.repositorio.RepositorioTarefa;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class ServicoTarefa {
    @Inject
    RepositorioTarefa repositorio;

    public List<RespostaTarefa> listarTodas() {
        return repositorio.listAll().stream().map(this::converterParaResposta).toList();
    }

    public RespostaTarefa buscarPorId(Long id) {
        return converterParaResposta(buscarEntidade(id));
    }

    @Transactional
    public RespostaTarefa criar(RequisicaoTarefa requisicao) {
        Tarefa tarefa = new Tarefa();
        copiarDados(requisicao, tarefa);
        repositorio.persist(tarefa);
        return converterParaResposta(tarefa);
    }

    @Transactional
    public RespostaTarefa atualizar(Long id, RequisicaoTarefa requisicao) {
        Tarefa tarefa = buscarEntidade(id);
        copiarDados(requisicao, tarefa);
        return converterParaResposta(tarefa);
    }

    @Transactional
    public void excluir(Long id) {
        if (!repositorio.deleteById(id)) {
            throw new TarefaNaoEncontradaException(id);
        }
    }

    private Tarefa buscarEntidade(Long id) {
        return repositorio.findByIdOptional(id).orElseThrow(() -> new TarefaNaoEncontradaException(id));
    }

    private void copiarDados(RequisicaoTarefa requisicao, Tarefa tarefa) {
        tarefa.titulo = requisicao.titulo();
        tarefa.descricao = requisicao.descricao();
        tarefa.concluida = requisicao.concluida();
    }

    private RespostaTarefa converterParaResposta(Tarefa tarefa) {
        return new RespostaTarefa(tarefa.id, tarefa.titulo, tarefa.descricao, tarefa.concluida);
    }
}
