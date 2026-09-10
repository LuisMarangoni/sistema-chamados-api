package sistema_chamados_api.chamado;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;



import java.util.Optional;

@RestController
@RequestMapping("/chamados")

public class ChamadoController {

    private final ChamadoService chamadoService;

    public ChamadoController(ChamadoService chamadoService) {
        this.chamadoService = chamadoService;
    }

    @GetMapping
    public Page<Chamado> listar(
            @RequestParam(required = false)
            StatusChamado status,

            @RequestParam(required = false)
            PrioridadeChamado prioridade,

            @RequestParam(required = false)
            Long solicitanteId,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime dataInicio,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime dataFim,

            @PageableDefault(
                    size = 10,
                    sort = "dataCriacao",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable
    ) {
        return chamadoService.listar(
                status,
                prioridade,
                solicitanteId,
                dataInicio,
                dataFim,
                pageable
        );

    }

    @GetMapping("/{id}")
     public ResponseEntity<Chamado> buscarPorId(@PathVariable Long id) {
        Optional<Chamado> resultado = chamadoService.buscarPorId(id);

        if (resultado.isPresent()) {
            return ResponseEntity.ok(resultado.get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/historico")
    public ResponseEntity<List<HistoricoStatusChamado>>
    listarHistorico(@PathVariable Long id) {
        Optional<List<HistoricoStatusChamado>> resultado =
                chamadoService.listarHistorico(id);

        if (resultado.isPresent()) {
            return ResponseEntity.ok(resultado.get());
        }

        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Chamado> atualizarStatus(
            @PathVariable Long id,
            @Valid @RequestBody AtualizarStatusRequest request

    ) {
        Optional<Chamado> resultado =
                chamadoService.atualizarStatus(id, request.status());

        if (resultado.isPresent()) {
            return ResponseEntity.ok(resultado.get());
        }

        return ResponseEntity.notFound().build();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Chamado criar(
            @Valid @RequestBody CriarChamadoRequest request
    ) {
        return chamadoService.criar(request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        boolean excluido = chamadoService.excluir(id);

        if (excluido) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }
    @PutMapping("/{id}")
    public ResponseEntity<Chamado> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody AtualizarChamadoRequest request
    ) {
        Optional<Chamado> resultado =
                chamadoService.atualizar(id, request);

        if (resultado.isPresent()) {
            return ResponseEntity.ok(resultado.get());
        }

        return ResponseEntity.notFound().build();
    }

}
