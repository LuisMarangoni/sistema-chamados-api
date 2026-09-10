package sistema_chamados_api.infra;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.ProblemDetail;
import sistema_chamados_api.chamado.SolicitanteNaoEncontradoException;
import sistema_chamados_api.chamado.SolicitanteInativoException;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class TratadorGlobalDeErros {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErroValidacaoResponse tratarValidacao(
            MethodArgumentNotValidException exception
    ) {
        Map<String, String> campos = new LinkedHashMap<>();

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(erro ->
                        campos.put(
                                erro.getField(),
                                erro.getDefaultMessage()
                        )
                );

        return new ErroValidacaoResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Erro de validação",
                campos
        );
    }

    @ExceptionHandler(SolicitanteNaoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProblemDetail tratarSolicitanteNaoEncontrado(
            SolicitanteNaoEncontradoException exception
    ) {
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                exception.getMessage()
        );
    }

    @ExceptionHandler(SolicitanteInativoException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_CONTENT)
    public ProblemDetail tratarSolicitanteInativo(
            SolicitanteInativoException exception
    ) {
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.UNPROCESSABLE_CONTENT,
                exception.getMessage()
        );
    }

    @ExceptionHandler(UsersApiIndisponivelException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ProblemDetail tratarUsersApiIndisponivel(
            UsersApiIndisponivelException exception
    ) {
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.SERVICE_UNAVAILABLE,
                exception.getMessage()
        );
    }

}
