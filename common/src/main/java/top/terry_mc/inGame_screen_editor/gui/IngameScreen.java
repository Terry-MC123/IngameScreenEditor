package top.terry_mc.inGame_screen_editor.gui;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import top.terry_mc.inGame_screen_editor.json.JsonScreen;

import java.util.ArrayList;
import java.util.List;

public class IngameScreen extends Screen {
    List<JsonScreen.Widget> widgets;
    Screen last;

    protected IngameScreen(JsonScreen screen, Screen last) {
        super(Component.nullToEmpty(screen.name));
        this.widgets = new ArrayList<>(screen.widgets.values());
        this.last = last;
    }

    @Override
    public void init() {
        widgets.forEach(widget -> {
            if(getRenderableWidget(widget) != null){
                addRenderableWidget(getRenderableWidget(widget));
            }
        });
    }

    public <T extends GuiEventListener & Widget & NarratableEntry> T getRenderableWidget(JsonScreen.Widget widget) {
        switch (widget.type) {
            case "button" -> {
                return (T) new Button(widget.x,widget.y,widget.width,widget.height,Component.nullToEmpty(widget.message),(button)->{});
            }
            case "text" -> {
                return (T) new TextWidget(widget.x, widget.y, Component.nullToEmpty(widget.message));
            }
        }
        return null;
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(last);
    }
}
