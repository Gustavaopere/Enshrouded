# 07 — Client Experience

**Goal:** render a clear, atmospheric and accessible Shroud experience without giving the client gameplay authority.

**Progress:** 4/4 tasks complete.

## Task order

1. [`✅ 01 HUD + shared client config`](✅-01-hud.md)
2. [`✅ 02 fog/rendering`](✅-02-fog-rendering.md)
3. [`✅ 03 audio/particles`](✅-03-audio-particles.md)
4. [`✅ 04 accessibility presets/validation`](✅-04-accessibility.md)

## Runtime contracts

- All visuals consume synchronized server state.
- Task 01 establishes the single shared `EnshroudedClientConfig`; Tasks 02/03 consume their sections and Task 04 adds accessibility presets/cross-setting validation. No later task registers a parallel client config.
- Client config is presentation-only and cannot weaken or alter server-authoritative mechanics.
- The Exposure HUD holds the latest server-authored reserve until a newer snapshot arrives; it does not manufacture gameplay depletion from wall-clock time.
- Fog rendering consumes the same synchronized Exposure state, advances transient interpolation once per rendered frame, resets at logout and never overrides underwater/lava/powder-snow fog; disabling it or setting intensity to zero leaves vanilla/other-mod fog ownership intact.
- Vanilla/NeoForge rendering is the baseline; optional render libraries are enhancements only.
- Accessibility profiles resolve coordinated effective presentation values through the same shared config, preserve a readable HUD/non-color Deadly warning at the lowest-effects preset and reset transient fog/audio/particle state when that config loads or reloads.
- Effects scale down cleanly for accessibility/performance without changing Exposure, damage, progression, passage requirements or logical Shroud state.

All four Stage 07 tasks are implemented, merged and independently verified on `main`.
