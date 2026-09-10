package sistema_chamados_api.chamado;

import sistema_chamados_api.infra.UsersApiClient;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;
import java.time.LocalDateTime;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;



@Service
public class ChamadoService {

    private final ChamadoRepository chamadoRepository;
    private final UsersApiClient usersApiClient;
    private final HistoricoStatusChamadoRepository
            historicoStatusChamadoRepository;

    public ChamadoService(
            ChamadoRepository chamadoRepository,
            UsersApiClient usersApiClient,
            HistoricoStatusChamadoRepository historicoStatusChamadoRepository
    ) {
        this.chamadoRepository = chamadoRepository;
        this.usersApiClient = usersApiClient;
        this.historicoStatusChamadoRepository =
                historicoStatusChamadoRepository;
    }

    public Page<Chamado> listar(
            StatusChamado status,
            PrioridadeChamado prioridade,
            Long solicitanteId,
            LocalDateTime dataInicio,
            LocalDateTime dataFim,
            Pageable pageable
    ) {
        Specification<Chamado> filtros = Specification.allOf(
                ChamadoSpecifications.comStatus(status),
                ChamadoSpecifications.comPrioridade(prioridade),
                ChamadoSpecifications.comSolicitanteId(solicitanteId),
                ChamadoSpecifications.criadoAPartirDe(dataInicio),
                ChamadoSpecifications.criadoAte(dataFim)
        );

        return chamadoRepository.findAll(filtros, pageable);
    }

    public Optional<Chamado> buscarPorId(Long id) {
        return chamadoRepository.findById(id);
    }

    public Optional<List<HistoricoStatusChamado>> listarHistorico(
            Long chamadoId
    ) {
        if (!chamadoRepository.existsById(chamadoId)) {
            return Optional.empty();
        }

        List<HistoricoStatusChamado> historico =
                historicoStatusChamadoRepository
                        .findByChamadoIdOrderByDataAlteracaoAsc(
                                chamadoId
                        );

        return Optional.of(historico);
    }

    @Transactional
    public Optional<Chamado> atualizarStatus(
            Long id,
            StatusChamado novoStatus
    ) {
        Optional<Chamado> resultado = buscarPorId(id);

        if (resultado.isEmpty()) {
            return Optional.empty();
        }

        Chamado chamado = resultado.get();
        StatusChamado statusAnterior = chamado.getStatus();

        chamado.atualizarStatus(novoStatus);

        Chamado chamadoAtualizado = chamadoRepository.save(chamado);

        if (statusAnterior != novoStatus) {
            HistoricoStatusChamado historico =
                    new HistoricoStatusChamado(
                            chamadoAtualizado.getId(),
                            statusAnterior,
                            novoStatus
                    );

            historicoStatusChamadoRepository.save(historico);
        }

        return Optional.of(chamadoAtualizado);
    }

    @Transactional
    public Chamado criar(CriarChamadoRequest request) {
        usersApiClient.validarSolicitante(request.solicitanteId());

        Chamado chamado = new Chamado(
                request.titulo(),
                request.descricao(),
                request.prioridade(),
                request.solicitanteId()
        );

        Chamado chamadoSalvo = chamadoRepository.save(chamado);

        HistoricoStatusChamado historico =
                new HistoricoStatusChamado(
                        chamadoSalvo.getId(),
                        null,
                        StatusChamado.ABERTO
                );

        historicoStatusChamadoRepository.save(historico);

        return chamadoSalvo;
    }

    public boolean excluir(Long id) {
        if (!chamadoRepository.existsById(id)) {
            return false;
        }

        chamadoRepository.deleteById(id);
        return true;
    }

    public Optional<Chamado> atualizar(
            Long id,
            AtualizarChamadoRequest request
    ) {
        Optional<Chamado> resultado = buscarPorId(id);

        if (resultado.isEmpty()) {
            return Optional.empty();
        }

        Chamado chamado = resultado.get();

        chamado.atualizarDados(
                request.titulo(),
                request.descricao(),
                request.prioridade()
        );

        return Optional.of(chamadoRepository.save(chamado));
    }
}