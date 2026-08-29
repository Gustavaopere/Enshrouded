# Campo do Shroud e persistência

## Modelo lógico

O Shroud atual não é representado como uma varredura contínua de todos os blocos do mundo. Ele usa uma **grade lógica esparsa 3D**. Cada célula é identificada por `ShroudCellPos` e guarda:

- posição lógica;
- intensidade entre `0.0` e `1.0`;
- severidade (`ShroudSeverity`).

A intensidade rejeita NaN, infinito e valores fora de `[0, 1]`.

`ShroudGridGeometry` faz a conversão determinística entre coordenadas de bloco e células da grade. O tamanho da célula é parametrizado pelo runtime; a conversão usa divisão inteira com `floorDiv`, portanto coordenadas negativas permanecem simétricas e determinísticas.

## Organização por região

O estado do mundo mantém duas coleções principais:

- núcleos (`ShroudCoreState`);
- regiões (`ShroudRegionState`).

Cada região pertence a um núcleo. Essa relação é validada pelo runtime: um núcleo não pode operar sobre uma região pertencente a outro núcleo.

## Persistência

O estado canônico é persistido por `ShroudSavedData` e serializado por um codec versionado. Existe um `ShroudSchema` explícito e erro próprio para schema não suportado.

A fila de expansão **não** é o estado canônico. Ela é runtime-only. Após reinício, o sistema pode reconstruir trabalho de fronteira usando as células lógicas persistidas, sem precisar salvar uma fila potencialmente grande e transitória.

## Consequência prática

Duas ideias que parecem equivalentes não são:

- “o Shroud existe numa área” = fato lógico persistente;
- “há uma fila tentando expandir o Shroud” = detalhe transitório de execução.

Isso permite reiniciar o servidor sem transformar a fila de trabalho em uma segunda fonte de verdade.

## Sanctuary

O contrato de Foundation define Sanctuary como supressão efetiva. Uma área protegida pode aparecer/agir como segura para consultas apropriadas sem apagar as células lógicas originais. A persistência do Shroud e a proteção são, portanto, camadas separadas.
