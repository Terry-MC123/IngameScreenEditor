package top.terry_mc.inGame_screen_editor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.prefs.Preferences;

public final class IngameScreenEditor {
    public static final String MOD_ID = "in-game_screen_editor";
    public static final Logger LOGGER = LogManager.getLogger("In-game Screen Editor");
    public static final Preferences PREFERENCES = Preferences.userNodeForPackage(IngameScreenEditor.class);
    public static Path STORAGE_PATH = Path.of(PREFERENCES.get("storage_path", "/gui")).toAbsolutePath();;

    public static void init() {
        LOGGER.info("In-game Screen Editor loaded.");
    }

    public static boolean setStoragePath(String path) {
        try {
            STORAGE_PATH = Path.of(path).toAbsolutePath();
            PREFERENCES.put("storage_path", path);
        } catch (InvalidPathException e) {
            return false;
        }
        return true;
    }
}
