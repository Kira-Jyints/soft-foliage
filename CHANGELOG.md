# Soft Foliage Changelog

## 1.2.0+26.2 — Soft Platforms

- Added enabled-by-default Soft Platform behavior for leaves and optional lily pads.
- Added `ALWAYS`, `CROUCH_ONLY`, and `DISABLED` platform modes.
- Added configurable support durations, recovery timing, and optional fall cushioning.
- Preserved one-way movement: players can still rise through foliage and enter it from the side.
- Added per-player lifecycle state so collision queries only read support state and cannot control its timing.
- Added a one-time migration that changes an unmarked legacy `DISABLED` default to `ALWAYS`, then preserves later player choices.
- Retained the subtle give-way motion as part of the platform feel.
- Restricted this release to Minecraft 26.2, Fabric Loader 0.19.3 or newer, Fabric API, and Java 25 or newer.

Thorn-bush behavior is unchanged and remains separate future work.
