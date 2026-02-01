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

Para visualizar os logs da aplicação:

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

## Testes automatizados

A aplicação possui uma suíte de **testes unitários** construída com **JUnit 5** e **Mockito**, focada em validar as regras de negócio dos serviços sem subir o contexto completo do Spring Boot.

### Como executar os testes

No diretório raiz do projeto, execute:

```bash
./mvnw test
```

Ou, no Windows (caso o script `.cmd` seja utilizado):

```bash
mvnw.cmd test
```

Também é possível rodar testes específicos de uma classe, por exemplo:

```bash
./mvnw -Dtest=ArtistServiceImplTest test
./mvnw -Dtest=AlbumServiceImplTest test
```

> Observação: existem alguns testes de integração que dependem do contexto completo do Spring e de infraestrutura (banco/Flyway). Esses testes foram anotados com `@Disabled` para que não impactem a execução dos testes unitários.

### O que os testes cobrem

Os testes foram organizados por serviço, cobrindo principalmente:

- **Artistas e Álbuns**
	- Criação, atualização, busca e paginação de artistas e álbuns.
	- Validações de regras de negócio (ex.: entidades não encontradas, conflitos, etc.).
	- Notificações via WebSocket quando um novo álbum é cadastrado.

- **Relacionamentos N:N (Artista x Gênero / Artista x Álbum)**
	- Criação e remoção de vínculos entre artistas e gêneros.
	- Criação e remoção de vínculos entre artistas e álbuns.
	- Tratamento de conflitos (vínculo já existente) e cenários de entidades não encontradas.

- **Regionais**
	- Sincronização de regionais com serviço externo (Argus).
	- Inativação de regionais locais que não existem mais na fonte externa.
	- Criação/atualização de regionais e mapeamento para DTOs de resposta.

- **Capas de Álbuns (MinIO)**
	- Upload de capas para o MinIO com definição de capa primária.
	- Listagem de capas com geração de URLs pré-assinadas.
	- Busca da capa primária de um álbum e tratamento de casos em que não existe.

- **Usuários e Autenticação**
	- Criação, atualização, desativação e registro de login de usuários (`AppUserServiceImpl`).
	- Normalização de e-mail, validação de conflito de e-mail e uso do `PasswordEncoder`.
	- Carregamento de usuários para autenticação (`UserDetailServiceImpl`).

- **Gêneros Musicais**
	- CRUD de gêneros, com ordenação por nome.
	- Busca filtrada por nome com ordenação ascendente/descendente.

- **Auditoria**
	- Registro de logs de auditoria (`AuditLogServiceImpl`) com:
		- Serialização de dados antigos/novos em JSON.
		- Associação com o usuário autenticado, IP e User-Agent da requisição quando disponíveis.
		- Garantia de que falhas de auditoria **não quebram** o fluxo principal da aplicação.

Além disso, há testes específicos para **JWT** (`JwtUtils`), validando geração de tokens e tratamento de tokens expirados.


## Demonstração do MinIO

Para comprovar o funcionamento do armazenamento de arquivos, a aplicação utiliza o MinIO para persistir as capas de álbuns.

As URLs pré-assinadas possuem validade temporária e expiram automaticamente após o período configurado.

Os arquivos são armazenados em um bucket privado e o acesso é realizado por meio de URLs pré-assinadas, geradas pela própria API, permitindo o download temporário das capas sem a exposição de credenciais de acesso.

Durante os testes, foi validado que o download ocorre corretamente quando a URL pré-assinada é utilizada conforme gerada pela aplicação. Requisições realizadas com métodos HTTP diferentes do método assinado são rejeitadas pelo MinIO, confirmando o funcionamento esperado do mecanismo de segurança.

A validação do funcionamento pode ser realizada a partir do terminal, diretamente no container da aplicação:

```bash
docker exec -it artists_api sh
```

```bash
curl -O "URL_PRE_ASSINADA_GERADA_PELA_API"
```

O MinIO também disponibiliza uma interface web administrativa, acessível pelo navegador em:

http://localhost:9001/

Nessa interface, é possível realizar o login utilizando as credenciais configuradas no ambiente da aplicação, permitindo a visualização dos buckets e dos arquivos armazenados.

---

## Demonstração do WebSocket

Para comprovar o funcionamento do WebSocket responsável por notificar o front-end a cada novo álbum cadastrado, foi disponibilizado um arquivo HTML simples, acessível diretamente pela própria API.

Esse arquivo tem como único objetivo demonstrar, de forma objetiva, a comunicação em tempo real via WebSocket, sem a implementação de um front-end completo, uma vez que o escopo do projeto é exclusivamente back-end.

Após subir a aplicação, a demonstração pode ser acessada em:

http://localhost:8080/ws-test.html

Ao cadastrar um novo álbum por meio da API, o evento será imediatamente exibido na tela, validando o envio e o recebimento das notificações em tempo real.

---

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

---

## Observações

O banco de dados e o armazenamento de arquivos utilizam volumes Docker para persistência.

Todas as credenciais e segredos estão centralizados no arquivo .env, seguindo boas práticas de configuração.

O projeto foi estruturado para facilitar a execução local e a avaliação técnica.