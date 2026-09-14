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
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Timeout;

import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;


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

    @Test
    @Timeout(15)
    void deveInterromperLoginQuandoRespostaDemorar() throws Exception {
        CountDownLatch requisicaoRecebida = new CountDownLatch(1);
        CountDownLatch liberarResposta = new CountDownLatch(1);

        HttpServer servidor = HttpServer.create(
                new InetSocketAddress("127.0.0.1", 0),
                0
        );

        servidor.createContext("/auth/login", exchange -> {
            try {
                exchange.getRequestBody().readAllBytes();
                requisicaoRecebida.countDown();

                // Mantém a conexão aberta, sem enviar uma resposta.
                liberarResposta.await(5, TimeUnit.SECONDS);
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
            } finally {
                exchange.close();
            }
        });

        servidor.start();

        try {
            String baseUrl = "http://127.0.0.1:"
                    + servidor.getAddress().getPort();

            UsersApiClient cliente = new UsersApiClient(
                    baseUrl,
                    "integracao@email.com",
                    "senha-ficticia-do-teste",
                    1000,
                    200
            );

            UsersApiIndisponivelException erro = assertThrows(
                    UsersApiIndisponivelException.class,
                    () -> cliente.validarSolicitante(3L)
            );

            assertTrue(
                    requisicaoRecebida.await(1, TimeUnit.SECONDS),
                    "O servidor deveria ter recebido o login"
            );

            Throwable causaRaiz = erro;

            while (causaRaiz.getCause() != null) {
                causaRaiz = causaRaiz.getCause();
            }

            assertInstanceOf(
                    SocketTimeoutException.class,
                    causaRaiz,
                    "A falha deve ser causada por timeout de leitura"
            );
        } finally {
            liberarResposta.countDown();
            servidor.stop(0);
        }
    }

}