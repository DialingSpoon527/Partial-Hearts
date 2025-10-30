package net.dialingspoon.partialhearts.gui;

import com.google.common.collect.ImmutableList;
import net.dialingspoon.partialhearts.PatternManager;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.*;

public class PatternListScreen extends Screen {

    private String selectedPatternName = PatternManager.getSelectedPatternName();
    private final Map<String, int[]> patterns;
    private PatternList patternList;

    public PatternListScreen(Screen parent) {
        super(Component.translatable("patternlist.title"));
        patterns = PatternManager.loadPatterns();
    }

    @Override
    protected void init() {
        this.patternList = new PatternList(this.minecraft, this.width, 175, 30, 22);
        this.addRenderableWidget(this.patternList);

        int buttonY = this.height - 26;
        Button newButton = Button.builder(Component.translatable("patternlist.new"), b -> this.minecraft.setScreen(new PatternEditScreen(this)))
                .pos(this.width / 2 - 100, buttonY)
                .size(60, 20)
                .build();

        Button doneButton = Button.builder(Component.translatable("gui.done"), b -> onClose())
                .pos(this.width / 2 + 40, buttonY)
                .size(60, 20)
                .build();

        this.addRenderableWidget(newButton);
        this.addRenderableWidget(doneButton);
    }

    @Override
    public void render(GuiGraphics gg, int mouseX, int mouseY, float partialTicks) {
        super.render(gg, mouseX, mouseY, partialTicks);
        gg.drawCenteredString(this.font, this.title, this.width / 2, 10, 0xFFFFFFFF);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent mouseButtonEvent, boolean doubled) {
        boolean result = super.mouseClicked(mouseButtonEvent, doubled);

        PatternEntry selected = this.patternList.getSelected();
        if (selected != null) {
            this.selectedPatternName = selected.getName();
        }

        return result;
    }

    @Override
    public void onClose() {
        PatternManager.savePatterns(patterns, selectedPatternName, patterns.get(selectedPatternName));
        this.minecraft.setScreen(null);
    }

    public void onPatternSaved(String oldName, String newName, int[] newData) {
        if (oldName != null) {
            patterns.remove(oldName);
        }
        while (patterns.containsKey(newName)) {
            newName = incrementName(newName);
        }
        patterns.put(newName, newData);
        selectedPatternName = newName;

        this.rebuildWidgets();
    }

    public void onPatternEditCanceled() {
        this.rebuildWidgets();
    }

    private void deletePattern(String name) {
        patterns.remove(name);
        if (selectedPatternName.equals(name)) {
            selectedPatternName = PatternManager.ORIGINAL_PATTERN;
        }
        this.rebuildWidgets();
    }

    private void duplicatePattern(String name) {
        int[] data = patterns.get(name);

        String newName = incrementName(name);
        while (patterns.containsKey(newName)) {
            newName = incrementName(newName);
        }

        int[] newData = Arrays.copyOf(data, data.length);
        patterns.put(newName, newData);
        this.rebuildWidgets();
    }

    private String incrementName(String name) {
        int len = name.length();
        int lastDigitStart = len;
        for (int i = len - 1; i >= 0; i--) {
            if (!Character.isDigit(name.charAt(i))) {
                lastDigitStart = i + 1;
                break;
            }
        }

        if (lastDigitStart == len) {
            return name + "2";
        }

        String prefix = name.substring(0, lastDigitStart);
        String numberStr = name.substring(lastDigitStart);
        int numberVal;
        try {
            numberVal = Integer.parseInt(numberStr);
        } catch (NumberFormatException e) {
            numberVal = 1;
        }

        numberVal += 1;
        return prefix + numberVal;
    }

    class PatternList extends ContainerObjectSelectionList<PatternEntry> {
        public PatternList(Minecraft minecraft, int width, int height, int y, int itemHeight) {
            super(minecraft, width, height, y, itemHeight);

            addEntry(new PatternEntry(PatternManager.ORIGINAL_PATTERN, patterns.get(PatternManager.ORIGINAL_PATTERN), true));
            addEntry(new PatternEntry(PatternManager.RANDOM_PATTERN, patterns.get(PatternManager.RANDOM_PATTERN), true));

            List<Map.Entry<String, int[]>> sortedEntries = patterns.entrySet().stream()
                    .filter(e -> !e.getKey().equals(PatternManager.ORIGINAL_PATTERN) && !e.getKey().equals(PatternManager.RANDOM_PATTERN))
                    .sorted(Map.Entry.comparingByKey())
                    .toList();

            for (Map.Entry<String, int[]> entry : sortedEntries) {
                addEntry(new PatternEntry(entry.getKey(), entry.getValue(), false));
            }
        }

        @Override
        public int getRowWidth() {
            return this.width - 60;
        }

        @Override
        public boolean isFocused() {
            return PatternListScreen.this.getFocused() == this;
        }

