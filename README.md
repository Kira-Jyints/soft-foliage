# Soft Foliage

Soft Foliage is a Fabric mod by KMV that lets players and configured vehicles pass through leaves. Lily pads can also be treated as soft foliage.

## Current development state

- Version: `1.2.0+26.2`
- Minecraft: `26.2`
- The existing collision behavior is active.
- Soft Platform is implemented as an enabled-by-default mechanic accepted through Kira's Minecraft 26.2 gameplay testing.

## Soft Platform

When enabled, leaves and configured lily pads briefly support a player who reaches them from above while remaining passable from below and from the side.

The generated `config/soft_foliage.json` supports:

- `softPlatformBehavior`: `DISABLED`, `CROUCH_ONLY`, or `ALWAYS`.
- `softPlatformCrouchSupportTicks`: support duration when crouching.
- `softPlatformNormalSupportTicks`: support duration when not crouching.
- `softPlatformResetDelayTicks`: recovery time before support can activate again.
- `softPlatformCushionsFalls`: resets accumulated fall distance when support first catches the player.

The default is `ALWAYS`. On the first 1.2.0 launch, an unmarked legacy `DISABLED` value migrates to `ALWAYS`; a legacy `CROUCH_ONLY` choice is preserved. The migration then records `softPlatformConfigVersion: 1`, after which valid `DISABLED`, `CROUCH_ONLY`, and `ALWAYS` choices are preserved normally.

## License

Licensed under the [Mozilla Public License 2.0](LICENSE). MPL-2.0 keeps changes to covered source files open while allowing the mod to be combined with differently licensed work.
