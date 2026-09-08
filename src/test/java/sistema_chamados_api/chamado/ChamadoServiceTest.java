package sistema_chamados_api.chamado;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChamadoServiceTest {

    @Mock
    private ChamadoRepository chamadoRepository;

    @InjectMocks
    private ChamadoService chamadoService;

    @Test
    void deveCriarChamadoComStatusAberto() {
        CriarChamadoRequest request = new CriarChamadoRequest(
                "Computador não liga",
                "Equipamento não apresenta sinal",
                PrioridadeChamado.ALTA
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
    }

}
