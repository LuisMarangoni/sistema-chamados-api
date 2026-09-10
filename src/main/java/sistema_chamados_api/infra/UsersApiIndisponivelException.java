package sistema_chamados_api.infra;

public class UsersApiIndisponivelException
        extends RuntimeException {

    public UsersApiIndisponivelException(Throwable cause) {
        super("Users API está indisponível", cause);
    }
}