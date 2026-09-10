package sistema_chamados_api.chamado;

import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDateTime;



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

    public static Specification<Chamado> criadoAPartirDe(
            LocalDateTime dataInicio
    ) {
        if (dataInicio == null) {
            return Specification.unrestricted();
        }

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(
                        root.<LocalDateTime>get("dataCriacao"),
                        dataInicio
                );
    }

    public static Specification<Chamado> criadoAte(
            LocalDateTime dataFim
    ) {
        if (dataFim == null) {
            return Specification.unrestricted();
        }

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThanOrEqualTo(
                        root.<LocalDateTime>get("dataCriacao"),
                        dataFim
                );
    }

    public static Specification<Chamado> comSolicitanteId(
            Long solicitanteId
    ) {
        if (solicitanteId == null) {
            return Specification.unrestricted();
        }

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("solicitanteId"),
                        solicitanteId
                );
    }

}
