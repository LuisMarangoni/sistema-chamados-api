package sistema_chamados_api.chamado;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chamados")

public class ChamadoController {
 @GetMapping

    public String listar(){

        return "Lista de chamados";
    }



}
