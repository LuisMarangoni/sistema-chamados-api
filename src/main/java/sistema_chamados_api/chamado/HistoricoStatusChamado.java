package sistema_chamados_api.chamado;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "historico_status_chamados")
public class HistoricoStatusChamado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chamado_id", nullable = false)
    private Long chamadoId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_anterior", length = 30)
    private StatusChamado statusAnterior;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_novo", nullable = false, length = 30)
    private StatusChamado statusNovo;

    @Column(name = "data_alteracao", nullable = false)
    private LocalDateTime dataAlteracao;

    protected HistoricoStatusChamado() {
    }

    public HistoricoStatusChamado(
            Long chamadoId,
            StatusChamado statusAnterior,
            StatusChamado statusNovo
    ) {
        this.chamadoId = chamadoId;
        this.statusAnterior = statusAnterior;
        this.statusNovo = statusNovo;
        this.dataAlteracao = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Long getChamadoId() {
        return chamadoId;
    }

    public StatusChamado getStatusAnterior() {
        return statusAnterior;
    }

    public StatusChamado getStatusNovo() {
        return statusNovo;
    }

    public LocalDateTime getDataAlteracao() {
        return dataAlteracao;
    }
}