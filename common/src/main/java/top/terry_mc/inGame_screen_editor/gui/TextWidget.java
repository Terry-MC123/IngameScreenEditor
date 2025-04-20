package top.terry_mc.inGame_screen_editor.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class TextWidget extends GuiComponent implements GuiEventListener, Widget, NarratableEntry {
    public Component component;
    public int x, y;
    @Override
    public void render(PoseStack poseStack, int i, int j, float f) {
        drawString(poseStack, Minecraft.getInstance().font, component, i, j, 0xffffff);
    }

    @Override
    public @NotNull NarrationPriority narrationPriority() {
        return NarrationPriority.NONE;
    }

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {
        narrationElementOutput.add(NarratedElementType.TITLE, this.component);
    }

    public TextWidget(int x, int y, Component component) {
        this.x = x;
        this.y = y;
        this.component = component;
    }
}
