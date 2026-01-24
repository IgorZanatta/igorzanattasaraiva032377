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

## Observações

O banco de dados e o armazenamento de arquivos utilizam volumes Docker para persistência.

Todas as credenciais e segredos estão centralizados no arquivo .env, seguindo boas práticas de configuração.

O projeto foi estruturado para facilitar a execução local e a avaliação técnica.