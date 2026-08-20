package sistema_chamados_api.chamado;

public class Chamado {

    private Long id;
    private String titulo;
    private String descricao;

    public Chamado(Long id, String titulo, String descricao) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
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
