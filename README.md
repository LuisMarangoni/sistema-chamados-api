# Sistema de Chamados API

[![CI](https://github.com/LuisMarangoni/sistema-chamados-api/actions/workflows/ci.yml/badge.svg)](https://github.com/LuisMarangoni/sistema-chamados-api/actions/workflows/ci.yml)

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
- Docker
- Docker Compose

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
- Paginar e ordenar chamados
- Filtrar chamados por status, prioridade e período de criação
- Executar API e PostgreSQL com Docker Compose
- Executar testes HTTP com MockMvc
- Vincular chamados a solicitantes por ID
- Filtrar chamados por solicitante
- Validar a existência do solicitante na Users API antes de criar um chamado

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
- Docker Desktop (para execução com containers)

Não é necessário instalar o Maven, pois o projeto possui Maven Wrapper.

## Executando com Docker

A forma recomendada de executar o projeto é com Docker Compose. Ele inicia a API e um PostgreSQL isolado, sem utilizar o banco instalado localmente.

Crie um arquivo `.env` na raiz do projeto com base no `.env.example`:

```text
DB_PASSWORD=defina_uma_senha_segura
```

Não envie o arquivo `.env` ao GitHub. Ele contém a senha local do banco Docker e já está ignorado pelo Git.

Inicie o ambiente:

```powershell
docker compose up --build
```

A API ficará disponível em:

```text
http://localhost:8080
```

O PostgreSQL Docker ficará disponível para acesso externo na porta `5433`:

```text
Host: localhost
Porta: 5433
Banco: sistema_chamados
Usuário: chamados_app
```

Para encerrar os containers sem apagar os dados:

```powershell
docker compose down
```


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

## Paginação e filtros

O endpoint de listagem aceita paginação, ordenação e filtros opcionais.

```http
GET /chamados?page=0&size=10&sort=dataCriacao,desc
GET /chamados?status=ABERTO
GET /chamados?prioridade=ALTA
GET /chamados?status=ABERTO&prioridade=ALTA
GET /chamados?dataInicio=2026-01-01T00:00:00
GET /chamados?dataFim=2026-12-31T23:59:59
GET /chamados?solicitanteId=1
```

- `page`: número da página, iniciado em `0`.
- `size`: quantidade de itens por página.
- `sort`: campo e direção da ordenação.
- `status`: `ABERTO`, `EM_ANDAMENTO`, `RESOLVIDO` ou `FECHADO`.
- `prioridade`: `BAIXA`, `MEDIA`, `ALTA` ou `URGENTE`.
- `dataInicio` e `dataFim`: datas ISO, por exemplo `2026-09-09T18:00:00`.
- `solicitanteId`: ID do usuário que abriu o chamado.

## Integração com Users API

Antes de criar um chamado, a API consulta `GET /usuarios/{id}` na Users API.

- Usuário existente: o chamado é criado.
- Usuário inexistente: a API retorna `404 Not Found`.
- A API de chamados armazena somente `solicitanteId`; ela não acessa diretamente o banco da Users API.

Para testar localmente, inicie a Users API em `http://localhost:8081`.

Ao executar a API de chamados com Docker Compose, a variável `USERS_API_URL` já está configurada para acessar a aplicação hospedada no Windows por `http://host.docker.internal:8081`.

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
  "prioridade": "ALTA",
  "solicitanteId": 1
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
- O chamado armazena `solicitanteId`, sem chave estrangeira para a Users API, pois cada API mantém seu próprio banco de dados.
- A validação do solicitante ocorre por HTTP com `UsersApiClient`, preservando a independência entre os bancos de dados.
