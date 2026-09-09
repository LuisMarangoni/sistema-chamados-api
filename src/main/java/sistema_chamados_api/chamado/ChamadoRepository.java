package sistema_chamados_api.chamado;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ChamadoRepository
        extends JpaRepository<Chamado, Long>,
        JpaSpecificationExecutor<Chamado> {
}