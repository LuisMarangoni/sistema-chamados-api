package sistema_chamados_api.chamado;


import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;


@Service
public class ChamadoService {

    private final ChamadoRepository chamadoRepository;

    public ChamadoService(ChamadoRepository chamadoRepository) {
        this.chamadoRepository = chamadoRepository;
    }

    public Page<Chamado> listar(
            StatusChamado status,
            PrioridadeChamado prioridade,
            Pageable pageable
    ) {
        Specification<Chamado> filtros = Specification.allOf(
                ChamadoSpecifications.comStatus(status),
                ChamadoSpecifications.comPrioridade(prioridade)
        );

        return chamadoRepository.findAll(filtros, pageable);
    }

    public Optional<Chamado> buscarPorId(Long id) {
        return chamadoRepository.findById(id);
    }


    public Optional<Chamado> atualizarStatus(Long id, StatusChamado novoStatus) {
        Optional<Chamado> resultado = buscarPorId(id);

        if (resultado.isEmpty()) {
            return Optional.empty();
        }

        Chamado chamado = resultado.get();
        chamado.atualizarStatus(novoStatus);

        return Optional.of(chamadoRepository.save(chamado));
    }

    public Chamado criar(CriarChamadoRequest request) {
        Chamado chamado = new Chamado(
                request.titulo(),
                request.descricao(),
                request.prioridade()
        );

        return chamadoRepository.save(chamado);
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