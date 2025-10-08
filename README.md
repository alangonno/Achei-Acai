# Achei Açaí
## Projeto Controle de Estoque e Vendas

![React](https://img.shields.io/badge/React-20232A?style=for-the-badge&logo=react&logoColor=61DAFB)
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)

## 📖 Sobre o Projeto

O **Achei Açaí** é uma aplicação web full-stack projetada para ser um sistema completo de Ponto de Venda (PDV) e gestão para lojas de açaí e estabelecimentos similares. O sistema permite o registro de vendas, a gestão do cardápio (produtos, complementos, coberturas) e a análise de dados através de relatórios detalhados.

O projeto foi desenvolvido com uma arquitetura moderna e desacoplada, consistindo numa API RESTful no back-end e numa Aplicação de Página Única (SPA) no front-end.

---

## ✨ Funcionalidades Principais

* **Ponto de Venda (PDV):** Interface intuitiva para registar novas vendas, adicionar produtos e adicionais a um carrinho dinâmico, aplicar descontos/acréscimos e selecionar a forma de pagamento.
* **Gestão de Cardápio:** CRUD completo para produtos, complementos e coberturas. Permitindo que os administradores adicionem novos usuarios.
* **Histórico de Vendas:** Visualização de todas as vendas registadas, com um modal para ver os detalhes completos de cada transação.
* **Relatórios Gerenciais:** Geração de relatórios por intervalo de datas, incluindo:
    * Faturamento total e por forma de pagamento.
    * Volume de produtos (Açaí, Sorvete) vendidos em litros, detalhado por variação.
    * Contagem total de cada complemento e cobertura vendidos.
* **Sistema de Autenticação e Autorização:**
    * Login seguro com senhas criptografadas (BCrypt).
    * Gestão de sessão via JSON Web Tokens (JWT).
    * Controlo de acesso baseado em funções (RBAC), com permissões distintas para `ADMIN` e `OPERADOR`.

---

## 🛠️ Tecnologias Utilizadas

A aplicação é dividida em dois componentes principais:

#### **Back-end (API)**

* **Linguagem:** Java 21
* **Servidor:** Apache Tomcat 11
* **Frameworks/APIs:** Servlets (Jakarta EE)
* **Base de Dados:** PostgreSQL
* **Persistência:** JDBC puro para controlo de transações.
* **Migrações de BD:** Flyway
* **Segurança:** jBCrypt para hashing de senhas, JJWT para JSON Web Tokens.
* **Build Tool:** Apache Maven
* **Containerização:** Docker

#### **Front-end (SPA)**

* **Biblioteca:** React 18+
* **Ferramenta de Build:** Vite
* **Gestão de Estado:** React Context API e `useReducer`
* **Roteamento:** React Router DOM
* **Estilização:** CSS Modules
* **Comunicação com API:** Fetch API

---

## 🚀 Como Executar o Projeto Localmente

A forma mais fácil e recomendada de executar a aplicação completa é utilizando o **Docker** e o **Docker Compose**.

### Pré-requisitos

* [Docker Desktop](https://www.docker.com/products/docker-desktop/) instalado e em execução.
* [Git](https://git-scm.com/) para clonar o repositório.

### Passos para a Execução

1.  **Clone o repositório:**
    ```bash
    git clone [https://github.com/seu-usuario/Achei-Acai.git](https://github.com/seu-usuario/Achei-Acai.git)
    cd Achei-Acai/acheiacai
    ```

2.  **Configure as Variáveis de Ambiente:**
    O `docker-compose.yml` já contém as variáveis necessárias. Apenas certifique-se de que as portas `5432` (PostgreSQL), `8080` (Back-end) e `5173` (Front-end) estão livres na sua máquina.

3.  **Suba os Contentores:**
    Na raiz do projeto (onde está o `docker-compose.yml`), execute o seguinte comando. A flag `--build` garante que as imagens do Docker serão construídas a partir do código-fonte mais recente.

    ```bash
    docker-compose up --build
    ```
    Aguarde alguns minutos para que o Docker descarregue as imagens base, construa os seus projetos e inicie os três contentores. O Flyway irá configurar a sua base de dados PostgreSQL automaticamente na primeira vez.

4.  **Aceda à Aplicação:**
    * **Front-end (Interface Principal):** Abra o seu navegador e aceda a `http://localhost:5173`
    * **Back-end (API):** A API estará disponível em `http://localhost:8080`
    * **Base de Dados:** Pode conectar-se à base de dados PostgreSQL usando as credenciais do `docker-compose.yml` em `localhost:5432`.

### Credenciais Padrão

Para o primeiro acesso, altere o script de migração `V2` para cria um utilizador lembre-se de adicionar um usuario ADMIN com a senha em hash na migração do flyway

---

Feito por Alan Gonçalves
