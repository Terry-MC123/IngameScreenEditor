package top.terry_mc.inGame_screen_editor.forge;

import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.terry_mc.inGame_screen_editor.IngameScreenEditor;
import net.minecraftforge.fml.common.Mod;
import top.terry_mc.inGame_screen_editor.logic.EventCallbacks;

@Mod(IngameScreenEditor.MOD_ID)
@Mod.EventBusSubscriber
public final class IngameScreenEditorForge {
    public IngameScreenEditorForge() {
        // Run our common setup.
        IngameScreenEditor.init();
    }

    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.InitScreenEvent event) {
        EventCallbacks.onScreenInit(event.getScreen());
    }
}
