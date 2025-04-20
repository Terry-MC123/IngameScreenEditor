package top.terry_mc.inGame_screen_editor.gui;

import com.google.gson.Gson;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import top.terry_mc.inGame_screen_editor.IngameScreenEditor;
import top.terry_mc.inGame_screen_editor.json.JsonScreen;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScreenSelectionScreen extends Screen {
    protected ScreenSelectionScreen(Screen last) {
        super(Component.nullToEmpty("Select a Screen to edit or create a new one"));
        this.last=last;
    }

    Screen last;
    EditBox storagePath = null;
    List<JsonScreen> screens;
    ScreenSelectionList list = new ScreenSelectionList(minecraft);
    boolean first = true;

    @Override
    public void init() {
        if(first) {
            loadScreenList();
            first = false;
        }
        storagePath = new EditBox(this.font, this.width/4-50,  this.height-60, 100, 20, storagePath, Component.nullToEmpty("Storage Path"));
        list.changePos(this.width, this.height, 32, this.height-65+4);
        this.addRenderableWidget(storagePath);
        this.addRenderableWidget(new Button(this.width/4+60, this.height-60, 50, 20, Component.nullToEmpty("Save"), button -> {
            Component component = Component.nullToEmpty("Failed!");
            component.getStyle().applyFormat(ChatFormatting.RED);
            if(IngameScreenEditor.setStoragePath(storagePath.getValue())) {
                component = Component.nullToEmpty("Success!");
                component.getStyle().applyFormat(ChatFormatting.GREEN);
                loadScreenList();
            }
            button.setMessage(component);
            ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
            executor.schedule(()->{
                button.setMessage(Component.nullToEmpty("Save"));
                executor.shutdown();
            },3, TimeUnit.SECONDS);
        }));
    }

    public void loadScreenList() {
        new Thread(()->{
            List<JsonScreen> res = new ArrayList<>();
            Gson gson = new Gson();
            try {
                Files.walkFileTree(IngameScreenEditor.STORAGE_PATH, new SimpleFileVisitor<>(){
                    @Override
                    public @NotNull FileVisitResult visitFile(Path file, @NotNull BasicFileAttributes attrs) {
                        File file1 = file.toFile();
                        if(file1.isFile()) {
                            try (FileInputStream stream = new FileInputStream(file1)) {
                                res.add(gson.fromJson(new String(stream.readAllBytes(), StandardCharsets.UTF_8), JsonScreen.class));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.screens=res;
            list.refreshEntries();
        },"Json reader").start();
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(last);
    }

    class ScreenSelectionList extends ObjectSelectionList<ScreenSelectionList.Entry>{
        public ScreenSelectionList(Minecraft minecraft) {
            super(minecraft, ScreenSelectionScreen.this.width, ScreenSelectionScreen.this.height, 32, ScreenSelectionScreen.this.height-65+4, 40);
            ScreenSelectionScreen.this.screens.forEach((jsonScreen -> this.addEntry(new Entry(jsonScreen))));
        }

        public void refreshEntries() {
            this.clearEntries();
            ScreenSelectionScreen.this.screens.forEach((jsonScreen -> this.addEntry(new Entry(jsonScreen))));
        }

        public void changePos(int width, int height, int y0, int y1){
            this.width=width;
            this.height=height;
            this.y0=y0;
            this.y1=y1;
        }

        @Override
        public void render(PoseStack poseStack, int i, int j, float f) {
            super.render(poseStack, i, j, f);
        }

        @Override
        protected boolean isFocused(){
            return ScreenSelectionScreen.this.getFocused() == this;
        }

        @Override
        protected int getScrollbarPosition() {
            return super.getScrollbarPosition() + 20;
        }

        @Override
        public int getRowWidth() {
            return super.getRowWidth() + 50;
        }

        public class Entry extends ObjectSelectionList.Entry<Entry>{
            public final JsonScreen jsonScreen;
            @Override
            public @NotNull Component getNarration() {
                return Component.nullToEmpty(null);
            }

            public Entry(JsonScreen jsonScreen){
                this.jsonScreen = jsonScreen;
            }

            @Override
            public boolean mouseClicked(double d, double e, int i) {
                if (i == 0) {
                    this.select();
                    return true;
                } else {
                    return false;
                }
            }

            private void select(){
                ScreenSelectionList.this.setSelected(this);
            }


            @Override
            public void render(PoseStack poseStack, int i, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float f) {
                fill(poseStack,x,y,x+entryWidth,y+entryHeight,0x8f2b2b2b);
                drawString(poseStack, ScreenSelectionList.this.minecraft.font, jsonScreen.name, x + 4, y + 4, 0xffffffff);
                drawString(poseStack, ScreenSelectionList.this.minecraft.font, "Widget Count: "+jsonScreen.widgets.size(), x + 4, y + 4 + (entryHeight-4) / 2, 0xffffffff);
                drawString(poseStack, ScreenSelectionList.this.minecraft.font, "Screen", x + entryWidth - 4 - ScreenSelectionList.this.minecraft.font.width("Screen"), y + 4, 0xffffffff);
                drawString(poseStack, ScreenSelectionList.this.minecraft.font, "IgSE", x + entryWidth - 4 - ScreenSelectionList.this.minecraft.font.width("IgSE"), y + 4 + (entryHeight-4) / 2, 0xffffffff);
            }
        }
    }
}
