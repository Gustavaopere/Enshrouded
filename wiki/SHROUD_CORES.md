# Núcleos do Shroud

## Bloco físico

O runtime registra atualmente `shroud_core`, um `ShroudCoreBlock` com block entity própria. O bloco é configurado com resistência física alta (`strength(5.0, 1200.0)`) e `PushReaction.BLOCK`, portanto não é tratado como um bloco comum movível por pistões.

## Estado lógico de um núcleo

`ShroudCoreState` liga o objeto físico/lógico a:

- UUID do núcleo;
- centro em coordenadas de bloco;
- tier;
- estado de ciclo de vida;
- raio máximo de influência;
- seed de expansão;
- epoch de expansão;
- UUID da região que o núcleo possui.

## Ciclo de vida

A máquina de estados atual é deliberadamente unilateral:

`DORMANT -> ACTIVE -> DESTROYED -> PURIFIED`

Não existem transições de retorno. Em particular:

- `DORMANT` só pode virar `ACTIVE`;
- `ACTIVE` só pode virar `DESTROYED`;
- `DESTROYED` só pode virar `PURIFIED`;
- `PURIFIED` é terminal.

Somente `ACTIVE` é `expansionEligible()`.

## Registro e colisões

`ShroudCoreService.registerDormant` cria simultaneamente o núcleo e sua região vazia. O serviço rejeita:

- UUID de região já pertencente a outro registro;
- dois núcleos diferentes no mesmo centro;
- reutilização do mesmo UUID com dados de registro diferentes.

Repetir exatamente o mesmo registro é idempotente.

Um núcleo dormente pode ser descartado apenas se sua região ainda estiver vazia. O serviço se recusa a descartar um núcleo dormente que já possua células lógicas, evitando apagar campo existente por um caminho de limpeza inadequado.

## Limites de segurança atuais

`CoreSafetyLimits` define clamps de servidor:

| Parâmetro | mínimo | padrão | máximo |
|---|---:|---:|---:|
| raio máximo de influência | 16 | 128 | 512 |
| trabalho de crescimento por tick | 1 | 32 | 512 |

Esses valores são limites de segurança do código atual, não promessa de balanceamento final.

## Destruição versus purificação

Destruir e purificar são etapas diferentes no modelo. O Stage 01 implementa a forma legal do ciclo, mas o gatilho de gameplay que transforma `DESTROYED` em `PURIFIED` pertence a um estágio posterior. Assim, não se deve interpretar a existência do estado `PURIFIED` como uma mecânica de purificação jogável já concluída.
