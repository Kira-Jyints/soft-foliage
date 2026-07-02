package kmv.softfoliage;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class SoftPlatformManager {

    private static final Map<SoftPlatformKey, SupportState> SUPPORT_STATES = new HashMap<>();

    private SoftPlatformManager() {
        // Utility class. Do not instantiate.
    }

    public static boolean canSupport(Level level, BlockPos pos, int supportTicks, int resetDelayTicks) {
        SoftPlatformKey key = new SoftPlatformKey(level.dimension(), pos.immutable());

        int safeSupportTicks = Math.max(20, supportTicks);
        int safeResetDelayTicks = Math.max(20, resetDelayTicks);

        SupportState state = SUPPORT_STATES.get(key);

        if (state == null) {
            state = new SupportState(safeSupportTicks, safeResetDelayTicks);
            SUPPORT_STATES.put(key, state);
        }

        state.resetTicksRemaining = safeResetDelayTicks;

        return state.supportTicksRemaining > 0;
    }

    public static void tick() {
        Iterator<Map.Entry<SoftPlatformKey, SupportState>> iterator =
                SUPPORT_STATES.entrySet().iterator();

        while (iterator.hasNext()) {
            SupportState state = iterator.next().getValue();

            if (state.supportTicksRemaining > 0) {
                state.supportTicksRemaining--;
            }

            if (state.resetTicksRemaining > 0) {
                state.resetTicksRemaining--;
            }

            if (state.resetTicksRemaining <= 0) {
                iterator.remove();
            }
        }
    }

    public static void clear() {
        SUPPORT_STATES.clear();
    }

    private record SoftPlatformKey(ResourceKey<Level> dimension, BlockPos pos) {
    }

    private static class SupportState {

        private int supportTicksRemaining;
        private int resetTicksRemaining;

        private SupportState(int supportTicksRemaining, int resetTicksRemaining) {
            this.supportTicksRemaining = supportTicksRemaining;
            this.resetTicksRemaining = resetTicksRemaining;
        }
    }
}