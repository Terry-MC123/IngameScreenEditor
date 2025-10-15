package top.terry_mc.inGame_screen_editor.fabric;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import top.terry_mc.inGame_screen_editor.IngameScreenEditor;
import net.fabricmc.api.ModInitializer;
import top.terry_mc.inGame_screen_editor.logic.EventCallbacks;

public final class IngameScreenEditorFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.
        ScreenEvents.AFTER_INIT.register(((minecraft, screen, i, i1) -> {
            EventCallbacks.onScreenInit(screen);
        }));
        IngameScreenEditor.init();
    }
}
