package sistema_chamados_api.chamado;

import jakarta.validation.constraints.NotNull;

public record AtualizarStatusRequest(

        @NotNull(message = "O status é obrigatório")
        StatusChamado status
) {
}