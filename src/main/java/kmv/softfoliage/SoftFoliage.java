package kmv.softfoliage;

import kmv.softfoliage.config.SoftFoliageConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SoftFoliage implements ModInitializer {
	public static final String MOD_ID = "soft_foliage";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		SoftFoliageConfig.load();

		ServerTickEvents.END_SERVER_TICK.register(server -> SoftPlatformManager.tick());

		LOGGER.info("Foliage has been softened...");
	}
}