package kmv.softfoliage;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

final class SoftPlatformStateTest {

    @Test
    void verifiesCompleteLifecycle() {
        SoftPlatformState state = new SoftPlatformState();
        Object world = new Object();

        state.enterWorld(world);
        assertEquals(SoftPlatformState.Phase.FRESH, state.phase());

        state.activate(100L, 30, 40);
        state.advance(100L);
        assertEquals(SoftPlatformState.Phase.ACTIVE, state.phase());
        state.advance(129L);
        assertEquals(SoftPlatformState.Phase.ACTIVE, state.phase());
        state.advance(130L);
        assertEquals(SoftPlatformState.Phase.RECOVERING, state.phase());
        state.advance(169L);
        assertEquals(SoftPlatformState.Phase.RECOVERING, state.phase());
        state.advance(170L);
        assertEquals(SoftPlatformState.Phase.FRESH, state.phase());
    }

    @Test
    void verifiesEarlyRecovery() {
        SoftPlatformState state = new SoftPlatformState();

        state.enterWorld("overworld");
        state.activate(10L, 70, 40);
        state.beginRecovery(20L, 40);

        state.advance(20L);
        assertEquals(SoftPlatformState.Phase.RECOVERING, state.phase());
        state.advance(59L);
        assertEquals(SoftPlatformState.Phase.RECOVERING, state.phase());
        state.advance(60L);
        assertEquals(SoftPlatformState.Phase.FRESH, state.phase());
    }

    @Test
    void verifiesWorldChangeReset() {
        SoftPlatformState state = new SoftPlatformState();

        state.enterWorld("overworld");
        state.activate(50L, 30, 40);
        state.enterWorld("nether");

        assertEquals(SoftPlatformState.Phase.FRESH, state.phase());
    }

    @Test
    void verifiesCollisionReadsCannotChangePhase() {
        SoftPlatformState state = new SoftPlatformState();

        state.enterWorld("overworld");
        state.activate(1_000L, 30, 40);

        for (int query = 0; query < 10_000; query++) {
            assertEquals(SoftPlatformState.Phase.ACTIVE, state.phase());
        }

        state.advance(1_030L);
        assertEquals(SoftPlatformState.Phase.RECOVERING, state.phase());
    }
}
