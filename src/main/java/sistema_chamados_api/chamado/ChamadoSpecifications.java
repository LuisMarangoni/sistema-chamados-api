package sistema_chamados_api.chamado;

import org.springframework.data.jpa.domain.Specification;

public final class ChamadoSpecifications {

    private ChamadoSpecifications() {
    }

    public static Specification<Chamado> comStatus(
            StatusChamado status
    ) {
        if (status == null) {
            return Specification.unrestricted();
        }

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<Chamado> comPrioridade(
            PrioridadeChamado prioridade
    ) {
        if (prioridade == null) {
            return Specification.unrestricted();
        }

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("prioridade"),
                        prioridade
                );
    }
}