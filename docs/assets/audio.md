# Enshrouded audio assets

Stage 07.03 ambient audio is original project material generated procedurally from synthetic oscillators plus deterministic seeded noise. No third-party recordings, samples, or proprietary Enshrouded-game assets are included.

| Asset | Purpose | Provenance | Redistribution |
|---|---|---|---|
| `src/main/resources/assets/enshrouded/sounds/ambient/shroud.ogg` | Ordinary Shroud local ambient pulse | Procedurally generated for this repository; synthetic waveform/noise only | CC0-1.0 |
| `src/main/resources/assets/enshrouded/sounds/ambient/deadly_shroud.ogg` | Deadly Shroud local ambient pulse | Procedurally generated for this repository; synthetic waveform/noise only | CC0-1.0 |

The files are short, non-looping Ogg/Vorbis pulses. Runtime spacing is controlled by the client-side bounded emission budget rather than by a persistent looping sound instance, so leaving the Shroud cannot leave an owned ambient loop alive.
