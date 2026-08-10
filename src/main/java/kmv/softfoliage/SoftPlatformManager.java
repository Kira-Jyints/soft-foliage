package kmv.softfoliage;

import net.minecraft.world.entity.player.Player;

public final class SoftPlatformManager {

    private SoftPlatformManager() {
    }

    public static boolean isSupporting(Player player) {
        SoftPlatformState state = ((SoftPlatformStateAccess) player)
                .softFoliage$getSoftPlatformState();

        return state.phase() == SoftPlatformState.Phase.ACTIVE;
    }
}
