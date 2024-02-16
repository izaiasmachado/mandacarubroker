# Mandacaru Broker - API

## Descrição

O projeto Mandacaru Broker API é uma aplicação Spring Boot que fornece operações CRUD (Create, Read, Update, Delete) para gerenciar informações sobre ações (stocks).

## Recursos

### Listar Todas as Ações

Retorna uma lista de todas as ações disponíveis.

**Endpoint:**

```http
GET /stocks
```

### Obter uma Ação por ID

Retorna os detalhes de uma ação específica com base no ID.

**Endpoint:**

```http
GET /stocks/{id}
```

### Criar uma Nova Ação

Cria uma nova ação com base nos dados fornecidos.

**Endpoint:**

```http
POST /stocks
```

**Corpo da Solicitação (Request Body):**

```JSON
{
  "symbol": "BBAS3",
  "companyName": "Banco do Brasil SA",
  "price": 56.97
}

```

### Atualizar uma Ação por ID

Atualiza os detalhes de uma ação específica com base no ID.

**Endpoint:**

```http
PUT /stocks/{id}
```

**Corpo da Solicitação (Request Body):**

```JSON
{
  "symbol": "BBAS3",
  "companyName": "Banco do Brasil SA",
  "price": 59.97
}

```

### Excluir uma Ação por ID

Exclui uma ação específica com base no ID.

**Endpoint:**

```http
DELETE /stocks/{id}
```

## Uso

1. Clone o repositório: `git clone https://github.com/izaiasmachado/mandacarubroker.git`
2. Siga o tutorial para [criar seu arquivo `.env` e subir um banco PostgreSQL](./docs/tutorials/setup-postgresql-docker-compose.md)
3. Importe o projeto em sua IDE preferida **(caso use o IntelliJ):** [realize o tutorial para setar variáveis de ambiente](./docs/tutorials/setup-dotenv-variables-intellij.md) e também [configure o CheckStyle](./docs/tutorials/setup-checkstyle-plugin-intellij.md)
4. Execute o aplicativo Spring Boot
5. Acesse a API em `http://localhost:8080`

## Requisitos

- Java 17 ou superior
- Maven
- Docker (Opcional para subir o banco de dados)

## Tecnologias Utilizadas

- Maven
- Spring Boot
- JUnit5
- PostgreSQL

## Contribuições

Contribuições são bem-vindas!

## Licença

Este projeto está licenciado sob a [Licença MIT](LICENSE).
