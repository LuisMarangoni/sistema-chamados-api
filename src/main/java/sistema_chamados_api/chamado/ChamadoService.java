package sistema_chamados_api.chamado;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class ChamadoService {

    private final List<Chamado> chamados = new ArrayList<>();
    private long proximoId = 1L;

    public List<Chamado> listar() {
        return List.copyOf(chamados);
    }

    public Optional<Chamado> buscarPorId(Long id) {
        for (Chamado chamado : chamados) {
            if (chamado.getId().equals(id)) {
                return Optional.of(chamado);
            }
        }

        return Optional.empty();
    }
    public Optional<Chamado> atualizarStatus(Long id, StatusChamado novoStatus) {
        Optional<Chamado> resultado = buscarPorId(id);

        if (resultado.isEmpty()) {
            return Optional.empty();
        }

        Chamado chamado = resultado.get();
        chamado.atualizarStatus(novoStatus);

        return Optional.of(chamado);
    }

    public Chamado criar(CriarChamadoRequest request) {
        Chamado chamado = new Chamado(
                proximoId,
                request.titulo(),
                request.descricao(),
                request.prioridade()
        );

        proximoId++;
        chamados.add(chamado);

        return chamado;
    }

    public boolean excluir(Long id) {
        return chamados.removeIf(
                chamado -> chamado.getId().equals(id)
        );
    }
}