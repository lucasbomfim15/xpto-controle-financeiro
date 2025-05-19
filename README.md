
# XPTO - API de Controle Financeiro

Este projeto consiste em uma API REST para controle financeiro de clientes, incluindo funcionalidades como cadastro de clientes, contas bancárias, endereços, transações financeiras e geração de relatórios. Desenvolvido com **Java 17**, **Spring Boot 3**, **PostgreSQL**, e **Swagger para documentação interativa**, o sistema adota boas práticas de arquitetura e desenvolvimento orientado a domínio.

---

## 🚀 Tecnologias Utilizadas

- Java 17  
- Spring Boot 3  
- Spring Data JPA  
- Spring Web  
- PostgreSQL  
- Docker e Docker Compose  
- Swagger (springdoc-openapi)  
- JUnit e Mockito (testes unitários e de integração)  

---

## ⚙️ Como rodar o projeto

Certifique-se de ter o Docker e Docker Compose instalados. Em seguida:

```bash
# Clone o repositório
git clone https://github.com/seu-usuario/xpto-api.git
cd xpto-api

# Suba os containers (PostgreSQL + aplicação)
docker-compose up -d
```

## 🛠️ Executar função SQL no banco

A aplicação depende da função fn_calcula_saldo_cliente para calcular o saldo atual dos clientes. Para criá-la:

## ▶️ Usando Docker

Execute o seguinte comando para rodar o script que cria a função no banco de dados:

  docker exec -i xpto_postgres psql -U admin -d xpto_db < scripts/init-function.sql

Esse comando acessa o container xpto_postgres e executa o arquivo scripts/init-function.sql dentro do banco xpto_db.

## 💡 Alternativa manual
Se preferir, você pode acessar o terminal interativo do banco com:
  docker exec -it xpto_postgres psql -U admin -d xpto_db

E dentro do psql, execute:
  \i /caminho/absoluto/para/scripts/init-function.sql

Substitua /caminho/absoluto/... pelo caminho completo até o arquivo .sql no seu sistema.


A aplicação estará disponível em:  
📍 `http://localhost:8080`  
Swagger UI:  
📘 `http://localhost:8080/swagger-ui.html`

---

## 📚 Documentação da API

A seguir estão listadas as rotas organizadas por domínio:

### 🔁 Transactions

| Método | Rota                                               | Descrição                         |
|--------|----------------------------------------------------|-----------------------------------|
| GET    | `/api/v1/transactions`                             | Listar todas as transações        |
| POST   | `/api/v1/transactions`                             | Criar uma nova transação          |
| GET    | `/api/v1/transactions/customer/{customerId}`       | Listar transações por cliente     |
| GET    | `/api/v1/transactions/account/{accountId}`         | Listar transações por conta       |

---

### 🏠 Addresses

| Método | Rota                                               | Descrição                             |
|--------|----------------------------------------------------|---------------------------------------|
| GET    | `/api/v1/addresses/{id}`                           | Buscar um endereço por ID             |
| PUT    | `/api/v1/addresses/{id}`                           | Atualizar um endereço por ID          |
| DELETE | `/api/v1/addresses/{id}`                           | Deletar um endereço por ID            |
| GET    | `/api/v1/addresses`                                | Listar todos os endereços             |
| POST   | `/api/v1/addresses`                                | Criar um novo endereço                |
| GET    | `/api/v1/addresses/customer/{customerId}`          | Listar endereços por ID de cliente    |

---

### 💳 Accounts

| Método | Rota                                               | Descrição                             |
|--------|----------------------------------------------------|---------------------------------------|
| GET    | `/api/v1/accounts/{id}`                            | Buscar conta por ID                   |
| PUT    | `/api/v1/accounts/{id}`                            | Atualizar os dados de uma conta       |
| DELETE | `/api/v1/accounts/{id}`                            | Deletar conta (exclusão lógica)       |
| GET    | `/api/v1/accounts`                                 | Listar todas as contas                |
| POST   | `/api/v1/accounts`                                 | Criar uma nova conta                  |
| GET    | `/api/v1/accounts/customer/{customerId}`           | Buscar contas por cliente             |

---

### 📈 Reports

| Método | Rota                                                                   | Descrição                                         |
|--------|------------------------------------------------------------------------|---------------------------------------------------|
| GET    | `/api/v1/reports/revenue`                                              | Relatório de receita da empresa em um período     |
| GET    | `/api/v1/reports/customers/balance-summary`                           | Resumo de saldo dos clientes em uma data          |
| GET    | `/api/v1/reports/customer/{customerId}/balance`                       | Obter saldo atual de um cliente                   |
| GET    | `/api/v1/reports/customer/{customerId}/balance-period`                | Obter saldo de um cliente dentro de um período    |

---

### 👥 Customers

| Método | Rota                                               | Descrição                   |
|--------|----------------------------------------------------|-----------------------------|
| GET    | `/api/v1/customers/{id}`                           | Buscar cliente por ID       |
| PUT    | `/api/v1/customers/{id}`                           | Atualizar cliente           |
| DELETE | `/api/v1/customers/{id}`                           | Deletar cliente             |
| GET    | `/api/v1/customers`                                | Listar todos os clientes    |
| POST   | `/api/v1/customers`                                | Criar um novo cliente       |
| GET    | `/api/v1/customers/{id}/balance`                   | Consultar saldo do cliente  |

---

## ✅ Boas Práticas Adotadas

- **Padrão RESTful**: Organização de rotas e verbos HTTP de acordo com as boas práticas da arquitetura REST.
- **Validações e DTOs**: Uso de Data Transfer Objects para garantir segurança e clareza nas entradas e saídas da API.
- **Tratamento global de erros**: Exceções personalizadas e `@ControllerAdvice` para retorno consistente de erros.
- **Separação por camadas (Domain-Driven Design)**:
  - **Domain**: Entidades e regras de negócio.
  - **Repository**: Interfaces para acesso a dados via Spring Data JPA.
  - **Service**: Lógica de negócio.
  - **Controller**: Camada de exposição (REST).
- **Testes unitários**: Cobertura dos serviços com `JUnit` e `Mockito`.
- **Testes de Integração**: Cobertura dos controllers com `JUnit` e `MockMVC`.
- **Exclusão lógica**: Contas não são removidas fisicamente, apenas marcadas como inativas.
- **Swagger/OpenAPI**: Documentação viva da API, atualizada conforme a aplicação evolui.

---

## 🧠 Padrões de Projeto Utilizados

- **DTO (Data Transfer Object)**: Separação entre entidade e objeto de entrada/saída da API.
- **Service Layer**: Centralização da lógica de negócio.
- **Repository Pattern**: Abstração de acesso aos dados.
- **Factory Method** (em alguns testes ou utilitários).
- **Strategy (Regra de Taxa)**: Implementado na lógica de taxa de transações por período.

---

## 📌 Observações

- A aplicação pode ser testada via Swagger UI, Postman ou qualquer outro cliente HTTP.
- O backend está preparado para deploy em produção, sendo facilmente adaptável para uso com PostgreSQL em nuvem.

---

## 🧪 Testes

Para rodar os testes localmente:

```bash
./mvnw test
```

---

## 📬 Contato

Desenvolvido por **Lucas Bomfim do Nascimento**  
LinkedIn: [https://www.linkedin.com/in/lucas-bomfim15/](https://www.linkedin.com/in/lucas-bomfim15/)
