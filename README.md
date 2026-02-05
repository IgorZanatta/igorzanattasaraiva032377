# Projeto – API de Artistas e Álbuns

## Informações do Processo Seletivo

- **Processo Seletivo:** PROCESSO SELETIVO CONJUNTO Nº 001/2026/SEPLAG/SEFAZ/SEDUC/SESP/PJC/PMMT/CBMMT/DETRAN/POLITEC/SEJUS/SEMA/SEAF/SINFRA/SECITECI/PGE/MTPREV
- **Vaga:** Engenheiro da Computação – Sênior
- **Candidato:** Igor Zanatta Saraiva
- **Anexo Selecionado:** ANEXO II-A – Projeto Desenvolvedor Back End
- **Projeto:** Projeto Prático – Implementação Back End Java Sênior

---

Projeto desenvolvido para o **Processo Seletivo – SEPLAG MT**, com foco em back-end Java e organização de infraestrutura.

A aplicação consiste em uma API REST para gerenciamento de artistas e álbuns, utilizando banco de dados relacional e armazenamento de arquivos, com ambiente totalmente containerizado via Docker.

A aplicação segue uma arquitetura em camadas, separando responsabilidades entre controllers (camada de exposição), services (regras de negócio), repositories (persistência) e entities (modelo de domínio). DTOs e mappers são utilizados para desacoplar o domínio da camada de transporte.

---

## Tecnologias Utilizadas

- Java 21
- Spring Boot
- Spring Security
- JWT
- Flyway
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

### Variáveis de ambiente

O arquivo `.env` não é versionado e está incluído no `.gitignore`, pois contém informações sensíveis.

Para facilitar a execução do projeto, foi disponibilizado um arquivo `.env.example`, contendo todas as variáveis necessárias com valores de exemplo.

Antes de subir a aplicação, copie o arquivo:

```bash
cp .env.example .env
```

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

### Usuário padrão para testes

Para facilitar a validação da API, a aplicação cria automaticamente um usuário padrão via Flyway Migrations:

- **Email:** test@example.com
- **Senha:** Password123

Esse usuário é utilizado apenas para autenticação e testes da API, não havendo endpoints públicos para gerenciamento de usuários.




2. Configure o token no botão **"Authorize"** do Swagger UI:
	 - Clique em **"Authorize"** (ícone de cadeado no topo da página).
	 - No campo de valor, preencha:

		 ```
		 Bearer SEU_ACCESS_TOKEN_AQUI
		 ```

	 - Clique em **"Authorize"** e depois em **"Close"**.

3. A partir desse momento, todas as requisições feitas pelo Swagger para endpoints protegidos irão incluir o cabeçalho `Authorization` com o token JWT informado.

4. Para renovar o token quando ele expirar, utilize o endpoint:
	 - Enviando o `refreshToken` no corpo ou no cabeçalho `Authorization` (Bearer).

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

A aplicação possui **55 testes automatizados** com cobertura das principais regras de negócio e fluxos críticos, construídos com **JUnit 5**, **Mockito** e **H2 Database** para testes de integração.

### Como executar os testes

No diretório raiz do projeto, execute:

```bash
./mvnw test
```

Ou, no Windows:

```bash
mvnw.cmd test
```

### Cobertura de testes

**✅ 55 testes passando (100% habilitados)**

#### Testes Unitários com Mockito
- **Serviços de domínio**: validação de regras de negócio sem dependências externas
- **Controllers**: validação de endpoints e fluxos de autenticação
- **Segurança**: geração e validação de tokens JWT

#### Testes de Integração com H2
- **Contexto Spring**: carregamento completo da aplicação com banco H2 em memória
- **JPA/Hibernate**: criação automática de schema (substitui Flyway em testes)

### O que é testado

**Artistas e Álbuns**
- CRUD completo com validação de entidades não encontradas
- Filtros, paginação e ordenação alfabética
- Notificações WebSocket na criação de álbuns

**Relacionamentos N:N**
- Vínculos entre artistas, gêneros e álbuns
- Prevenção de duplicatas e validação de integridade

**Regionais**
- Sincronização com API externa (Argus)
- Estratégia de inativação (sem exclusão física)

**Capas de Álbuns**
- Upload para MinIO e definição de capa primária
- Geração de URLs pré-assinadas temporárias

**Autenticação e Segurança**
- Fluxo completo de login, refresh e logout
- Token rotation e blacklist de tokens invalidados
- Validação e expiração de tokens JWT

