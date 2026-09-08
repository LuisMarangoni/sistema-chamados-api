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
@Table(name = "chamados")



public class Chamado {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String titulo;

    @Column(nullable = false, length = 1000)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatusChamado status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PrioridadeChamado prioridade;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    public StatusChamado getStatus() {
        return status;
    }

    public PrioridadeChamado getPrioridade() {
        return prioridade;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public Chamado(
            Long id,
            String titulo,
            String descricao,
            PrioridadeChamado prioridade
    ) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.status = StatusChamado.ABERTO;
        this.prioridade = prioridade;
        this.dataCriacao = LocalDateTime.now();
    }
    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void atualizarStatus(StatusChamado novoStatus) {
        this.status = novoStatus;
    }

    public void atualizarDados(
            String novoTitulo,
            String novaDescricao,
            PrioridadeChamado novaPrioridade
    ) {
        this.titulo = novoTitulo;
        this.descricao = novaDescricao;
        this.prioridade = novaPrioridade;
    }

    protected Chamado() {
    }


}
