package br.com.sysall.payments.domain.repository;

import br.com.sysall.payments.domain.model.Trabalhador;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TrabalhadorRepository {

    Trabalhador salvar(Trabalhador trabalhador);

    Optional<Trabalhador> buscarPorId(UUID id);

    List<Trabalhador> listarTodos();

    void atualizar(Trabalhador trabalhador);

    void excluir(UUID id);
}