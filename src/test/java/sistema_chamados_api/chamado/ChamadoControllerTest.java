package sistema_chamados_api.chamado;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
                PrioridadeChamado.ALTA
        );

        PageImpl<Chamado> pagina = new PageImpl<>(
                List.of(chamado),
                PageRequest.of(0, 10),
                1
        );

        when(chamadoService.listar(
                eq(StatusChamado.ABERTO),
                eq(PrioridadeChamado.ALTA),
                isNull(),
                isNull(),
                any(Pageable.class)
        )).thenReturn(pagina);

        mockMvc.perform(
                        get("/chamados")
                                .param("status", "ABERTO")
                                .param("prioridade", "ALTA")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].titulo")
                        .value("Erro de rede"))
                .andExpect(jsonPath("$.content[0].prioridade")
                        .value("ALTA"))
                .andExpect(jsonPath("$.content[0].status")
                        .value("ABERTO"))
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
}