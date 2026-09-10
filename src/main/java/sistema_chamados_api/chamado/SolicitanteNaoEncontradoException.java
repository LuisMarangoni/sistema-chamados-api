package sistema_chamados_api.chamado;

public class SolicitanteNaoEncontradoException
        extends RuntimeException {

    public SolicitanteNaoEncontradoException(Long solicitanteId) {
        super(
                "Solicitante com ID "
                        + solicitanteId
                        + " não foi encontrado"
        );
    }
}