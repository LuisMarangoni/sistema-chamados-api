package sistema_chamados_api.infra;


import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public UsersApiClient(
            @Value("${users-api.url}") String usersApiUrl,
            @Value("${users-api.email}") String email,
            @Value("${users-api.password}") String senha,
            @Value("${users-api.connect-timeout-ms:2000}") int connectTimeoutMs,
            @Value("${users-api.read-timeout-ms:5000}") int readTimeoutMs
    ) {
        this(
                criarRestClient(
                        usersApiUrl,
                        connectTimeoutMs,
                        readTimeoutMs
                ),
                email,
                senha
        );
    }

    UsersApiClient(RestClient restClient, String email, String senha) {
        this.restClient = restClient;
        this.email = email;
        this.senha = senha;
    }

    public void validarSolicitante(Long solicitanteId) {
        try {
            String tokenUsado = obterToken();
            UsuarioResumoResponse usuario;

            try {
                usuario = buscarUsuario(solicitanteId, tokenUsado);
            } catch (HttpClientErrorException.Unauthorized exception) {
                String novoToken = renovarToken(tokenUsado);
                usuario = buscarUsuario(solicitanteId, novoToken);
            }

            if (!usuario.ativo()) {
                throw new SolicitanteInativoException(solicitanteId);
            }
        } catch (RestClientException exception) {
            throw new UsersApiIndisponivelException(exception);
        }
    }

    private UsuarioResumoResponse buscarUsuario(
            Long solicitanteId,
            String tokenAcesso
    ) {
        UsuarioResumoResponse usuario = restClient.get()
                .uri("/usuarios/{id}", solicitanteId)
                .header("Authorization", "Bearer " + tokenAcesso)
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

        if (usuario == null) {
            throw new RestClientException(
                    "Users API retornou uma resposta vazia"
            );
        }

        return usuario;
    }

    private synchronized String renovarToken(String tokenRejeitado) {
        if (Objects.equals(token, tokenRejeitado)) {
            token = null;
        }

        return obterToken();
    }

    private synchronized String obterToken() {
        if (token == null) {
            LoginResponse resposta = restClient.post()
                    .uri("/auth/login")
                    .body(new LoginRequest(email, senha))
                    .retrieve()
                    .body(LoginResponse.class);

            if (resposta == null
                    || resposta.token() == null
                    || resposta.token().isBlank()) {
                throw new RestClientException(
                        "Users API retornou login sem token válido"
                );
            }

            token = resposta.token();
        }

        return token;
    }

    private record LoginRequest(String email, String senha) {
    }

    private record LoginResponse(String tipo, String token) {
    }

    private static RestClient criarRestClient(
            String baseUrl,
            int connectTimeoutMs,
            int readTimeoutMs
    ) {
        if (connectTimeoutMs <= 0 || readTimeoutMs <= 0) {
            throw new IllegalArgumentException(
                    "Os timeouts da Users API devem ser positivos"
            );
        }

        SimpleClientHttpRequestFactory factory =
                new SimpleClientHttpRequestFactory();

        factory.setConnectTimeout(connectTimeoutMs);
        factory.setReadTimeout(readTimeoutMs);

        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(factory)
                .build();
    }

}
