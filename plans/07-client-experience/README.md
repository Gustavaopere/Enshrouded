# 07 — Client Experience

**Goal:** render a clear, atmospheric and accessible Shroud experience without giving the client gameplay authority.

**Progress:** 1/4 tasks complete.

## Task order

1. [`✅ 01 HUD + shared client config`](✅-01-hud.md)
2. [`02 fog/rendering`](02-fog-rendering.md)
3. [`03 audio/particles`](03-audio-particles.md)
4. [`04 accessibility presets/validation`](04-accessibility.md)

## Runtime contracts

- All visuals consume synchronized server state.
- Task 01 establishes the single shared `EnshroudedClientConfig`; Tasks 02/03 consume their sections and Task 04 adds accessibility presets/cross-setting validation. No later task registers a parallel client config.
- Client config is presentation-only and cannot weaken or alter server-authoritative mechanics.
- The Exposure HUD holds the latest server-authored reserve until a newer snapshot arrives; it does not manufacture gameplay depletion from wall-clock time.
- Vanilla/NeoForge rendering is the baseline; optional render libraries are enhancements only.
- Effects scale down cleanly for accessibility/performance.

Tasks are implemented and merged in the order above unless `STATUS.md` explicitly records a reviewed dependency change.
