# Projeto – API de Artistas e Álbuns

Projeto desenvolvido para o **Processo Seletivo – SEPLAG MT**, com foco em back-end Java e organização de infraestrutura.

A aplicação consiste em uma API REST para gerenciamento de artistas e álbuns, utilizando banco de dados relacional e armazenamento de arquivos, com ambiente totalmente containerizado via Docker.

A aplicação segue uma arquitetura em camadas, separando responsabilidades entre controllers (camada de exposição), services (regras de negócio), repositories (persistência) e entities (modelo de domínio). DTOs e mappers são utilizados para desacoplar o domínio da camada de transporte.

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

## Documentação da API (Swagger / OpenAPI)

A documentação interativa da API é gerada automaticamente via **springdoc-openapi** e exposta pelo Swagger UI.

- **Swagger UI**: interface gráfica para explorar e testar os endpoints

	http://localhost:8080/swagger-ui.html

	ou

	http://localhost:8080/swagger-ui/index.html

- **Documento OpenAPI em JSON** (utilizado pelo próprio Swagger UI e por ferramentas externas):

	http://localhost:8080/v3/api-docs

### Como testar a API pelo Swagger

1. Acesse o Swagger UI em um dos links acima.
2. Os endpoints estarão organizados por grupos (auth, artists, albums, regionals, etc.).
3. Para testar um endpoint:
	 - Clique sobre o endpoint desejado.
	 - Clique em **"Try it out"**.
	 - Preencha os parâmetros ou o corpo da requisição.
	 - Clique em **"Execute"** para enviar a requisição diretamente para a API.

### Autenticação via Swagger (JWT)

Alguns endpoints são protegidos e exigem **token JWT**. Para utilizá-los pelo Swagger:

1. Obtenha um token de acesso usando o endpoint de login:
	 - Endpoint: `POST /api/v1/auth/login`
	 - Body (exemplo):

### Usuário padrão para testes

Para facilitar a validação da API, a aplicação cria automaticamente um usuário padrão via Flyway Migrations:

- **Email:** test@example.com
- **Senha:** Password123

Esse usuário é utilizado apenas para autenticação e testes da API, não havendo endpoints públicos para gerenciamento de usuários.


	 - A resposta conterá um `accessToken` e um `refreshToken`.

2. Configure o token no botão **"Authorize"** do Swagger UI:
	 - Clique em **"Authorize"** (ícone de cadeado no topo da página).
	 - No campo de valor, preencha:

		 ```
		 Bearer SEU_ACCESS_TOKEN_AQUI
		 ```

	 - Clique em **"Authorize"** e depois em **"Close"**.

3. A partir desse momento, todas as requisições feitas pelo Swagger para endpoints protegidos irão incluir o cabeçalho `Authorization` com o token JWT informado.

4. Para renovar o token quando ele expirar, utilize o endpoint:
	 - `POST /api/v1/auth/refresh`, enviando o `refreshToken` no corpo ou no cabeçalho `Authorization` (Bearer).

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
    - Validação do fluxo de autenticação (login, refresh e logout).
    - Registro de login do usuário.
    - Normalização de e-mail e carregamento de usuários para autenticação (`UserDetailsServiceImpl`).

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

## Autenticação e Segurança

A aplicação utiliza autenticação baseada em JWT, com access token de curta duração (5 minutos) e refresh token para renovação segura da sessão.

O processo de refresh token rotation é implementado, garantindo que tokens antigos sejam invalidados após a renovação.

Para reforçar a segurança, a aplicação mantém uma blacklist de tokens inválidos, utilizada tanto no fluxo de refresh quanto no logout.

Endpoints de autenticação são públicos apenas quando necessário, mantendo os demais protegidos por Spring Security.

---

## Decisões de Arquitetura e Escopo

### Gerenciamento de Usuários

Embora a aplicação utilize um modelo de usuário para autenticação e controle de acesso (JWT e Rate Limit), não há endpoints públicos para gerenciamento de usuários.

Essa decisão foi tomada para manter a API aderente ao escopo do edital, cujo domínio é focado exclusivamente em Artistas e Álbuns. Expor CRUD de usuários não agrega valor aos casos de uso avaliados e aumentaria desnecessariamente a superfície de ataque da aplicação.

Para fins de teste e validação da API, um usuário padrão é criado automaticamente via Flyway Migrations, conforme descrito na seção de autenticação.



---
## Implementações Futuras

O modelo de usuário foi implementado como infraestrutura interna de autenticação. Caso haja necessidade futura de gerenciamento de usuários, a arquitetura já permite a exposição controlada desses endpoints sem impacto nas regras de negócio existentes, mantendo a separação de responsabilidades e a segurança da aplicação.


---
## Observações

O banco de dados e o armazenamento de arquivos utilizam volumes Docker para persistência.

Todas as credenciais e segredos estão centralizados no arquivo .env, seguindo boas práticas de configuração.

O projeto foi estruturado para facilitar a execução local e a avaliação técnica.