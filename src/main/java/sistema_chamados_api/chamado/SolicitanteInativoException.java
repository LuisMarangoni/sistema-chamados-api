package sistema_chamados_api.chamado;

public class SolicitanteInativoException
        extends RuntimeException {

    public SolicitanteInativoException(Long solicitanteId) {
        super(
                "Solicitante com ID "
                        + solicitanteId
                        + " está inativo"
        );
    }
}