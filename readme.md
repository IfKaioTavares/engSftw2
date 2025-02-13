
# Comandos para configurar e rodar o SonarQube

### 1. Rodando o SonarQube com Docker

Execute o seguinte comando para rodar o SonarQube com a configuração desejada:

```bash
docker run -d --name sonarqube -e SONAR_ES_BOOTSTRAP_CHECKS_DISABLE=true -p 9000:9000 sonarqube:latest
```

- **Login:** admin
- **Senha:** admin

### 2. Criar um projeto local

- Após logar no SonarQube, clique em **"Create a local project"**.
- Na próxima tela, insira o **nome do projeto**, a **chave (key)** e a **branch principal**.
- Selecione a opção **"Use the global setting"** e clique no botão **Create project**.

### 3. Configuração do projeto

- Na tela do projeto que você acabou de criar no SonarQube, clique no botão **"Locally"**.
- Selecione a opção de rodar com **Maven**.
- Copie o comando gerado e execute-o no terminal do seu projeto para rodar a análise do SonarQube.