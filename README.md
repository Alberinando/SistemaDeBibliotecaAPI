# Sistema Biblioteca API

Esta API é desenvolvida com Spring Boot e integra diferentes módulos para gerenciar o sistema de biblioteca, incluindo operações de empréstimos, funcionários, histórico, livros e membros.

## Pré-requisitos

- **JDK 17:** Necessário para compilar e executar a aplicação.
- **Apache Maven:** Usado no gerenciamento de dependências e na construção do projeto.
- **PostgreSQL:** Banco de dados configurado manualmente:
  - **Banco:** sistemaBibliotecaDB
  - **Usuário:** postgres
  - **Senha:** 123456
- **Porta 8088:** A aplicação será executada nesta porta. Certifique-se de que ela esteja livre.

## Endpoints

### Emprestimos
- **GET** `/v1/emprestimos`
- **GET** `/v1/emprestimos/{id}`
- **POST** `/v1/emprestimos`
- **PUT** `/v1/emprestimos`
- **DELETE** `/v1/emprestimos/{id}`

### Funcionarios
- **GET** `/v1/funcionario`
- **GET** `/v1/funcionario/{id}`
- **POST** `/v1/funcionario`
- **PUT** `/v1/funcionario`
- **DELETE** `/v1/funcionario/{id}`
- **POST** `/v1/funcionario/auth`

### Historico
- **GET** `/v1/historico`
- **GET** `/v1/historico/{id}`

### Livros
- **GET** `/v1/livros`
- **GET** `/v1/livros/{id}`
- **GET** `/v1/livros/list`
- **POST** `/v1/livros`
- **PUT** `/v1/livros`
- **DELETE** `/v1/livros/{id}`

### Membros
- **GET** `/v1/membros`
- **GET** `/v1/membros/{id}`
- **GET** `/v1/membros/list`
- **POST** `/v1/membros`
- **PUT** `/v1/membros`
- **DELETE** `/v1/membros/{id}`

## Executando a Aplicação

1. **Compile e construa o projeto com Maven:**
   ```bash
   mvn clean install
   ```

2. **Execute a aplicação:**
  ```bash
  mvn spring-boot:run
  ```

  - Ou gerando o jar executável:
  ```bash
  java -jar target/sistema-biblioteca-api-0.0.1-SNAPSHOT.jar
  ```

Certifique-se de que o PostgreSQL esteja em execução e que o banco sistemaBibliotecaDB tenha sido criado com as credenciais indicadas.

## Informações Adicionais

- Configuração da Aplicação:
  Os parâmetros de conexão com o banco e a porta do servidor estão no arquivo src/main/resources/application.properties.
- Cross-Origin:
  Todos os endpoints estão configurados para aceitar requisições de qualquer origem (CORS liberado).
- Autenticação:
  O endpoint /v1/funcionario/auth é utilizado para autenticar funcionários e gerar um token para futuras requisições.

Utilize o exemplo de payload fornecido para efetuar o login:
  ```bash
  {
     "login": "Adm",
     "senha": "123456"
  }
  ```
