package top.terry_mc.inGame_screen_editor.forge;

import top.terry_mc.inGame_screen_editor.IngameScreenEditor;
import net.minecraftforge.fml.common.Mod;

@Mod(IngameScreenEditor.MOD_ID)
public final class IngameScreenEditorForge {
    public IngameScreenEditorForge() {
        // Run our common setup.
        IngameScreenEditor.init();
    }
}