**Auditoria**
- Rastreamento de operações com contexto de usuário e requisição
- Resiliência (não quebra fluxo principal em caso de falha)


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

### Autenticação do WebSocket

**O endpoint WebSocket (`/ws`) está público para facilitar testes e demonstração:**

- **Liberado para testes**: Permite conexão sem token JWT para facilitar validação durante desenvolvimento e avaliação
- **Demonstração funcional**: O arquivo `ws-test.html` pode conectar diretamente sem autenticação
- **Produção**: Recomenda-se implementar autenticação JWT no handshake do WebSocket


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

## Rate Limiting

A aplicação implementa rate limiting por usuário, limitando o consumo da API a **10 requisições por minuto**, conforme exigido no edital.

A estratégia utilizada garante:
- controle por usuário autenticado
- proteção contra abuso e negação de serviço
- resposta adequada quando o limite é excedido


## Configuração CORS (Cross-Origin Resource Sharing)

**A aplicação implementa bloqueio rigoroso de origens (CORS), atendendo ao requisito de segurança do edital:**

**Comportamento atual:**

- **Bloqueio total por padrão**: Qualquer origem não autorizada é **bloqueada**
- **Localhost liberado apenas para testes**: `http://localhost:3000` está permitido exclusivamente para facilitar testes locais durante desenvolvimento e avaliação
- **Produção preparada**: Domínio oficial SEPLAG comentado e pronto para ativação


---

## Decisões de Arquitetura e Escopo

### Gerenciamento de Usuários

O modelo de usuário é utilizado exclusivamente como infraestrutura de autenticação e controle de acesso (JWT e Rate Limit).

Não há endpoints públicos para gerenciamento de usuários, pois o edital tem como foco o domínio de Artistas e Álbuns. Expor CRUD de usuários não agregaria valor aos casos de uso avaliados e aumentaria desnecessariamente a superfície de ataque da aplicação.

Para facilitar testes e validação da API, um usuário padrão é criado automaticamente via Flyway Migrations.


### Artistas

O módulo de Artistas representa uma entidade central do domínio.

Foram expostos apenas endpoints de criação, atualização e listagem, com filtros por nome, tipo e ordenação alfabética, conforme solicitado no edital.
Operações de remoção não foram expostas para evitar ambiguidades de domínio e preservar consistência histórica.

Os relacionamentos com álbuns e gêneros são tratados de forma controlada, evitando que a API se torne excessivamente permissiva ou complexa.


### Álbuns

O módulo de Álbuns implementa criação, atualização, remoção e listagem paginada, atendendo diretamente aos requisitos do edital.

A listagem suporta filtros individuais por:

- título (busca parcial),

- ano de lançamento,

- tipo de artista associado (SOLO ou BAND).

A paginação e ordenação são realizadas via Spring Data Pageable.

Consultas por ID e listagens agregadas com artistas foram removidas para manter consistência com o módulo de Artistas e evitar operações fora do escopo principal avaliado.

A criação de um álbum dispara uma notificação via WebSocket, permitindo comunicação em tempo real com o front-end.


### Gêneros Musicais

O módulo de Gêneros foi implementado como um catálogo de apoio, permitindo a categorização de artistas por estilo musical.

Foram mantidos apenas endpoints de criação e listagem, com busca por nome e ordenação alfabética.
Não há operações de remoção ou atualização expostas, seguindo a mesma estratégia de simplificação aplicada aos demais módulos.

O relacionamento entre artistas e gêneros é modelado via tabela de junção N:N, permitindo flexibilidade e futuras extensões sem impactar o domínio principal.


### Capas de Álbuns

As capas de álbuns são tratadas em um módulo específico e armazenadas externamente no MinIO, com apenas as referências persistidas no banco de dados.

A implementação garante que:

- a primeira capa enviada seja automaticamente definida como capa primária,

- URLs pré-assinadas sejam geradas com tempo de expiração,

- o acesso aos arquivos seja controlado e seguro.

Essa abordagem separa o armazenamento físico da lógica de negócio, favorecendo escalabilidade e segurança.

---

### Regionais

O módulo de Regionais atende ao requisito sênior de sincronização com sistema externo.

As regionais são importadas a partir do serviço Argus e mantidas localmente com controle de ativação.

