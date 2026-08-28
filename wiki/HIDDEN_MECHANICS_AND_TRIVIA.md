# Mecânicas não óbvias e trivia

Esta página reúne comportamentos confirmados pelo código que podem não aparecer diretamente em texto de jogo.

## O Shroud atual é 3D

A grade possui X, Y e Z e expande verticalmente além de horizontalmente. Os seis vizinhos ortogonais incluem `+Y` e `-Y`.

## O Shroud não depende do terreno para existir

A política atual é explicitamente `terrainNeutral`. Ela usa núcleo, geometria, coordenada e seed. Não lê bloco, bioma ou material para decidir a intensidade. Isso é intencional: Terrain Corruption poderá projetar o campo lógico no mundo depois, sem tornar o campo dependente dessa projeção.

## A borda não é perfeitamente lisa

A intensidade é radial, mas recebe um fator de ruído determinístico entre 90% e 100%. O objetivo atual é quebrar uniformidade sem introduzir aleatoriedade dependente da ordem dos ticks.

## A fila não é o Shroud

A fronteira em memória pode desaparecer num restart e ser reconstruída. As células persistidas são a verdade canônica. Isso evita salvar backlog transitório e impede que uma falha de fila apague o campo.

## Expansão possui defesa em profundidade

Mesmo que uma política de propagação aceite por engano uma célula distante, o scheduler possui sua própria checagem de raio. O hard radius não depende de uma única classe.

## Um núcleo destruído para de expandir imediatamente no modelo

`expansionEligible()` é verdadeiro somente em `ACTIVE`. Frontiers pertencentes a núcleos ausentes ou não elegíveis são descartadas pelo scheduler.

## `DESTROYED` não é `PURIFIED`

O modelo preserva essa diferença deliberadamente. A limpeza/purificação futura pode ocorrer depois da destruição sem falsificar que o campo nunca existiu.

## Registro repetido pode ser seguro ou erro

Registrar de novo o mesmo UUID com os mesmos dados é idempotente. Reutilizar o UUID com centro, região, tier, raio ou seed diferentes é tratado como colisão e lança erro.

## Um núcleo dormente não pode ser removido se já houver campo

O caminho `discardDormant` exige que a região esteja vazia. É uma trava de integridade para evitar que uma operação de bootstrap/rollback remova células reais.

## Sanctuary não apaga corrupção

A semântica já congelada é de supressão. O campo lógico latente continua existindo sob a proteção. Isso permitirá, quando os consumidores correspondentes existirem, diferenciar “área limpa” de “área corrompida atualmente suprimida”.

## Limites são clamps, não lore

Raio 16–512 e trabalho 1–512 por tick são limites técnicos atuais. Eles podem ser usados por configuração/runtime, mas não representam tiers narrativos da Flame ou do Shroud.
