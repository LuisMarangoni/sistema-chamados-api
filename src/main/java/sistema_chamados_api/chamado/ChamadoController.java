package sistema_chamados_api.chamado;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;


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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Chamado criar(@RequestBody CriarChamadoRequest request) {
        return chamadoService.criar(request);
    }

}
