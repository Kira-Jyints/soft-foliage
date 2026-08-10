package kmv.softfoliage.config;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class SoftPlatformConfigMigrationTest {

    @Test
    void migratesLegacyDisabledDefaultToAlwaysOnce() {
        JsonObject config = configWithBehavior("DISABLED");

        assertTrue(SoftPlatformConfigMigration.migrate(config));
        assertEquals("ALWAYS", config.get("softPlatformBehavior").getAsString());
        assertEquals(1, config.get("softPlatformConfigVersion").getAsInt());
    }

    @Test
    void preservesDisabledAfterMigrationHasRun() {
        JsonObject config = configWithBehavior("DISABLED");
        config.addProperty("softPlatformConfigVersion", 1);

        assertFalse(SoftPlatformConfigMigration.migrate(config));
        assertEquals("DISABLED", config.get("softPlatformBehavior").getAsString());
    }

    @Test
    void preservesLegacyCrouchOnlyChoice() {
        JsonObject config = configWithBehavior("CROUCH_ONLY");

        assertTrue(SoftPlatformConfigMigration.migrate(config));
        assertEquals("CROUCH_ONLY", config.get("softPlatformBehavior").getAsString());
        assertEquals(1, config.get("softPlatformConfigVersion").getAsInt());
    }

    @Test
    void migrationIsIdempotent() {
        JsonObject config = configWithBehavior("DISABLED");

        assertTrue(SoftPlatformConfigMigration.migrate(config));
        config.addProperty("softPlatformBehavior", "DISABLED");

        assertFalse(SoftPlatformConfigMigration.migrate(config));
        assertEquals("DISABLED", config.get("softPlatformBehavior").getAsString());
    }

    private static JsonObject configWithBehavior(String behavior) {
        JsonObject config = new JsonObject();
        config.addProperty("softPlatformBehavior", behavior);
        return config;
    }
}
