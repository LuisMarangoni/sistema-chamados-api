package sistema_chamados_api.chamado;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ChamadoRepositoryTest {

    @Autowired
    private ChamadoRepository chamadoRepository;

    @Test
    void deveFiltrarChamadosPorStatusEPrioridade() {
        Chamado chamadoAlta = new Chamado(
                "Servidor indisponível",
                "Servidor não responde",
                PrioridadeChamado.ALTA
        );

        Chamado chamadoMedia = new Chamado(
                "Impressora offline",
                "Impressora não responde",
                PrioridadeChamado.MEDIA
        );

        chamadoRepository.saveAll(
                List.of(chamadoAlta, chamadoMedia)
        );

        Specification<Chamado> filtros = Specification.allOf(
                ChamadoSpecifications.comStatus(
                        StatusChamado.ABERTO
                ),
                ChamadoSpecifications.comPrioridade(
                        PrioridadeChamado.ALTA
                )
        );

        Page<Chamado> resultado = chamadoRepository.findAll(
                filtros,
                PageRequest.of(0, 10)
        );

        assertEquals(1, resultado.getTotalElements());
        assertEquals(
                PrioridadeChamado.ALTA,
                resultado.getContent().getFirst().getPrioridade()
        );
        assertEquals(
                StatusChamado.ABERTO,
                resultado.getContent().getFirst().getStatus()
        );
    }

    @Test
    void deveFiltrarChamadosPorPeriodoDeCriacao() {
        LocalDateTime dataInicio =
                LocalDateTime.now().minusMinutes(1);

        Chamado chamado = new Chamado(
                "Falha no servidor",
                "Serviço indisponível",
                PrioridadeChamado.URGENTE
        );

        chamadoRepository.saveAndFlush(chamado);

        LocalDateTime dataFim =
                LocalDateTime.now().plusMinutes(1);

        Specification<Chamado> filtros = Specification.allOf(
                ChamadoSpecifications.criadoAPartirDe(dataInicio),
                ChamadoSpecifications.criadoAte(dataFim)
        );

        Page<Chamado> resultado = chamadoRepository.findAll(
                filtros,
                PageRequest.of(0, 10)
        );

        assertEquals(1, resultado.getTotalElements());
        assertEquals(
                "Falha no servidor",
                resultado.getContent().getFirst().getTitulo()
        );
    }

}
