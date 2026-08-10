package kmv.softfoliage.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

final class SoftPlatformConfigMigration {

    static final int CURRENT_VERSION = 1;

    private static final String VERSION_KEY = "softPlatformConfigVersion";
    private static final String BEHAVIOR_KEY = "softPlatformBehavior";

    private SoftPlatformConfigMigration() {
    }

    static boolean migrate(JsonObject jsonObject) {
        if (readVersion(jsonObject) >= CURRENT_VERSION) {
            return false;
        }

        JsonElement behavior = jsonObject.get(BEHAVIOR_KEY);
        if (behavior != null
                && behavior.isJsonPrimitive()
                && behavior.getAsJsonPrimitive().isString()
                && "DISABLED".equals(behavior.getAsString())) {
            jsonObject.addProperty(BEHAVIOR_KEY, "ALWAYS");
        }

        jsonObject.addProperty(VERSION_KEY, CURRENT_VERSION);
        return true;
    }

    private static int readVersion(JsonObject jsonObject) {
        JsonElement version = jsonObject.get(VERSION_KEY);
        if (version == null
                || !version.isJsonPrimitive()
                || !version.getAsJsonPrimitive().isNumber()) {
            return 0;
        }

        try {
            return version.getAsInt();
        } catch (NumberFormatException exception) {
            return 0;
        }
    }
}
