package sistema_chamados_api.infra;

import java.util.Map;

public record ErroValidacaoResponse(
        int status,
        String erro,
        Map<String, String> campos
) {
}