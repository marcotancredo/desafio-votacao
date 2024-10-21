# Pré-requisitos e Instruções para o Funcionamento da Aplicação

## 1. Pré-requisitos
- **Java 17** deve estar instalado no ambiente.
- **Docker** deve estar instalado para criar o banco de dados e executar a aplicação.

## 2. Executando a Aplicação com Docker

Na raiz do projeto, abra o terminal e execute:

```bash
docker-compose up -d
```

### 2.1. Executando Componentes Separados
- **Somente banco de dados:**
  ```bash
  docker-compose up database -d
  ```
- **Somente a aplicação:**
  ```bash
  docker-compose up app -d
  ```

## 3. Executando a Aplicação Manualmente (Sem Docker)

Caso não deseje usar Docker para a aplicação, siga os passos abaixo:

### 3.1. Gerando o JAR com Maven
1. No terminal, na raiz do projeto, execute:
   ```bash
   mvn clean package
   ```
   Isso irá baixar as dependências necessárias e gerar o JAR.

2. A pasta `target` será criada com o arquivo `desafiovotacao-0.0.1-SNAPSHOT.jar`.

### 3.2. Executando o JAR
- Navegue até a pasta `target` no terminal e execute:
  ```bash
  java -jar desafiovotacao-0.0.1-SNAPSHOT.jar
  ```

## 4. Alterando a Porta da Aplicação

Se a porta padrão `8080` estiver ocupada, você pode alterar a configuração no arquivo `src/main/resources/application.yml`. Modifique o valor de `server.port` para a porta desejada e repita os passos descritos no item [3](#3-executando-a-aplicação-manualmente-sem-docker).

## 5. Testando a API

Use uma ferramenta de testes de API para realizar as requisições. Exemplos:

- **Insomnia**:  
  Se estiver utilizando o [Insomnia](https://insomnia.rest/download), há uma collection disponível para importação no caminho:  
  `src/main/resources/insomnia/Insomnia_2024-10-21.json`

## 6. Documentação dos Endpoints

A documentação Swagger da API estará disponível após a execução da aplicação no seguinte endereço:  
[Swagger UI](http://localhost:8080/desafio/api/swagger-ui/index.html#/)

# Versionamento de API na URL

## Principais Vantagens

### 1. Clareza
O versionamento via URL torna explícito qual versão da API está sendo utilizada, facilitando o entendimento tanto para desenvolvedores quanto para clientes. Como o número da versão (`/v1`, `/v2`) está diretamente na URL, é fácil para qualquer pessoa identificar a versão ativa, o que ajuda no diagnóstico de problemas, comunicação entre equipes e na manutenção. O cliente sabe exatamente o que esperar de cada versão, evitando surpresas com mudanças inesperadas.

### 2. Compatibilidade retroativa
Uma das maiores vantagens desse modelo é a capacidade de manter diferentes versões da API ativas ao mesmo tempo. Isso garante que os clientes que dependem de uma versão específica da API possam continuar usando-a, mesmo que novas versões sejam lançadas. Com a versão anterior ainda disponível, os consumidores têm tempo para se adaptar e migrar para novas versões sem interromper o serviço ou causar falhas em suas integrações.

Por exemplo, se um cliente estiver usando `/v1`, ele não será afetado pelas mudanças feitas em `/v2`, como alterações em parâmetros, formatos de retorno ou lógica de negócios.

### 3. Controle sobre mudanças
Ao lançar uma nova versão (como `/v2`), a API pode introduzir mudanças significativas sem medo de quebrar a compatibilidade com usuários da versão anterior. Isso oferece maior flexibilidade para fazer melhorias profundas, como refatorações, mudanças de arquitetura, novos métodos ou a remoção de recursos desatualizados, sem comprometer os usuários da versão anterior.

Além disso, permite que as alterações sejam feitas de maneira controlada. Qualquer modificação que quebraria a compatibilidade pode ser implementada em uma nova versão, enquanto as atualizações menores e compatíveis retroativamente podem ser aplicadas na versão atual.

### 4. Facilidade de implementação
O versionamento via URL é relativamente fácil de implementar, pois se trata apenas de adicionar um prefixo à rota da API. Não exige mudanças complexas na lógica do servidor ou na infraestrutura. A simplicidade desse método também facilita o gerenciamento de diferentes versões no código-fonte, separando claramente as funcionalidades que pertencem a cada uma.

Por exemplo, a equipe de desenvolvimento pode organizar o código por versões, como `/v1` e `/v2`, mantendo cada uma em seus próprios controladores ou pastas, tornando a manutenção de múltiplas versões gerenciável.

Cada versão da API pode ter sua própria documentação, explicando as diferenças e novidades, assim como o comportamento esperado, o que melhora a experiência do usuário e desenvolvedor.