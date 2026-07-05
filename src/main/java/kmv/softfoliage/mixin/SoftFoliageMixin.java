package kmv.softfoliage.mixin;

import kmv.softfoliage.config.SoftFoliageConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.LilyPadBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
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
			handlePlayerCollision(cir);
			return;
		}

		if (shouldIgnoreVehicleCollision(entity)) {
			cir.setReturnValue(Shapes.empty());
		}
	}

	@Unique
	private static void handlePlayerCollision(CallbackInfoReturnable<VoxelShape> cir) {
		if (!SoftFoliageConfig.INSTANCE.playersPassThroughLeaves) {
			return;
		}

		cir.setReturnValue(Shapes.empty());
	}

	@Unique
	private static boolean shouldIgnoreVehicleCollision(Entity entity) {
		Entity controllingPassenger = entity.getControllingPassenger();

		return controllingPassenger instanceof Player player
				&& !player.isSpectator()
				&& SoftFoliageConfig.INSTANCE.vehiclesPassThroughLeaves;
	}
}