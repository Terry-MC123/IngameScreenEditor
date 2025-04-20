package top.terry_mc.inGame_screen_editor.mixin;

import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Screen.class)
public interface IScreenAccessor {
    @Invoker("addRenderableWidget")
    <T extends GuiEventListener & Widget & NarratableEntry> T invokeAddRenderableWidget(T guiEventListener);
}
