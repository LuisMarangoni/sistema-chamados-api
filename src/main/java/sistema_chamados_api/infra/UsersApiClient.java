package sistema_chamados_api.infra;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import sistema_chamados_api.chamado.SolicitanteNaoEncontradoException;
import org.springframework.web.client.RestClientException;
import sistema_chamados_api.chamado.SolicitanteInativoException;
import java.util.Objects;


@Component
public class UsersApiClient {

    private final RestClient restClient;
    private final String email;
    private final String senha;
    private String token;

    public UsersApiClient(
            @Value("${users-api.url}") String usersApiUrl,
            @Value("${users-api.email}") String email,
            @Value("${users-api.password}") String senha
    ) {
        this.restClient = RestClient.create(usersApiUrl);
        this.email = email;
        this.senha = senha;
    }

    public void validarSolicitante(Long solicitanteId) {
        try {
            UsuarioResumoResponse usuario = restClient.get()
                    .uri("/usuarios/{id}", solicitanteId)
                    .header("Authorization", "Bearer " + obterToken())
                    .retrieve()
                    .onStatus(
                            status -> status.value() == 404,
                            (request, response) -> {
                                throw new SolicitanteNaoEncontradoException(
                                        solicitanteId
                                );
                            }
                    )
                    .body(UsuarioResumoResponse.class);

            if (!usuario.ativo()) {
                throw new SolicitanteInativoException(solicitanteId);
            }
        }
        catch (RestClientException exception) {
            throw new UsersApiIndisponivelException(exception);
        }
    }

    private synchronized String obterToken() {
        if (token == null) {
            LoginResponse resposta = restClient.post()
                    .uri("/auth/login")
                    .body(new LoginRequest(email, senha))
                    .retrieve()
                    .body(LoginResponse.class);

            token = Objects.requireNonNull(resposta).token();
        }

        return token;
    }

    private record LoginRequest(String email, String senha) {
    }

    private record LoginResponse(String tipo, String token) {
    }

}
