# 07 — Client Experience

**Goal:** render a clear, atmospheric and accessible Shroud experience without giving the client gameplay authority.

## Task order

1. [`01 HUD`](01-hud.md)
2. [`02 fog/rendering`](02-fog-rendering.md)
3. [`03 audio/particles`](03-audio-particles.md)
4. [`04 accessibility/config`](04-accessibility.md)

## Runtime contracts

- All visuals consume synchronized server state.
- Vanilla/NeoForge rendering is the baseline; optional render libraries are enhancements only.
- Effects scale down cleanly for accessibility/performance.

Tasks are implemented and merged in the order above unless `STATUS.md` explicitly records a reviewed dependency change.
