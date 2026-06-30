package kmv.softfoliage.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.LilyPadBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import kmv.softfoliage.config.SoftFoliageConfig;
import org.spongepowered.asm.mixin.Unique;

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

		if (context instanceof EntityCollisionContext entityCollisionContext) {
			Entity entity = entityCollisionContext.getEntity();

			if (shouldIgnoreLeafCollision(entity)) {
				cir.setReturnValue(Shapes.empty());
			}
		}
	}
	@Unique
	private static boolean shouldIgnoreLeafCollision(Entity entity) {
		if (entity == null) {
			return false;
		}

		if (entity instanceof Player player && !player.isSpectator()) {
			return SoftFoliageConfig.INSTANCE.playersPassThroughLeaves;
		}

		Entity controllingPassenger = entity.getControllingPassenger();

		if (controllingPassenger instanceof Player player && !player.isSpectator()) {
			return SoftFoliageConfig.INSTANCE.vehiclesPassThroughLeaves;
		}

		return false;
	}
}