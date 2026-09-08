package sistema_chamados_api.chamado;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ChamadoRepository
        extends JpaRepository<Chamado, Long> {
}