package sistema_chamados_api.chamado;

public record ResumoChamadosResponse(
        long total,
        long abertos,
        long emAndamento,
        long resolvidos,
        long fechados
) {
}