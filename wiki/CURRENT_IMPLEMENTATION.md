# Estado atual e limites

## Implementado no runtime atual

O mod já possui uma base NeoForge 1.21.1 funcional e testada, estado persistente por dimensão para o Shroud, ciclo de vida de núcleos físicos e uma expansão lógica limitada por orçamento.

### Foundation

A Foundation consolidou contratos independentes de implementação para consulta do Shroud, autoridade de mutação, progressão/Flame, wards, classificação mágica e manifestações de Lich. Esses contratos existem para impedir que estágios posteriores acoplem diretamente sistemas que ainda não foram implementados.

Um detalhe relevante é a semântica de Sanctuary: a proteção é tratada como uma **sobreposição efetiva**. Ela pode suprimir o efeito percebido do Shroud, mas não apaga o campo lógico subjacente.

### Stage 01 — Shroud Field

Implementado e verificado:

1. **Estado e persistência** — `ShroudWorldState`, regiões, células e núcleos persistem por `SavedData`, com schema versionado.
2. **Core lifecycle** — núcleos seguem um ciclo de vida estrito e possuem bloco/block entity próprios.
3. **Bounded frontier expansion** — somente núcleos elegíveis expandem, por uma grade lógica esparsa e com filas/limites de trabalho explícitos.

Ainda não concluído no Stage 01:

- consulta/sincronização final de zonas;
- seeding canônico de núcleos.

## O que NÃO deve ser tratado como feature atual

Os diretórios de plano existem para Terrain Corruption, Exposure, Corrupted Ecology, Flame Progression, Lich & Story, Client Experience, Integrations e Hardening, mas esses estágios ainda não estão implementados no checkpoint desta wiki.

Consequentemente, a wiki não afirma atualmente que existam:

- corrupção visual/material completa do terreno;
- cronômetro de exposição jogável e morte por permanência;
- ecologia corrompida completa;
- progressão funcional da Flame;
- encontros jogáveis de Lich;
- HUD final do Shroud;
- integrações finais com outros mods.

## Fonte de verdade

Para gameplay documentado, a precedência é:

1. código registrado/wired no runtime;
2. testes e GameTests que comprovam o contrato;
3. recursos/configs efetivamente carregados;
4. `plans/STATUS.md` para maturidade;
5. planos futuros apenas como fronteira de escopo.
