package kmv.softfoliage;

import java.util.Objects;

public final class SoftPlatformState {

    public enum Phase {
        FRESH,
        ACTIVE,
        RECOVERING
    }

    private Phase phase = Phase.FRESH;
    private Object worldKey;
    private long supportEndsAt;
    private long freshAt;

    public void enterWorld(Object newWorldKey) {
        if (!Objects.equals(worldKey, newWorldKey)) {
            reset();
            worldKey = newWorldKey;
        }
    }

    public void advance(long currentTick) {
        if (phase == Phase.ACTIVE && currentTick >= supportEndsAt) {
            phase = Phase.RECOVERING;
        }

        if (phase == Phase.RECOVERING && currentTick >= freshAt) {
            resetTiming();
        }

    }

    public Phase phase() {
        return phase;
    }

    public void activate(long currentTick, int supportTicks, int recoveryTicks) {
        int safeSupportTicks = Math.max(1, supportTicks);
        int safeRecoveryTicks = Math.max(1, recoveryTicks);

        phase = Phase.ACTIVE;
        supportEndsAt = currentTick + safeSupportTicks;
        freshAt = supportEndsAt + safeRecoveryTicks;
    }

    public void beginRecovery(long currentTick, int recoveryTicks) {
        int safeRecoveryTicks = Math.max(1, recoveryTicks);

        phase = Phase.RECOVERING;
        supportEndsAt = currentTick;
        freshAt = currentTick + safeRecoveryTicks;
    }

    public void reset() {
        resetTiming();
        worldKey = null;
    }

    private void resetTiming() {
        phase = Phase.FRESH;
        supportEndsAt = 0L;
        freshAt = 0L;
    }
}
