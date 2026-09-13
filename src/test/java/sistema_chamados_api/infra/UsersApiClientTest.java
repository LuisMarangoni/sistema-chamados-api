package sistema_chamados_api.infra;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class UsersApiClientTest {

    @Test
    void deveRenovarTokenQuandoConsultaRetornar401() {
        RestClient.Builder builder = RestClient.builder()
                .baseUrl("http://users-api.test");

        MockRestServiceServer servidor =
                MockRestServiceServer.bindTo(builder).build();

        UsersApiClient cliente = new UsersApiClient(
                builder.build(),
                "integracao@email.com",
                "senha-ficticia-do-teste"
        );

        // Primeiro login: recebe um token.
        servidor.expect(requestTo("http://users-api.test/auth/login"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("""
                        {
                          "tipo": "Bearer",
                          "token": "token-antigo"
                        }
                        """, MediaType.APPLICATION_JSON));

        // A primeira consulta funciona com esse token.
        servidor.expect(requestTo("http://users-api.test/usuarios/3"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", "Bearer token-antigo"))
                .andRespond(withSuccess("""
                        {"id": 3, "ativo": true}
                        """, MediaType.APPLICATION_JSON));

        // Na próxima consulta, simulamos que o token expirou.
        servidor.expect(requestTo("http://users-api.test/usuarios/3"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", "Bearer token-antigo"))
                .andRespond(withStatus(HttpStatus.UNAUTHORIZED));

        // O comportamento desejado é fazer login novamente.
        servidor.expect(requestTo("http://users-api.test/auth/login"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("""
                        {
                          "tipo": "Bearer",
                          "token": "token-novo"
                        }
                        """, MediaType.APPLICATION_JSON));

        // E repetir a consulta usando o novo token.
        servidor.expect(requestTo("http://users-api.test/usuarios/3"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", "Bearer token-novo"))
                .andRespond(withSuccess("""
                        {"id": 3, "ativo": true}
                        """, MediaType.APPLICATION_JSON));

        assertDoesNotThrow(() -> cliente.validarSolicitante(3L));
        assertDoesNotThrow(() -> cliente.validarSolicitante(3L));

        servidor.verify();
    }

    @Test
    void deveEncerrarQuandoConsultaRetornar401MesmoAposRenovarToken() {
        RestClient.Builder builder = RestClient.builder()
                .baseUrl("http://users-api.test");

        MockRestServiceServer servidor =
                MockRestServiceServer.bindTo(builder).build();

        UsersApiClient cliente = new UsersApiClient(
                builder.build(),
                "integracao@email.com",
                "senha-ficticia-do-teste"
        );

        servidor.expect(requestTo("http://users-api.test/auth/login"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("""
                    {"tipo": "Bearer", "token": "token-antigo"}
                    """, MediaType.APPLICATION_JSON));

        servidor.expect(requestTo("http://users-api.test/usuarios/3"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", "Bearer token-antigo"))
                .andRespond(withStatus(HttpStatus.UNAUTHORIZED));

        servidor.expect(requestTo("http://users-api.test/auth/login"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("""
                    {"tipo": "Bearer", "token": "token-novo"}
                    """, MediaType.APPLICATION_JSON));

        servidor.expect(requestTo("http://users-api.test/usuarios/3"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", "Bearer token-novo"))
                .andRespond(withStatus(HttpStatus.UNAUTHORIZED));

        assertThrows(
                UsersApiIndisponivelException.class,
                () -> cliente.validarSolicitante(3L)
        );

        servidor.verify();
    }

}