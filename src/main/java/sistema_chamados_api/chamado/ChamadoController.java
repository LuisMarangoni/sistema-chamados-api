package sistema_chamados_api.chamado;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;


@RestController
@RequestMapping("/chamados")

public class ChamadoController {

 @GetMapping
 public List<Chamado> listar() {
     Chamado chamado = new Chamado(
             1L,
             "Computador não liga",
             "O computador não apresenta nenhum sinal"
     );

     return List.of(chamado);
 }



}
