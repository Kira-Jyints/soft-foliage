package kmv.softfoliage.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SoftFoliageConfig {

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir()
            .resolve("soft_foliage.json");

    public Boolean playersPassThroughLeaves = true;
    public Boolean vehiclesPassThroughLeaves = true;
    public Boolean lilyPadsAreSoft = true;

    public static SoftFoliageConfig INSTANCE = new SoftFoliageConfig();

    public static void load() {
        try {
            if (!Files.exists(CONFIG_PATH)) {
                saveDefaultConfig();
            }

            String json = Files.readString(CONFIG_PATH);

            var jsonObject = GSON.fromJson(json, com.google.gson.JsonObject.class);

            if (jsonObject == null
                    || !jsonObject.has("playersPassThroughLeaves")
                    || !jsonObject.has("vehiclesPassThroughLeaves")
                    || !jsonObject.has("lilyPadsAreSoft")
                    || !jsonObject.get("playersPassThroughLeaves").isJsonPrimitive()
                    || !jsonObject.get("vehiclesPassThroughLeaves").isJsonPrimitive()
                    || !jsonObject.get("lilyPadsAreSoft").isJsonPrimitive()
                    || !jsonObject.get("playersPassThroughLeaves").getAsJsonPrimitive().isBoolean()
                    || !jsonObject.get("vehiclesPassThroughLeaves").getAsJsonPrimitive().isBoolean()
                    || !jsonObject.get("lilyPadsAreSoft").getAsJsonPrimitive().isBoolean()) {

                System.err.println("[Soft Foliage] Config was invalid. Recreating default config.");
                INSTANCE = new SoftFoliageConfig();
                saveDefaultConfig();
                return;
            }

            INSTANCE = GSON.fromJson(jsonObject, SoftFoliageConfig.class);

        } catch (Exception exception) {
            System.err.println("[Soft Foliage] Failed to load config. Recreating default config.");
            INSTANCE = new SoftFoliageConfig();
            saveDefaultConfig();
            exception.printStackTrace();
        }
    }

    private boolean isValid() {
        return playersPassThroughLeaves != null
                && vehiclesPassThroughLeaves != null
                && lilyPadsAreSoft != null;
    }

    private static void saveDefaultConfig() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            String json = GSON.toJson(new SoftFoliageConfig());
            Files.writeString(CONFIG_PATH, json);
        } catch (IOException exception) {
            System.err.println("[Soft Foliage] Failed to save default config!");
            exception.printStackTrace();
        }
    }
}