package sistema_chamados_api.chamado;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;



import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/chamados")

public class ChamadoController {

    private final ChamadoService chamadoService;

    public ChamadoController(ChamadoService chamadoService) {
        this.chamadoService = chamadoService;
    }
    @GetMapping
        public List<Chamado> listar() {

     return chamadoService.listar();
 }

    @GetMapping("/{id}")
     public ResponseEntity<Chamado> buscarPorId(@PathVariable Long id) {
        Optional<Chamado> resultado = chamadoService.buscarPorId(id);

        if (resultado.isPresent()) {
            return ResponseEntity.ok(resultado.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Chamado> atualizarStatus(
            @PathVariable Long id,
            @RequestBody AtualizarStatusRequest request
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
    public Chamado criar(@RequestBody CriarChamadoRequest request) {
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

}
