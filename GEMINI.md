GEMINI.MD: Contexto do Projeto "Achei Açaí"

1. Objetivo deste Ficheiro

Este documento serve como um "prompt de sistema" ou um ficheiro de contexto para o Gemini. Ele define a stack tecnológica, as decisões de arquitetura e o estado atual do projeto "Achei Açaí", um sistema de Ponto de Venda (PDV) full-stack.

2. Resumo da Aplicação

Projeto: "Achei Açaí"

Tipo: Aplicação Web de Ponto de Venda (PDV) e Gestão.

Funcionalidades:

Gestão de Cardápio: CRUD para Produtos, Complementos e Coberturas.

Ponto de Venda: Interface para criar vendas, adicionar itens a um carrinho, aplicar descontos/acréscimos e selecionar forma de pagamento.

Histórico de Vendas: Lista paginada de vendas passadas com um modal de detalhes (recibo).

Relatórios: Página de análise com filtros por data para ver volumes vendidos, totais financeiros, etc.

Segurança: Sistema de login com controlo de acesso baseado em funções (RBAC). Apenas ADMINs podem criar novos utilizadores.

3. Arquitetura e Stack Tecnológica

O projeto é desacoplado (API + SPA) e containerizado (Docker).

3.1. Back-end (API)

Tecnologia: Java 21, Servlets (Jakarta EE 10/11), Tomcat 11.

Abordagem: JDBC puro. Não usamos Spring. A lógica de negócio é implementada diretamente nos DAOs e Servlets.

Base de Dados: PostgreSQL.

Gestão de BD: Flyway para migrações automáticas (V1__..., V2__...). O Flyway é executado por um ServletContextListener (FlywayMigrationListener) no arranque da aplicação.

Segurança:

Autenticação: JWT (biblioteca jjwt). O LoginServlet emite o token.

Autorização: Um AuthenticationFilter protege todas as rotas (exceto /login), validando o token JWT. A autorização por função (ex: ADMIN) é tratada dentro de cada Servlet (ex: UsuarioServlet).

Senhas: Armazenadas como hash usando jBCrypt (PasswordUtil).

Configuração:

Segredos (DB_URL, JWT_SECRET_KEY) são lidos a partir de Variáveis de Ambiente.

Uma FabricaConexao centraliza a lógica de conexão, com fallback para database.properties (que está no .gitignore).

Deploy: A API é empacotada como ROOT.war (para correr na raiz do servidor) usando um Dockerfile multi-stage (Maven build -> Tomcat).

3.2. Front-end (SPA)

Tecnologia: React 18+ (com Hooks).

Ferramenta de Build: Vite.

Roteamento: react-router-dom para navegação entre páginas.

Gestão de Estado:

Estado Global: React Context API (useReducer). Temos dois contextos principais:

AuthContext: Gere o estado do utilizador (token, dados descodificados, isAuthenticated).

CartContext: Gere o estado do carrinho de compras (itens, adicionais, descontos, totais).

Estado Local: useState para controlos de UI (loading, formulários, etc.).

Comunicação API:

Toda a lógica fetch está isolada em src/services/.

Um apiClient.js centralizado intercepta todos os pedidos para adicionar o Authorization: Bearer <token> automaticamente.

Estilização: CSS Modules (.module.css) para evitar conflitos de estilos globais.

Deploy: O front-end é servido por um Dockerfile multi-stage (Node build -> NGINX).

3.3. Orquestração (Local)

Ferramenta: docker-compose.yml

Serviços: São definidos 3 serviços:

database: Imagem oficial postgres:16.

backend: Construído a partir do backend/Dockerfile.

frontend: Construído a partir do frontend/Dockerfile.

Comunicação: O apiClient.js do front-end é configurado via variável de ambiente VITE_API_BASE_URL (definida no docker-compose.yml) para encontrar o serviço backend (ex: http://backend:8080).

4. Regras de Interação (Para o Gemini)

Foco no "Porquê": Sempre explique por que uma decisão de arquitetura ou código está a ser tomada, fazendo referência aos nossos princípios (segurança, reutilização, DRY, etc.).

Usar os Nossos Componentes: Estou a usar componentes reutilizáveis (GerenciadorDeEntidade, Tabela, FormularioGenerico, Paginacao). Ao criar novas funcionalidades, priorize a reutilização ou o refinamento destas peças.

Formato do Código: Gere sempre blocos de código de texto simples. Nunca use a interface de "Canvas" ou de geração de ficheiros. Especifique sempre o caminho completo do ficheiro (ex: // frontend/src/pages/NovaPagina.jsx).

Contexto: Lembre-se desta arquitetura. Todos os pedidos futuros de código devem respeitar esta stack e estas decisões.

Depuração: Ao depurar, guie-me para encontrar os logs (Docker, Console do Navegador) e ajude-me a interpretar as mensagens de erro (como CORS, 401, 404, NullPointerException) no contexto da nossa arquitetura