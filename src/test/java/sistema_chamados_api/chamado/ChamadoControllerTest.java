package sistema_chamados_api.chamado;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import java.util.List;
import org.springframework.http.MediaType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.doThrow;
import sistema_chamados_api.infra.UsersApiIndisponivelException;
import java.util.Optional;



@WebMvcTest(ChamadoController.class)
class ChamadoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ChamadoService chamadoService;

    @Test
    void deveListarChamadosComFiltros() throws Exception {
        Chamado chamado = new Chamado(
                "Erro de rede",
                "Usuário sem acesso à internet",
                PrioridadeChamado.ALTA,
                1L
        );

        PageImpl<Chamado> pagina = new PageImpl<>(
                List.of(chamado),
                PageRequest.of(0, 10),
                1
        );

        when(chamadoService.listar(
                eq(StatusChamado.ABERTO),
                eq(PrioridadeChamado.ALTA),
                eq(1L),
                isNull(),
                isNull(),
                any(Pageable.class)
        )).thenReturn(pagina);

        mockMvc.perform(
                        get("/chamados")
                                .param("status", "ABERTO")
                                .param("prioridade", "ALTA")
                                .param("solicitanteId", "1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].titulo")
                        .value("Erro de rede"))
                .andExpect(jsonPath("$.content[0].prioridade")
                        .value("ALTA"))
                .andExpect(jsonPath("$.content[0].status")
                        .value("ABERTO"))
                .andExpect(jsonPath("$.content[0].solicitanteId")
                        .value(1))
                .andExpect(jsonPath("$.totalElements")
                        .value(1));
    }

    @Test
    void deveRetornarBadRequestParaStatusInvalido()
            throws Exception {
        mockMvc.perform(
                        get("/chamados")
                                .param("status", "INVALIDO")
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveRetornarBadRequestQuandoSolicitanteNaoForInformado()
            throws Exception {
        mockMvc.perform(
                        post("/chamados")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                      "titulo": "Erro de rede",
                                      "descricao": "Usuário sem acesso à internet",
                                      "prioridade": "ALTA"
                                    }
                                    """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.campos.solicitanteId")
                        .value("O solicitante é obrigatório"));
    }

    @Test
    void deveRetornarNotFoundQuandoSolicitanteNaoExistir()
            throws Exception {
        doThrow(new SolicitanteNaoEncontradoException(999L))
                .when(chamadoService)
                .criar(any(CriarChamadoRequest.class));

        mockMvc.perform(
                        post("/chamados")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                {
                                  "titulo": "Erro de rede",
                                  "descricao": "Usuário sem acesso à internet",
                                  "prioridade": "ALTA",
                                  "solicitanteId": 999
                                }
                                """)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail")
                        .value("Solicitante com ID 999 não foi encontrado"));
    }

    @Test
    void deveRetornarServiceUnavailableQuandoUsersApiFalhar()
            throws Exception {
        doThrow(new UsersApiIndisponivelException(
                new RuntimeException("Falha de conexão")
        ))
                .when(chamadoService)
                .criar(any(CriarChamadoRequest.class));

        mockMvc.perform(
                        post("/chamados")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                {
                                  "titulo": "Erro de rede",
                                  "descricao": "Usuário sem acesso à internet",
                                  "prioridade": "ALTA",
                                  "solicitanteId": 1
                                }
                                """)
                )
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.detail")
                        .value("Users API está indisponível"));
    }

    @Test
    void deveRetornarUnprocessableContentQuandoSolicitanteEstiverInativo()
            throws Exception {
        doThrow(new SolicitanteInativoException(1L))
                .when(chamadoService)
                .criar(any(CriarChamadoRequest.class));

        mockMvc.perform(
                        post("/chamados")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                {
                                  "titulo": "Erro de rede",
                                  "descricao": "Usuário sem acesso à internet",
                                  "prioridade": "ALTA",
                                  "solicitanteId": 1
                                }
                                """)
                )
                .andExpect(status().is(422))
                .andExpect(jsonPath("$.detail")
                        .value("Solicitante com ID 1 está inativo"));
    }

    @Test
    void deveListarHistoricoDoChamado() throws Exception {
        HistoricoStatusChamado historico =
                new HistoricoStatusChamado(
                        1L,
                        StatusChamado.ABERTO,
                        StatusChamado.EM_ANDAMENTO
                );

        when(chamadoService.listarHistorico(1L))
                .thenReturn(Optional.of(List.of(historico)));

        mockMvc.perform(get("/chamados/1/historico"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].chamadoId").value(1))
                .andExpect(jsonPath("$[0].statusAnterior")
                        .value("ABERTO"))
                .andExpect(jsonPath("$[0].statusNovo")
                        .value("EM_ANDAMENTO"));
    }

    @Test
    void deveRetornarNotFoundAoListarHistoricoDeChamadoInexistente()
            throws Exception {
        when(chamadoService.listarHistorico(999L))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/chamados/999/historico"))
                .andExpect(status().isNotFound());
    }

}
