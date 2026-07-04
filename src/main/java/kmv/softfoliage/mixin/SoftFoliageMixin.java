package kmv.softfoliage.mixin;

import kmv.softfoliage.SoftPlatformManager;
import kmv.softfoliage.config.SoftFoliageConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.LilyPadBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.class)
public class SoftFoliageMixin {

	@Unique
	private static final VoxelShape LEAF_SUPPORT_SHAPE = Shapes.block();

	@Unique
	private static final VoxelShape LILY_PAD_SUPPORT_SHAPE = Shapes.box(
			0.0625,
			0.0,
			0.0625,
			0.9375,
			0.09375,
			0.9375
	);

	@Inject(method = "getCollisionShape", at = @At("HEAD"), cancellable = true)
	private void softFoliage$getCollisionShape(
			BlockState state,
			BlockGetter level,
			BlockPos pos,
			CollisionContext context,
			CallbackInfoReturnable<VoxelShape> cir
	) {
		boolean isSoftLeaves = state.getBlock() instanceof LeavesBlock;

		boolean isSoftLilyPad =
				state.getBlock() instanceof LilyPadBlock
						&& SoftFoliageConfig.INSTANCE.lilyPadsAreSoft;

		if (!isSoftLeaves && !isSoftLilyPad) {
			return;
		}

		if (!(context instanceof EntityCollisionContext entityCollisionContext)) {
			return;
		}

		Entity entity = entityCollisionContext.getEntity();

		if (entity == null) {
			return;
		}

		if (entity instanceof Player player && !player.isSpectator()) {
			handlePlayerCollision(player, level, pos, isSoftLilyPad, context, cir);
			return;
		}

		if (shouldIgnoreVehicleCollision(entity)) {
			cir.setReturnValue(Shapes.empty());
		}
	}

	@Unique
	private static void handlePlayerCollision(
			Player player,
			BlockGetter level,
			BlockPos pos,
			boolean isSoftLilyPad,
			CollisionContext context,
			CallbackInfoReturnable<VoxelShape> cir
	) {
		if (!SoftFoliageConfig.INSTANCE.playersPassThroughLeaves) {
			return;
		}

		if (shouldUseSoftPlatform(player, level, pos, isSoftLilyPad, context)) {
			return;
		}

		cir.setReturnValue(Shapes.empty());
	}

	@Unique
	private static boolean shouldUseSoftPlatform(
			Player player,
			BlockGetter level,
			BlockPos pos,
			boolean isSoftLilyPad,
			CollisionContext context
	) {
		String mode = SoftFoliageConfig.INSTANCE.softPlatformBehavior;

		if (mode.equals("DISABLED")) {
			return false;
		}

		if (!(level instanceof Level actualLevel)) {
			return false;
		}

		VoxelShape supportShape = isSoftLilyPad ? LILY_PAD_SUPPORT_SHAPE : LEAF_SUPPORT_SHAPE;

		if (!context.isAbove(supportShape, pos, true)) {
			return false;
		}

		int supportTicks;

		if (player.isShiftKeyDown()) {
			supportTicks = SoftFoliageConfig.INSTANCE.softPlatformCrouchSupportTicks;
		} else if (mode.equals("ALWAYS")) {
			supportTicks = SoftFoliageConfig.INSTANCE.softPlatformNormalSupportTicks;
		} else {
			return false;
		}

		return SoftPlatformManager.canSupport(
				actualLevel,
				pos,
				supportTicks,
				SoftFoliageConfig.INSTANCE.softPlatformResetDelayTicks
		);
	}

	@Unique
	private static boolean shouldIgnoreVehicleCollision(Entity entity) {
		Entity controllingPassenger = entity.getControllingPassenger();

		return controllingPassenger instanceof Player player
				&& !player.isSpectator()
				&& SoftFoliageConfig.INSTANCE.vehiclesPassThroughLeaves;
	}
}