package kmv.softfoliage;

import kmv.softfoliage.config.SoftFoliageConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.LilyPadBlock;
import net.minecraft.world.level.block.state.BlockState;

public final class SoftPlatformPlayerHandler {

    private SoftPlatformPlayerHandler() {
        // Utility class. Do not instantiate.
    }

    public static void tick(ServerPlayer player) {
        String mode = SoftFoliageConfig.INSTANCE.softPlatformBehavior;

        if (mode.equals("DISABLED")) {
            return;
        }

        BlockPos feetPos = player.blockPosition();
        BlockPos belowFeetPos = feetPos.below();

        BlockState feetState = player.level().getBlockState(feetPos);
        BlockState belowFeetState = player.level().getBlockState(belowFeetPos);

        boolean insideSoftBlock = isSoftPlatformBlock(feetState);
        boolean aboveSoftBlock = isSoftPlatformBlock(belowFeetState);

        if (!insideSoftBlock && !aboveSoftBlock) {
            return;
        }

        // Detection checkpoint only.
        // Movement support will be added after this is verified stable.
    }

    private static boolean isSoftPlatformBlock(BlockState state) {
        if (state.getBlock() instanceof LeavesBlock) {
            return true;
        }

        return state.getBlock() instanceof LilyPadBlock
                && SoftFoliageConfig.INSTANCE.lilyPadsAreSoft;
    }
}