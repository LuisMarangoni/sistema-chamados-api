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
- Registrar e consultar o histórico de alterações de status
- Consultar resumo quantitativo dos chamados por status

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
USERS_API_EMAIL=email_da_conta_de_integracao
USERS_API_PASSWORD=senha_da_conta_de_integracao
```

Não envie o arquivo `.env` ao GitHub. Ele contém as credenciais locais e já está ignorado pelo Git. `USERS_API_EMAIL` e `USERS_API_PASSWORD` correspondem ao login de uma conta ativa da Users API com perfil `SUPORTE` ou `ADMIN`, não ao usuário do PostgreSQL.

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
| `GET` | `/chamados/{id}/historico` | Lista o histórico de status do chamado |
| `DELETE` | `/chamados/{id}` | Exclui um chamado |
| `GET` | `/chamados/resumo` | Retorna a quantidade de chamados por status |

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

## Resumo dos chamados

O endpoint retorna a quantidade total de chamados e a distribuição por status:

```http
GET /chamados/resumo
```

## Histórico de status

Cada chamado registra um evento ao ser criado (`null → ABERTO`) e uma nova entrada sempre que o status realmente muda.

```http
GET /chamados/{id}/historico
```

O histórico é retornado em ordem cronológica e informa:

- status anterior;
- status novo;
- data da alteração.

Um chamado inexistente retorna `404 Not Found`. Solicitar o mesmo status atual não cria um evento duplicado.

## Integração com Users API

Antes de criar um chamado, a API consulta `GET /usuarios/{id}` na Users API.

- Usuário existente e ativo: o chamado é criado.
- Usuário inexistente: a API retorna `404 Not Found`.
- Usuário inativo: a API retorna `422 Unprocessable Content` e não cria o chamado.
- A API de chamados armazena somente `solicitanteId`; ela não acessa diretamente o banco da Users API.
- Users API indisponível ou com falha: a API retorna `503 Service Unavailable` e não cria o chamado.

Para testar localmente, inicie a Users API em `http://localhost:8081`.

### Autenticação e renovação do token

O `UsersApiClient` faz login em `POST /auth/login` usando `USERS_API_EMAIL` e `USERS_API_PASSWORD` e mantém o JWT em memória para reutilizá-lo nas consultas. A conta de integração precisa do perfil `SUPORTE` ou `ADMIN` para acessar `GET /usuarios/{id}`; o perfil `USUARIO` não é suficiente.

Se a consulta retornar `401 Unauthorized`, o cliente descarta o token rejeitado, faz login novamente e repete a consulta **uma única vez**. Caso outra execução já tenha renovado o token, ele reutiliza o token atualizado. Não é utilizado um endpoint de refresh token.

Se a segunda consulta também retornar `401`, a integração encerra a tentativa e a API de chamados responde `503 Service Unavailable`. Outros erros, como `403 Forbidden`, não provocam renovação. Um `401` no próprio login também encerra a tentativa, sem repetir o login indefinidamente.

Uma resposta de login sem corpo ou com token ausente, nulo, vazio ou contendo apenas espaços é tratada como indisponibilidade da integração (`503 Service Unavailable`). Nesse caso, o cliente não armazena o token nem realiza a consulta de usuário. Essa verificação valida a presença do token, não sua assinatura criptográfica.

Se alterar as credenciais no `.env`, recrie o container da API para carregar os novos valores:

```powershell
docker compose up -d --force-recreate api
```

Ao executar a API de chamados com Docker Compose, a variável `USERS_API_URL` já está configurada para acessar a aplicação hospedada no Windows por `http://host.docker.internal:8081`.

### Limites de tempo das chamadas HTTP

O cliente utiliza `SimpleClientHttpRequestFactory` com limites configuráveis, aplicados tanto ao login quanto à consulta de usuários:

| Variável de ambiente | Padrão | Finalidade |
|---|---|---|
| `USERS_API_CONNECT_TIMEOUT_MS` | `2000` | Limite para estabelecer a conexão, em milissegundos |
| `USERS_API_READ_TIMEOUT_MS` | `5000` | Limite de espera durante a leitura da resposta, em milissegundos |

Os valores devem ser positivos. Esses limites não representam um prazo total para a operação de criação de chamado, que pode envolver login, consulta e renovação do token.

Um timeout é tratado como indisponibilidade da integração, resultando em `503 Service Unavailable` sem criar o chamado. Não há repetição automática por timeout; a nova tentativa de consulta é exclusiva do tratamento de `401`.

Os padrões estão definidos em `application.properties`. Para personalizar em Docker, passe as variáveis explicitamente ao serviço `api` no `compose.yaml`; adicionar valores apenas ao `.env` do Compose não os injeta automaticamente no container.

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

O `UsersApiClientTest` utiliza `MockRestServiceServer` para simular a renovação após um `401` e o encerramento quando a consulta continua retornando `401` após a renovação.

Também verifica o tratamento de login com resposta sem corpo e, por meio de um teste parametrizado, token ausente, nulo, vazio ou contendo apenas espaços. As expectativas HTTP garantem que não haja consulta de usuário após essas respostas inválidas.

O cenário de timeout inicia um servidor HTTP temporário em `127.0.0.1`, em uma porta livre, e retém a resposta do login. O teste verifica que a causa original da falha é `SocketTimeoutException`, usando um timeout de leitura de 200 ms exclusivo do teste. O servidor é encerrado ao final, inclusive em caso de falha. Esse cenário valida o timeout de leitura do login, não o timeout de conexão nem a leitura da consulta de usuário.

Não é necessário iniciar as APIs, Docker ou fazer login para executar esses testes:

```powershell
.\mvnw.cmd "-Dtest=UsersApiClientTest" test
```

## Decisões técnicas

- O PostgreSQL gera os IDs por meio de `GenerationType.IDENTITY`.
- Os enums são persistidos como texto para facilitar a leitura no banco.
- O Flyway cria e versiona a estrutura do banco.
- DTOs separam os dados recebidos da entidade persistida.
- O H2 isola o ambiente de testes do banco utilizado pela aplicação.
- O Maven Wrapper mantém uma versão consistente do Maven entre ambientes.
- O chamado armazena `solicitanteId`, sem chave estrangeira para a Users API, pois cada API mantém seu próprio banco de dados.
- A validação do solicitante ocorre por HTTP com `UsersApiClient`, preservando a independência entre os bancos de dados.
- A Users API informa se o solicitante está ativo; chamados só podem ser criados para usuários ativos.
- Falhas de conexão ou respostas de erro da Users API são convertidas em `503 Service Unavailable`, evitando a criação de chamados sem solicitante validado.
- O histórico de status é persistido em tabela própria e consultado em ordem cronológica; alterações repetidas para o mesmo status não criam novos eventos.
