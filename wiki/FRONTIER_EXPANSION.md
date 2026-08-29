# Expansão da fronteira

## Como a expansão atual funciona

A expansão do Shroud é lógica, determinística e limitada por orçamento. `ShroudExpansionScheduler` não carrega chunks e não faz leitura de material do terreno para decidir a propagação atual.

A política canônica disponível hoje é `ShroudPropagationPolicy.terrainNeutral()`.

### Intensidade

Para uma célula candidata dentro do raio do núcleo:

1. calcula-se a distância 3D entre o centro da célula e o centro do núcleo;
2. aplica-se atenuação radial `1 - distância/raio`;
3. aplica-se ruído estável derivado de `expansionSeed` e das coordenadas da célula;
4. o fator de ruído fica entre `0.90` e `1.00`;
5. o resultado final é limitado a `[0, 1]`.

O ruído é determinístico: a mesma seed e a mesma coordenada produzem o mesmo valor.

### Vizinhança

A política visita seis vizinhos ortogonais por célula:

- +X / -X;
- +Y / -Y;
- +Z / -Z.

Não há diagonal nesse algoritmo atual.

## Dupla defesa de raio

O limite de influência é aplicado em duas camadas:

- a política de propagação retorna zero fora do raio;
- o próprio scheduler repete a checagem geométrica antes de aceitar/processar candidatos.

Isso impede que uma implementação futura de política com bug consiga, sozinha, fazer a fronteira escapar do raio máximo do núcleo.

## Orçamento e justiça entre núcleos

`ShroudWorkBudget` limita trabalho global por tick e trabalho por núcleo. O scheduler mantém uma ordem estável dos núcleos ativos e um `nextCoreHint`, evitando que um único núcleo monopolize indefinidamente o orçamento quando vários possuem trabalho pendente.

O resultado de cada tick registra:

- entradas processadas;
- células realmente aplicadas;
- trabalho processado por núcleo.

## Fila limitada e recuperação

Cada núcleo possui uma `ShroudFrontier` de capacidade fixa. Se nem todos os vizinhos elegíveis couberem, isso não cria uma fila auxiliar ilimitada.

Quando uma fila fica vazia, o runtime pode reconstruir candidatos a partir das células já persistidas da região. Essa reconstrução:

- usa apenas estado lógico conhecido;
- não escaneia chunks;
- não cria uma segunda fila infinita;
- é controlada por epoch para não refazer continuamente o mesmo trabalho esgotado.

## Epoch

Entradas da fronteira carregam o `expansionEpoch` do núcleo. Uma entrada antiga, pertencente a outro epoch, é consumida mas não aplicada. Isso fornece uma forma de invalidar trabalho transitório obsoleto sem tratar a fila como estado persistente.

## Estado atual versus Terrain Corruption

A expansão descrita nesta página materializa **células lógicas de Shroud**. Ela ainda não significa que blocos do terreno serão convertidos visualmente em corrupção. A transformação física do mundo pertence ao Stage 02 — Terrain Corruption, ainda não implementado neste checkpoint.
