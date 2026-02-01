# Projeto – API de Artistas e Álbuns

Projeto desenvolvido para o **Processo Seletivo – SEPLAG MT**, com foco em back-end Java e organização de infraestrutura.

A aplicação consiste em uma API REST para gerenciamento de artistas e álbuns, utilizando banco de dados relacional e armazenamento de arquivos, com ambiente totalmente containerizado via Docker.

---

## Tecnologias Utilizadas

- Java 21
- Spring Boot
- PostgreSQL 16
- MinIO
- Docker e Docker Compose

---

## Pré-requisitos

Antes de executar o projeto, é necessário ter instalado:

- Docker
- Docker Compose (já incluso nas versões atuais do Docker Desktop)

Não é necessário instalar Java, Maven ou PostgreSQL localmente.

---

## Configuração inicial

O projeto utiliza variáveis de ambiente centralizadas no arquivo `.env`.

Certifique-se de que o arquivo `.env` esteja presente na raiz do projeto antes de executar os comandos.  
Esse arquivo já contém todas as configurações necessárias para banco de dados, MinIO e autenticação JWT.

---

## Como executar o projeto

### 1. Subir toda a infraestrutura e a API

No diretório raiz do projeto, execute:

```bash
docker compose up --build -d
```

Esse comando irá criar a rede Docker do projeto, subir o PostgreSQL, subir o MinIO, realizar o build da aplicação Spring Boot e iniciar a API já conectada aos serviços.

---

## Verificação dos serviços

Para verificar se todos os containers estão em execução, utilize o comando:

```bash
docker compose ps
```

Todos os serviços devem aparecer com status Running ou Healthy.

---

## Acessos principais

- API disponível em: http://localhost:8080
- Console do MinIO disponível em: http://localhost:9001

O PostgreSQL estará disponível na porta **5432**, permitindo acesso via ferramentas como DBeaver ou pgAdmin, se necessário.

---

## Comandos úteis

Para ver os log da aplicação:

```bash
docker logs -f artists_api
```

Para parar todos os serviços:

```bash
docker compose down
```

Para parar todos os serviços e remover volumes (reset completo do ambiente):

```bash
docker compose down -v
```

Para parar somente a API:

```bash
docker compose stop api
```

Para subir somente a API:

```bash
docker compose up -d api
```

Para rebuildar somente a API:

```bash
docker compose up --build -d api
```

---

## Demonstração do WebSocket

Para comprovar o funcionamento do WebSocket responsável por notificar o front-end a cada novo álbum cadastrado, foi disponibilizado um arquivo HTML simples, acessível diretamente pela própria API.

Esse arquivo tem como único objetivo demonstrar, de forma objetiva, a comunicação em tempo real via WebSocket, sem a implementação de um front-end completo, uma vez que o escopo do projeto é exclusivamente back-end.

Após subir a aplicação, a demonstração pode ser acessada em:

http://localhost:8080/ws-test.html

Ao cadastrar um novo álbum por meio da API, o evento será imediatamente exibido na tela, validando o envio e o recebimento das notificações em tempo real.

## Health Checks, Liveness e Readiness

A aplicação expõe endpoints de verificação de saúde (*Health Checks*) utilizando o **Spring Boot Actuator**, permitindo que ferramentas de monitoramento e infraestrutura verifiquem o estado da API.

Os seguintes endpoints estão disponíveis após a inicialização da aplicação:

- Health Check geral:

http://localhost:8080/actuator/health

- Liveness Probe (verifica se a aplicação está viva):

http://localhost:8080/actuator/health/liveness

- Readiness Probe (verifica se a aplicação está pronta para receber requisições):

http://localhost:8080/actuator/health/readiness

Esses endpoints estão expostos sem autenticação, conforme boas práticas, para permitir monitoramento e verificação de disponibilidade da aplicação.


## Observações

O banco de dados e o armazenamento de arquivos utilizam volumes Docker para persistência.

Todas as credenciais e segredos estão centralizados no arquivo .env, seguindo boas práticas de configuração.

O projeto foi estruturado para facilitar a execução local e a avaliação técnica.