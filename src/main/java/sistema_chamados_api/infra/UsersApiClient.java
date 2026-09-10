package sistema_chamados_api.infra;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import sistema_chamados_api.chamado.SolicitanteNaoEncontradoException;

@Component
public class UsersApiClient {

    private final RestClient restClient;

    public UsersApiClient(
            @Value("${users-api.url}") String usersApiUrl
    ) {
        this.restClient = RestClient.create(usersApiUrl);
    }

    public void validarSolicitante(Long solicitanteId) {
        restClient.get()
                .uri("/usuarios/{id}", solicitanteId)
                .retrieve()
                .onStatus(
                        status -> status.value() == 404,
                        (request, response) -> {
                            throw new SolicitanteNaoEncontradoException(
                                    solicitanteId
                            );
                        }
                )
                .toBodilessEntity();
    }
}