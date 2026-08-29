# Blocos, estado e contratos públicos

## Conteúdo registrado atualmente

O conteúdo físico confirmado neste checkpoint é concentrado no núcleo do Shroud:

- `enshrouded:shroud_core` — bloco do núcleo;
- block entity associada ao `ShroudCoreBlock`.

A presença de planos para corrupção, criaturas, Flame ou Lich não significa que seus blocos/itens/entidades finais estejam registrados hoje.

## Contratos de Foundation

A Foundation já define interfaces para que sistemas futuros conversem sem dependências cíclicas. Entre os limites já congelados estão:

- consulta de severidade/amostra do Shroud;
- autoridade e tipo de mutação;
- proprietário de progressão;
- consulta de passagem da Flame;
- consulta de ward da Flame;
- classificação mágica;
- manifestação do Lich e origem imutável de encontro.

Esses contratos são parte real do mod, mas vários ainda funcionam como seams de integração/arquitetura, e não como conteúdo completo visível ao jogador.

## Por que isso importa para a wiki

Uma classe de contrato pode existir antes da feature concreta que a consumirá. Por isso esta wiki usa três categorias:

- **runtime jogável/registrado** — está conectado ao mod e pode afetar o jogo;
- **infraestrutura implementada** — código real e testado, mas ainda sem feature final consumidora;
- **planejado** — somente especificação/plano; não entra como feature atual.

Essa separação é especialmente importante para Flame e Lich no estado atual do projeto.
