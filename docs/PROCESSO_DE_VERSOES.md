# Processo de versões e entregas

## Identificação da versão

O formato adotado será `MAIOR.MENOR.CORRECAO`.

- `1.0.0`: primeira entrega do módulo Pagamentos Avulsos.
- `1.1.0`: nova funcionalidade compatível com os dados existentes.
- `1.1.1`: correção sem nova funcionalidade relevante.
- `2.0.0`: mudança importante que exige atenção especial na atualização.

## Antes de instalar uma atualização

1. Criar uma cópia do banco de dados do cliente.
2. Verificar a versão que está instalada.
3. Registrar as mudanças no `CHANGELOG.md`.
4. Gerar um instalador identificado com a versão e a data.
5. Testar a atualização com uma cópia dos dados antes de aplicá-la no cliente.

## Registro de entrega

Para cada entrega, manter em `releases/<versão>/`:

- `NOTAS-DA-VERSAO.md` com as mudanças para o cliente;
- data da entrega;
- instruções de instalação ou atualização;
- checksum do instalador, quando houver;
- observação de que o backup foi realizado.

Não guardar o banco real do cliente nem o instalador final no repositório Git.
