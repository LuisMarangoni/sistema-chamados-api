package sistema_chamados_api.chamado;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoricoStatusChamadoRepository
        extends JpaRepository<HistoricoStatusChamado, Long> {

    List<HistoricoStatusChamado>
    findByChamadoIdOrderByDataAlteracaoAsc(Long chamadoId);
}