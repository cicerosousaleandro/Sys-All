# Sys-All

Sistema modular de gestão local em rede. A primeira entrega é o módulo **Pagamentos Avulsos**, voltado ao registro semanal de pagamentos em espécie para trabalhadores sem vínculo empregatício.

## Versão em desenvolvimento

`v1.0.0 - Pagamentos Avulsos`

## Como executar durante o desenvolvimento

Pré-requisitos: Java 21 e Maven.

```powershell
mvn -s .mvn\settings.xml clean javafx:run
```

O parâmetro `-s .mvn\settings.xml` usa uma configuração do projeto para guardar as dependências na pasta local `.m2/`, evitando uma limitação de permissão do computador de desenvolvimento. Essa pasta é ignorada pelo Git.

## Estrutura

- `src/main/java`: código-fonte Java.
- `src/main/resources`: arquivos de interface e configuração que acompanham o programa.
- `docs`: decisões, processo de versões e documentação do produto.
- `releases`: registros de cada versão entregue; instaladores não são enviados ao repositório.
- `data`, `backups` e `logs`: criadas na instalação do cliente e ignoradas pelo Git.

## Segurança dos dados

O banco de produção, cópias de segurança, relatórios reais e credenciais nunca devem ser incluídos no Git. Cada cliente terá dados separados e o banco do módulo Pagamentos Avulsos não será compartilhado com o módulo Almoxarifado.
