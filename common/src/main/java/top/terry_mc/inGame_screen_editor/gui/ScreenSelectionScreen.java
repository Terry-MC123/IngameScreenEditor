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
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import org.apache.commons.io.FilenameUtils;
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
    public ScreenSelectionScreen(Screen last) {
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
        this.addRenderableWidget(list);
        this.addRenderableWidget(storagePath);
        this.addRenderableWidget(new Button(this.width/4+60, this.height-60, 50, 20, Component.nullToEmpty("Save"), button -> {
            MutableComponent component = (MutableComponent) Component.nullToEmpty("Failed!");
            component.withStyle(ChatFormatting.RED);
            if(IngameScreenEditor.setStoragePath(storagePath.getValue())) {
                component = (MutableComponent) Component.nullToEmpty("Success!");
                component.withStyle(ChatFormatting.GREEN);
                loadScreenList();
            }
            button.setMessage(component);
            ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
            executor.schedule(()->{
                button.setMessage(Component.nullToEmpty("Save"));
                executor.shutdown();
            },3, TimeUnit.SECONDS);
        }));
        this.addRenderableWidget(new Button(this.width/4+120, this.height-60, 50, 20, Component.nullToEmpty("Refresh"), button -> {
            loadScreenList();
        }));
    }

    @Override
    public void render(PoseStack poseStack, int i, int j, float f) {
        this.renderBackground(poseStack);
        super.render(poseStack, i, j, f);
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
                        if(file1.isFile() && FilenameUtils.getExtension(file1.getName()).equals("json")) {
                            try (FileInputStream stream = new FileInputStream(file1)) {
                                res.add(gson.fromJson(new String(stream.readAllBytes(), StandardCharsets.UTF_8), JsonScreen.class));
                                IngameScreenEditor.LOGGER.info("Loaded Screen \"{}\".", res.get(res.size()-1).name);
                            } catch (IOException e) {
                                IngameScreenEditor.LOGGER.error("Failed to read file: "+file1.getAbsolutePath(), e);
                            }
                        }
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (IOException e) {
                IngameScreenEditor.LOGGER.error("Failed to read files in "+IngameScreenEditor.STORAGE_PATH, e);
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
                if(isMouseOver(d,e)) {
                    if (i == 0) {
                        this.select();
                        return true;
                    }
                }
                return false;
            }

            private void select(){
                ScreenSelectionList.this.setSelected(this);
            }


            @Override
            public void render(PoseStack poseStack, int i, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float f) {
                if (ScreenSelectionList.this.getSelected() != null && ScreenSelectionList.this.getSelected() == this) {
                    fill(poseStack, x-2, y-2, x + entryWidth+2, y + entryHeight+2, 0x8fffffff);
                }
                fill(poseStack, x, y, x + entryWidth, y + entryHeight, 0x8f2b2b2b);
                drawString(poseStack, Minecraft.getInstance().font, jsonScreen.name, x + 4, y + 4, 0xffffffff);
                drawString(poseStack, Minecraft.getInstance().font, "Widget Count: "+jsonScreen.widgets.size(), x + 4, y + 4 + (entryHeight-4) / 2, 0xffffffff);
                drawString(poseStack, Minecraft.getInstance().font, "Screen", x + entryWidth - 4 - Minecraft.getInstance().font.width("Screen"), y + 4, 0xffffffff);
                drawString(poseStack, Minecraft.getInstance().font, "IgSE", x + entryWidth - 4 - Minecraft.getInstance().font.width("IgSE"), y + 4 + (entryHeight-4) / 2, 0xffffffff);
            }
        }
    }
}
