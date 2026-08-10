package kmv.softfoliage.mixin;

import kmv.softfoliage.SoftPlatformState;
import kmv.softfoliage.SoftPlatformStateAccess;
import kmv.softfoliage.SoftPlatformPlayerHandler;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerSoftPlatformStateMixin implements SoftPlatformStateAccess {

	@Inject(method = "tick", at = @At("HEAD"))
	private void softFoliage$prepareSoftPlatformState(CallbackInfo ci) {
		SoftPlatformPlayerHandler.tick((Player) (Object) this);
	}

    @Unique
    private SoftPlatformState softFoliage$softPlatformState;

    @Override
    public SoftPlatformState softFoliage$getSoftPlatformState() {
        if (softFoliage$softPlatformState == null) {
            softFoliage$softPlatformState = new SoftPlatformState();
        }

        return softFoliage$softPlatformState;
    }
}
