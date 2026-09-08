# Sistema de Chamados API

API REST para gerenciamento de chamados técnicos, desenvolvida com Java e Spring Boot.

O projeto permite criar, consultar, atualizar e excluir chamados, armazenando os dados em PostgreSQL.

## Tecnologias

- Java 21
- Spring Boot 4
- Spring Web MVC
- Spring Data JPA
- PostgreSQL
- Flyway
- Bean Validation
- OpenAPI e Swagger UI
- JUnit 5
- Mockito
- H2 para testes
- Maven

## Funcionalidades

- Criar chamado
- Listar chamados
- Buscar chamado por ID
- Atualizar os dados de um chamado
- Atualizar somente o status
- Excluir chamado
- Validar os dados de entrada
- Retornar erros de validação
- Persistir dados no PostgreSQL
- Documentar os endpoints com OpenAPI

## Estrutura

A aplicação está dividida em responsabilidades:

- `Controller`: recebe e responde às requisições HTTP.
- `Service`: executa as regras da aplicação.
- `Repository`: acessa o banco de dados.
- `Entity`: representa o chamado persistido.
- `Request DTOs`: representam e validam os dados recebidos pela API.
- `Flyway`: controla as alterações na estrutura do banco.

## Pré-requisitos

- Java 21
- PostgreSQL
- Git

Não é necessário instalar o Maven, pois o projeto possui Maven Wrapper.

## Banco de dados

Acesse o PostgreSQL com um usuário administrador e execute:

```sql
CREATE USER chamados_app WITH PASSWORD 'SUA_SENHA';
CREATE DATABASE sistema_chamados OWNER chamados_app;
```

Não coloque a senha no código ou em arquivos versionados.

## Configuração

No PowerShell, acesse a pasta do projeto e configure a senha:

```powershell
$senhaSegura = Read-Host "Senha de chamados_app" -AsSecureString
$ponte = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($senhaSegura)

try {
    $env:DB_PASSWORD = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($ponte)
} finally {
    [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($ponte)
}
```

A aplicação utiliza por padrão:

```text
Banco: sistema_chamados
Usuário: chamados_app
Servidor: localhost
Porta: 5432
```

## Executando a aplicação

No Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

A API ficará disponível em:

```text
http://localhost:8080
```

## Documentação

Com a aplicação em execução:

- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

## Endpoints

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/chamados` | Cria um chamado |
| `GET` | `/chamados` | Lista os chamados |
| `GET` | `/chamados/{id}` | Busca um chamado por ID |
| `PUT` | `/chamados/{id}` | Atualiza os dados do chamado |
| `PATCH` | `/chamados/{id}/status` | Atualiza somente o status |
| `DELETE` | `/chamados/{id}` | Exclui um chamado |

## Exemplo de criação

Requisição:

```http
POST /chamados
Content-Type: application/json
```

```json
{
  "titulo": "Computador não liga",
  "descricao": "O equipamento não apresenta nenhum sinal",
  "prioridade": "ALTA"
}
```

As prioridades aceitas são:

```text
BAIXA
MEDIA
ALTA
URGENTE
```

Todo chamado novo começa com o status `ABERTO`.

Os status disponíveis são:

```text
ABERTO
EM_ANDAMENTO
RESOLVIDO
FECHADO
```

## Executando os testes

```powershell
.\mvnw.cmd test
```

Os testes utilizam um banco H2 em memória. Portanto, não precisam da senha do PostgreSQL e não modificam os dados locais.

## Decisões técnicas

- O PostgreSQL gera os IDs por meio de `GenerationType.IDENTITY`.
- Os enums são persistidos como texto para facilitar a leitura no banco.
- O Flyway cria e versiona a estrutura do banco.
- DTOs separam os dados recebidos da entidade persistida.
- O H2 isola o ambiente de testes do banco utilizado pela aplicação.
- O Maven Wrapper mantém uma versão consistente do Maven entre ambientes.