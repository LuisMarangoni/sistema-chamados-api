package sistema_chamados_api.chamado;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class ChamadoService {

    private final List<Chamado> chamados = new ArrayList<>();
    private long proximoId = 1L;

    public List<Chamado> listar() {
        return List.copyOf(chamados);
    }

    public Chamado criar(CriarChamadoRequest request) {
        Chamado chamado = new Chamado(
                proximoId,
                request.titulo(),
                request.descricao()
        );

        proximoId++;
        chamados.add(chamado);

        return chamado;
    }
}