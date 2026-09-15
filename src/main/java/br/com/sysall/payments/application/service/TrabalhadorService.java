package br.com.sysall.payments.application.service;

import br.com.sysall.payments.domain.model.Trabalhador;
import br.com.sysall.payments.domain.repository.TrabalhadorRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TrabalhadorService {

    private final TrabalhadorRepository trabalhadorRepository;

    public TrabalhadorService(
            TrabalhadorRepository trabalhadorRepository) {

        this.trabalhadorRepository =
                trabalhadorRepository;
    }

    public Trabalhador cadastrar(
            Trabalhador trabalhador) {

        validarTrabalhador(trabalhador);

        return trabalhadorRepository.salvar(
                trabalhador
        );
    }

    public Optional<Trabalhador> buscarPorId(
            UUID id) {

        if (id == null) {
            throw new IllegalArgumentException(
                    "O identificador do trabalhador é obrigatório."
            );
        }

        return trabalhadorRepository.buscarPorId(id);
    }

    public List<Trabalhador> listarTodos() {

        return trabalhadorRepository.listarTodos();
    }

    public void atualizar(
            Trabalhador trabalhador) {

        validarTrabalhador(trabalhador);

        if (trabalhador.getId() == null) {
            throw new IllegalArgumentException(
                    "O identificador do trabalhador é obrigatório."
            );
        }

        trabalhadorRepository.atualizar(
                trabalhador
        );
    }

    public void excluir(UUID id) {

        if (id == null) {
            throw new IllegalArgumentException(
                    "O identificador do trabalhador é obrigatório."
            );
        }

        trabalhadorRepository.excluir(id);
    }

    private void validarTrabalhador(
            Trabalhador trabalhador) {

        if (trabalhador == null) {
            throw new IllegalArgumentException(
                    "O trabalhador é obrigatório."
            );
        }

        if (trabalhador.getNome() == null
                || trabalhador.getNome().isBlank()) {

            throw new IllegalArgumentException(
                    "O nome do trabalhador é obrigatório."
            );
        }

        if (trabalhador.getFuncao() == null
                || trabalhador.getFuncao().isBlank()) {

            throw new IllegalArgumentException(
                    "A função do trabalhador é obrigatória."
            );
        }

        if (trabalhador.getValorPadrao() == null) {

            throw new IllegalArgumentException(
                    "O valor padrão do trabalhador é obrigatório."
            );
        }

        if (trabalhador.getValorPadrao()
                .compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "O valor padrão não pode ser negativo."
            );
        }
    }
}