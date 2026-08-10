package kmv.softfoliage;

import kmv.softfoliage.config.SoftFoliageConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.LilyPadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class SoftPlatformPlayerHandler {

    private static final double SURFACE_EPSILON = 1.0E-7D;

    private SoftPlatformPlayerHandler() {
    }

    public static void tick(Player player) {
        SoftPlatformState state = ((SoftPlatformStateAccess) player)
                .softFoliage$getSoftPlatformState();

        long currentTick = player.level().getGameTime();
        state.enterWorld(player.level().dimension());
        state.advance(currentTick);

        String mode = SoftFoliageConfig.INSTANCE.softPlatformBehavior;
        boolean crouchOnly = "CROUCH_ONLY".equals(mode);

        if ("DISABLED".equals(mode)) {
            state.reset();
            return;
        }

        if (crouchOnly && !player.isShiftKeyDown()) {
            if (state.phase() == SoftPlatformState.Phase.ACTIVE) {
                state.beginRecovery(
                        currentTick,
                        SoftFoliageConfig.INSTANCE.softPlatformResetDelayTicks
                );
            }
            return;
        }

        if (state.phase() != SoftPlatformState.Phase.FRESH) {
            return;
        }

        Vec3 movement = player.getDeltaMovement();
        if (movement.y > 0.0D || !willReachSoftPlatform(player, movement)) {
            return;
        }

        int supportTicks = player.isShiftKeyDown()
                ? SoftFoliageConfig.INSTANCE.softPlatformCrouchSupportTicks
                : SoftFoliageConfig.INSTANCE.softPlatformNormalSupportTicks;

        state.activate(
                currentTick,
                supportTicks,
                SoftFoliageConfig.INSTANCE.softPlatformResetDelayTicks
        );

        if (SoftFoliageConfig.INSTANCE.softPlatformCushionsFalls) {
            player.resetFallDistance();
        }
    }

    private static boolean willReachSoftPlatform(Player player, Vec3 movement) {
        AABB destination = player.getBoundingBox().move(movement.x, 0.0D, movement.z);

        int minimumX = Mth.floor(destination.minX + SURFACE_EPSILON);
        int maximumX = Mth.floor(destination.maxX - SURFACE_EPSILON);
        int minimumZ = Mth.floor(destination.minZ + SURFACE_EPSILON);
        int maximumZ = Mth.floor(destination.maxZ - SURFACE_EPSILON);

        int highestY = Mth.floor(destination.minY - SURFACE_EPSILON);
        int lowestY = Mth.floor(destination.minY + movement.y - SURFACE_EPSILON);

        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

        for (int y = highestY; y >= lowestY; y--) {
            for (int x = minimumX; x <= maximumX; x++) {
                for (int z = minimumZ; z <= maximumZ; z++) {
                    cursor.set(x, y, z);
                    if (isSoftPlatformBlock(player.level().getBlockState(cursor))) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private static boolean isSoftPlatformBlock(BlockState state) {
        if (state.getBlock() instanceof LeavesBlock) {
            return true;
        }

        return state.getBlock() instanceof LilyPadBlock
                && SoftFoliageConfig.INSTANCE.lilyPadsAreSoft;
    }
}