        @Override
        protected int addEntry(PatternEntry entry) {
            int i = super.addEntry(entry);
            if (entry.getName().equals(selectedPatternName)) setSelected(entry);
            return i;
        }
    }

    class PatternEntry extends ContainerObjectSelectionList.Entry<PatternEntry> {
        private final String name;
        private final int[] data;
        private final boolean special;

        private CheckButton selectButton;
        private Button editButton;
        private Button duplicateButton;
        private Button deleteButton;

        public PatternEntry(String name, int[] data, boolean special) {
            this.name = name;
            this.data = data;
            this.special = special;

            selectButton = new CheckButton(width / 2 -5, 0, 20, this);
            addWidget(selectButton);

            if (!special) {
                this.editButton = Button.builder(Component.translatable("patternlist.edit"), b -> onEdit()).size(40, 20).build();
                this.duplicateButton = Button.builder(Component.translatable("patternlist.duplicate"), b -> onDuplicate()).size(60, 20).build();
                this.deleteButton = Button.builder(Component.translatable("patternlist.delete"), b -> onDelete()).size(50, 20).build();
            }
        }

        public String getName() {
            return this.name;
        }

        @Override
        public void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY, boolean bl, float deltaTicks) {
            int x = this.getContentX();
            int y = this.getContentY();
            drawScrollableText(guiGraphics, font, Component.literal(name), x, y + 5, 160, 20, 0xFFFFFFFF);

            x += 170;
            selectButton.setPosition(x, y);
            selectButton.render(guiGraphics, mouseX, mouseY, deltaTicks);

            x += 30;

            if (!special) {
                this.editButton.setPosition(x, y);
                this.editButton.render(guiGraphics, mouseX, mouseY, deltaTicks);
                x += 45;

                this.duplicateButton.setPosition(x, y);
                this.duplicateButton.render(guiGraphics, mouseX, mouseY, deltaTicks);
                x += 65;

                this.deleteButton.setPosition(x, y);
                this.deleteButton.render(guiGraphics, mouseX, mouseY, deltaTicks);
            }
        }

        protected static void drawScrollableText(GuiGraphics context, Font textRenderer, Component text, int startX, int startY, int width, int height, int color) {
            int endX = startX + width;
            int endY = startY + height;
            int i = textRenderer.width(text);
            int var10000 = startY + endY;
            Objects.requireNonNull(textRenderer);
            int j = (var10000 - 9) / 2 + 1;
            int k = endX - startX;
            int l;
            if (i > k) {
                l = i - k;
                double d = Util.getMillis() / 250.0;
                double e = Math.max(l * 0.5, 3.0);
                double f = Math.sin(1.5707963267948966 * Math.cos(6.283185307179586 * d / e)) / 2.0 + 0.5;
                double g = Mth.lerp(f, 0.0, l);
                context.enableScissor(startX, startY, endX, endY);
                context.drawString(textRenderer, text, startX - (int)g, j, color);
                context.disableScissor();
            } else {
                context.drawString(textRenderer, text, startX, j, color);
            }
        }

        @Override
        public List<? extends GuiEventListener> children() {
            if (special) {
                return Collections.emptyList();
            }
            return ImmutableList.of(editButton, duplicateButton, deleteButton);
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            if (special) {
                return Collections.emptyList();
            }
            return ImmutableList.of(editButton, duplicateButton, deleteButton);
        }

        private void onEdit() {
            minecraft.setScreen(new PatternEditScreen(PatternListScreen.this, name, data));
        }

        private void onDuplicate() {
            duplicatePattern(name);
        }

        private void onDelete() {
            deletePattern(name);
        }
    }

    public class CheckButton extends AbstractWidget {
        private final WidgetSprites sprites = new WidgetSprites(
                ResourceLocation.withDefaultNamespace("widget/checkbox_selected"),
                ResourceLocation.withDefaultNamespace("widget/checkbox"),
                ResourceLocation.withDefaultNamespace("widget/checkbox_selected_highlighted"),
                ResourceLocation.withDefaultNamespace("widget/checkbox_highlighted")
        );
        private final PatternEntry parent;

        public CheckButton(int i, int j, int k, PatternEntry parent) {
            super(i, j, k, k, CommonComponents.EMPTY);
            this.parent = parent;
        }

        @Override
        public void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
            this.defaultButtonNarrationText(narrationElementOutput);
        }

        @Override
        protected void renderWidget(GuiGraphics guiGraphics, int i, int j, float f) {
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, this.sprites.get(patternList.getSelected() == parent, this.isHovered), this.getX(), this.getY(), this.width, this.height);
        }

        @Override
        public void onClick(MouseButtonEvent mouseButtonEvent, boolean bl) {
            super.onClick(mouseButtonEvent, bl);
            patternList.setSelected(parent);
        }
    }
}