A sincronização é executada automaticamente a cada 20 minutos, mantendo os dados atualizados sem necessidade de intervenção manual. O endpoint `/api/v1/regionais/sincronizar` permanece disponível para forçar atualizações imediatas quando necessário.

O processo de sincronização:

- insere novos registros,

- atualiza dados existentes,

- inativa registros que não existem mais na fonte externa.

Essa estratégia evita exclusões físicas e garante integridade referencial.

---

### Relacionamentos N:N

A aplicação implementa relacionamentos muitos-para-muitos (**N:N**), conforme exigido no edital, entre:

- **Artistas e Álbuns**
- **Artistas e Gêneros**

O relacionamento entre **Artistas e Álbuns** é do tipo **N:N**, conforme exigido no edital, sendo implementado por meio de **tabela de junção explícita**, garantindo **integridade referencial**, **controle de duplicidade** e **registro temporal do vínculo**.

A gestão desses relacionamentos é realizada na camada de serviço, evitando a exposição desnecessária de endpoints técnicos e mantendo o domínio consistente e controlado.

> **Observação de design:**  
> Embora o relacionamento entre Artistas e Álbuns seja do tipo **N:N** e esteja plenamente implementado no domínio, a API não expõe endpoints públicos para listar artistas de um álbum ou álbuns de um artista.  
> Essa decisão foi tomada para evitar acoplamento excessivo, payloads desnecessários e impactos de performance, mantendo a API alinhada ao escopo do edital.  
> O requisito de “expor quais álbuns são/tem cantores e/ou bandas” é atendido por meio de filtros parametrizados na listagem de álbuns.

---

### Auditoria

A aplicação possui uma infraestrutura de auditoria implementada e funcional, projetada para registrar operações relevantes com informações detalhadas, como:

- entidade afetada,
- tipo de operação,
- estado anterior e novo,
- usuário responsável e contexto da requisição (IP, User-Agent).

Atualmente, a auditoria é aplicada ao **registro de login de usuários**.

A infraestrutura está pronta (tabela, entidade, serviço e repositório) para expansão a outras operações do domínio sem refatorações estruturais.

---

## Implementações Futuras

A arquitetura foi projetada visando escalabilidade e evolução do domínio, permitindo a adição de novas funcionalidades sem impacto significativo nas regras de negócio existentes.

Entre as possíveis evoluções estão:

- Gerenciamento controlado de usuários, com definição de perfis e permissões, mantendo a separação entre infraestrutura de autenticação e regras de domínio.

- Ampliação dos relacionamentos do domínio, como:

  - associação de artistas a bandas,

  - histórico de participação de artistas em bandas (período de atuação, ano de entrada e saída),

  - suporte a múltiplos papéis de um artista ao longo do tempo.

- Uso estratégico de soft delete, por meio do campo active, permitindo manter registros históricos relevantes para análises internas e auditoria, sem remoções físicas.

- Expansão da auditoria, aproveitando os campos de createdAt e updatedAt para análises temporais, rastreabilidade completa e conformidade.

- Evolução do uso de gêneros musicais, possibilitando análises mais detalhadas, como:

  - gêneros mais associados a álbuns,

  - tendências de estilos musicais ao longo do tempo,

  - apoio a mecanismos de recomendação e busca avançada.

- Relatórios e consultas analíticas, explorando dados históricos para apoiar tomada de decisão e visualização de métricas.

Essas extensões podem ser implementadas de forma incremental, aproveitando a modelagem atual baseada em entidades bem definidas, relacionamentos explícitos e separação clara de responsabilidades.

---

## Considerações sobre Escopo e Aderência ao Edital

Todas as funcionalidades e requisitos descritos no edital para o **ANEXO II-A – Projeto Desenvolvedor Back End (PROJETO PRÁTICO - IMPLEMENTAÇÃO BACK END JAVA SÊNIOR)** foram integralmente implementados e estão disponíveis para validação.

As seções de *Implementações Futuras* descritas neste README não correspondem a requisitos pendentes do edital, mas sim a **evoluções arquiteturais opcionais**, propostas para demonstrar a capacidade de expansão do sistema em um cenário de produção real, sem impacto nas funcionalidades exigidas.

---

## Observações

O banco de dados e o armazenamento de arquivos utilizam volumes Docker para persistência.

Todas as credenciais e segredos estão centralizados no arquivo .env, seguindo boas práticas de configuração.

O projeto foi estruturado para facilitar a execução local e a avaliação técnica.