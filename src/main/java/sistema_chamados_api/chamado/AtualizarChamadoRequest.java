package sistema_chamados_api.chamado;

public record AtualizarChamadoRequest(
        String titulo,
        String descricao,
        PrioridadeChamado prioridade
) {
}