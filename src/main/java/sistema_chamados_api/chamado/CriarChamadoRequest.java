package sistema_chamados_api.chamado;

public record CriarChamadoRequest(
        String titulo,
        String descricao
) {
}