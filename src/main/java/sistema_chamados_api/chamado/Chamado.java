package sistema_chamados_api.chamado;


import java.time.LocalDateTime;


public class Chamado {

    private Long id;
    private String titulo;
    private String descricao;
    private StatusChamado status;
    private PrioridadeChamado prioridade;
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


}
