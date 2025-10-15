package top.terry_mc.inGame_screen_editor.logic;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import top.terry_mc.inGame_screen_editor.gui.ScreenSelectionScreen;
import top.terry_mc.inGame_screen_editor.mixin.IScreenAccessor;

public class EventCallbacks {
    public static void onScreenInit(Screen screen) {
        if(screen instanceof TitleScreen) {
            ((IScreenAccessor)screen).invokeAddRenderableWidget(new Button(0, 0, 150, 20, Component.nullToEmpty("IgSE Screen Selection"), button -> {
                Minecraft.getInstance().setScreen(new ScreenSelectionScreen(screen));
            }));
        }
    }
}
