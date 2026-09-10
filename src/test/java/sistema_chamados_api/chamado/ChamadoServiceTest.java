package sistema_chamados_api.chamado;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import sistema_chamados_api.infra.UsersApiClient;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import sistema_chamados_api.infra.UsersApiIndisponivelException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.ArgumentMatchers.eq;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;




@ExtendWith(MockitoExtension.class)
class ChamadoServiceTest {

    @Mock
    private ChamadoRepository chamadoRepository;

    @Mock
    private UsersApiClient usersApiClient;

    @InjectMocks
    private ChamadoService chamadoService;

    @Test
    void deveCriarChamadoComStatusAberto() {
        CriarChamadoRequest request = new CriarChamadoRequest(
                "Computador não liga",
                "Equipamento não apresenta sinal",
                PrioridadeChamado.ALTA,
                1L
        );

        when(chamadoRepository.save(any(Chamado.class)))
                .thenAnswer(invocacao -> invocacao.getArgument(0));

        Chamado resultado = chamadoService.criar(request);

        assertEquals("Computador não liga", resultado.getTitulo());
        assertEquals("Equipamento não apresenta sinal", resultado.getDescricao());
        assertEquals(PrioridadeChamado.ALTA, resultado.getPrioridade());
        assertEquals(StatusChamado.ABERTO, resultado.getStatus());
        assertNotNull(resultado.getDataCriacao());

        verify(chamadoRepository).save(any(Chamado.class));
        verify(usersApiClient).validarSolicitante(1L);
    }

    @Test
    void deveRetornarVazioQuandoChamadoNaoExistir() {
        when(chamadoRepository.findById(999L))
                .thenReturn(Optional.empty());

        Optional<Chamado> resultado =
                chamadoService.buscarPorId(999L);

        assertTrue(resultado.isEmpty());

        verify(chamadoRepository).findById(999L);
    }

    @Test
    void deveAtualizarStatusDoChamado() {
        Chamado chamado = new Chamado(
                "Sistema indisponível",
                "Usuário não consegue acessar",
                PrioridadeChamado.URGENTE,
                1L
        );

        when(chamadoRepository.findById(1L))
                .thenReturn(Optional.of(chamado));

        when(chamadoRepository.save(chamado))
                .thenReturn(chamado);

        Optional<Chamado> resultado =
                chamadoService.atualizarStatus(
                        1L,
                        StatusChamado.EM_ANDAMENTO
                );

        assertTrue(resultado.isPresent());
        assertEquals(
                StatusChamado.EM_ANDAMENTO,
                resultado.get().getStatus()
        );

        verify(chamadoRepository).findById(1L);
        verify(chamadoRepository).save(chamado);
    }

    @Test
    void deveListarChamadosComPaginacao() {
        Pageable pageable = PageRequest.of(0, 2);

        Chamado chamado = new Chamado(
                "Erro de rede",
                "Usuário sem acesso à internet",
                PrioridadeChamado.ALTA,
                1L
        );

        Page<Chamado> paginaEsperada = new PageImpl<>(
                List.of(chamado),
                pageable,
                1
        );

        when(chamadoRepository.findAll(
                any(Specification.class),
                eq(pageable)
        )).thenReturn(paginaEsperada);

        Page<Chamado> resultado = chamadoService.listar(
                null,
                null,
                null,
                null,
                null,
                pageable
        );

        assertEquals(1, resultado.getTotalElements());
        assertEquals(1, resultado.getContent().size());
        assertEquals("Erro de rede", resultado.getContent().getFirst().getTitulo());

        verify(chamadoRepository).findAll(
                any(Specification.class),
                eq(pageable)
        );
    }

    @Test
    void naoDeveSalvarChamadoQuandoUsersApiEstiverIndisponivel() {
        CriarChamadoRequest request = new CriarChamadoRequest(
                "Teste de indisponibilidade",
                "Não deve salvar sem validar solicitante",
                PrioridadeChamado.ALTA,
                1L
        );

        doThrow(new UsersApiIndisponivelException(
                new RuntimeException("Falha de conexão")
        ))
                .when(usersApiClient)
                .validarSolicitante(1L);

        assertThrows(
                UsersApiIndisponivelException.class,
                () -> chamadoService.criar(request)
        );

        verify(chamadoRepository, never()).save(any(Chamado.class));
    }

    @Test
    void naoDeveSalvarChamadoQuandoSolicitanteEstiverInativo() {
        CriarChamadoRequest request = new CriarChamadoRequest(
                "Teste de solicitante inativo",
                "Não deve salvar chamado para usuário inativo",
                PrioridadeChamado.ALTA,
                1L
        );

        doThrow(new SolicitanteInativoException(1L))
                .when(usersApiClient)
                .validarSolicitante(1L);

        assertThrows(
                SolicitanteInativoException.class,
                () -> chamadoService.criar(request)
        );

        verify(chamadoRepository, never()).save(any(Chamado.class));
    }

}
