package kmv.softfoliage.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

public class SoftFoliageConfig {

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir()
            .resolve("soft_foliage.json");

    private static final Set<String> SOFT_PLATFORM_MODES = Set.of(
            "DISABLED",
            "CROUCH_ONLY",
            "ALWAYS"
    );

    public Boolean playersPassThroughLeaves = true;
    public Boolean vehiclesPassThroughLeaves = true;
    public Boolean lilyPadsAreSoft = true;

    public String softPlatformBehavior = "DISABLED";
    public Integer softPlatformCrouchSupportTicks = 70;
    public Integer softPlatformNormalSupportTicks = 30;
    public Integer softPlatformResetDelayTicks = 40;

    public static SoftFoliageConfig INSTANCE = new SoftFoliageConfig();

    public static void load() {
        try {
            if (!Files.exists(CONFIG_PATH)) {
                INSTANCE = new SoftFoliageConfig();
                saveConfig();
                return;
            }

            String json = Files.readString(CONFIG_PATH);
            JsonObject jsonObject = GSON.fromJson(json, JsonObject.class);

            if (jsonObject == null) {
                System.err.println("[Soft Foliage] Config was empty or invalid. Recreating default config.");
                INSTANCE = new SoftFoliageConfig();
                saveConfig();
                return;
            }

            boolean changed = false;

            changed |= ensureBoolean(jsonObject, "playersPassThroughLeaves", true);
            changed |= ensureBoolean(jsonObject, "vehiclesPassThroughLeaves", true);
            changed |= ensureBoolean(jsonObject, "lilyPadsAreSoft", true);

            changed |= ensureStringOption(jsonObject, "softPlatformBehavior", "ALWAYS", SOFT_PLATFORM_MODES);
            changed |= ensureInteger(jsonObject, "softPlatformCrouchSupportTicks", 70, 20);
            changed |= ensureInteger(jsonObject, "softPlatformNormalSupportTicks", 30, 20);
            changed |= ensureInteger(jsonObject, "softPlatformResetDelayTicks", 40, 20);

            INSTANCE = GSON.fromJson(jsonObject, SoftFoliageConfig.class);

            if (changed) {
                System.err.println("[Soft Foliage] Config was missing or had invalid values. Updating while preserving valid settings.");
                saveConfig();
            }

        } catch (Exception exception) {
            System.err.println("[Soft Foliage] Failed to load config. Recreating default config.");
            INSTANCE = new SoftFoliageConfig();
            saveConfig();
            exception.printStackTrace();
        }
    }

    private static boolean ensureBoolean(JsonObject jsonObject, String key, boolean defaultValue) {
        if (!jsonObject.has(key)
                || !jsonObject.get(key).isJsonPrimitive()
                || !jsonObject.get(key).getAsJsonPrimitive().isBoolean()) {

            jsonObject.addProperty(key, defaultValue);
            return true;
        }

        return false;
    }

    private static boolean ensureInteger(JsonObject jsonObject, String key, int defaultValue, int minimumValue) {
        if (!jsonObject.has(key)
                || !jsonObject.get(key).isJsonPrimitive()
                || !jsonObject.get(key).getAsJsonPrimitive().isNumber()
                || jsonObject.get(key).getAsInt() < minimumValue) {

            jsonObject.addProperty(key, defaultValue);
            return true;
        }

        return false;
    }

    private static boolean ensureStringOption(JsonObject jsonObject, String key, String defaultValue, Set<String> allowedValues) {
        if (!jsonObject.has(key)
                || !jsonObject.get(key).isJsonPrimitive()
                || !jsonObject.get(key).getAsJsonPrimitive().isString()
                || !allowedValues.contains(jsonObject.get(key).getAsString())) {

            jsonObject.addProperty(key, defaultValue);
            return true;
        }

        return false;
    }

    private static void saveConfig() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            String json = GSON.toJson(INSTANCE);
            Files.writeString(CONFIG_PATH, json);
        } catch (IOException exception) {
            System.err.println("[Soft Foliage] Failed to save config!");
            exception.printStackTrace();
        }
    }
}