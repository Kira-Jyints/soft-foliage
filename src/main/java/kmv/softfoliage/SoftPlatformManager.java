package kmv.softfoliage;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class SoftPlatformManager {

    private static final Map<SoftPlatformKey, SupportState> SUPPORT_STATES = new ConcurrentHashMap<>();

    private SoftPlatformManager() {
    }

    public static boolean canSupport(Level level, BlockPos pos, int supportTicks, int resetDelayTicks) {
        SoftPlatformKey key = new SoftPlatformKey(
                level.dimension(),
                pos.immutable(),
                level.isClientSide()
        );

        long currentTick = level.getGameTime();

        int safeSupportTicks = Math.max(20, supportTicks);
        int safeResetDelayTicks = Math.max(20, resetDelayTicks);

        SupportState state = SUPPORT_STATES.get(key);

        if (state != null && currentTick > state.resetEndsAt) {
            SUPPORT_STATES.remove(key);
            state = null;
        }

        if (state == null) {
            state = new SupportState(
                    currentTick + safeSupportTicks,
                    currentTick + safeResetDelayTicks
            );

            SUPPORT_STATES.put(key, state);
        }

        if (currentTick <= state.supportEndsAt) {
            state.resetEndsAt = currentTick + safeResetDelayTicks;
            return true;
        }

        return false;
    }

    public static void tick() {
        // Support timing is based on world game time now.
        // Cleanup happens opportunistically in canSupport().
    }

    public static void clear() {
        SUPPORT_STATES.clear();
    }

    private record SoftPlatformKey(ResourceKey<Level> dimension, BlockPos pos, boolean clientSide) {
    }

    private static class SupportState {

        private final long supportEndsAt;
        private long resetEndsAt;

        private SupportState(long supportEndsAt, long resetEndsAt) {
            this.supportEndsAt = supportEndsAt;
            this.resetEndsAt = resetEndsAt;
        }
    }
}