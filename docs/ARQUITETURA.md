# Arquitetura inicial do Sys-All

## Princípio central

O Sys-All é uma aplicação local em rede. A interface JavaFX será instalada nas máquinas de trabalho e se comunicará com um serviço no computador servidor por nome ou IP da rede local.

## Separação de módulos e dados

Cada módulo terá serviço e banco SQLite próprios. Na primeira entrega:

```text
Pagamentos Avulsos
  - Serviço: Sys-All Pagamentos
  - Banco: pagamentos-avulsos.db
```

Em uma entrega futura:

```text
Almoxarifado
  - Serviço: Sys-All Almoxarifado
  - Banco: almoxarifado.db
```

Os bancos não serão acessados por uma pasta compartilhada. Os computadores clientes falarão com o serviço pela rede local. Isso evita conflitos de gravação no SQLite e mantém os módulos independentes.

## Organização do código

```text
br.com.sysall
  application  inicialização do programa
  payments     regras de Pagamentos Avulsos
  shared       componentes reutilizáveis e configuração
  ui           telas JavaFX
```

## Situação atual

Esta primeira etapa cria a fundação do projeto e uma tela inicial de navegação. O banco, a comunicação em rede e as telas de cadastro serão implementados em etapas seguintes.
