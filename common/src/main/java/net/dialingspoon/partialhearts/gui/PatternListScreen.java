package net.dialingspoon.partialhearts.gui;

import com.google.common.collect.ImmutableList;
import net.dialingspoon.partialhearts.PatternManager;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class PatternListScreen extends Screen {

    private final Screen parent;

    private String selectedPatternName = PatternManager.getSelectedPatternName();
    private final Map<String, int[]> patterns;
    private PatternList patternList;
    private long initTime;

    public PatternListScreen(Screen parent) {
        super(Component.translatable("patternlist.title"));
        this.parent = parent;

        patterns = PatternManager.loadPatterns();
    }

    @Override
    protected void init() {
        initTime = Util.getMillis();
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
        gg.drawCenteredString(this.font, this.title, this.width / 2, 10, 0xFFFFFF);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean result = super.mouseClicked(mouseX, mouseY, button);

        PatternEntry selected = this.patternList.getSelected();
        if (selected != null) {
            this.selectedPatternName = selected.getName();
        }

        return result;
    }

    @Override
    public void onClose() {
        PatternManager.savePatterns(patterns, selectedPatternName, patterns.get(selectedPatternName));
        this.minecraft.setScreen(parent);
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
            children().add(entry);
            if (entry.getName().equals(selectedPatternName)) setSelected(entry);
            return children().size() - 1;
        }
    }

    class PatternEntry extends ContainerObjectSelectionList.Entry<PatternEntry> {
        private final String name;
        private final int[] data;
        private final boolean special;

        private final CheckButton selectButton;
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
        public void render(GuiGraphics gg, int index, int top, int left, int rowWidth, int rowHeight, int mouseX, int mouseY, boolean hovered, float partialTick) {
            PatternManager.setSelectedPattern(patterns.get(name));
            gg.blitSprite(Gui.HeartType.CONTAINER.getSprite(false, false, false), left - 20, top + 7, 18, 18);
            PatternManager.health = 2 - (((float) ((Util.getMillis() - initTime)) / 500) % 20) / 10;
            gg.blitSprite(Gui.HeartType.NORMAL.getSprite(false, false, false), left - 20, top + 7, 18, 18);

            left += 5;
            drawScrollableText(gg, font, Component.literal(name), left, top + 5, 160, 20, 0xFFFFFF);

            left += rowWidth /2;
            selectButton.setPosition(left, top);
            selectButton.render(gg, mouseX, mouseY, partialTick);

            left += rowWidth > 367 ? 30 : rowWidth / 12;

            if (!special) {
                this.editButton.setPosition(left, top);
                this.editButton.render(gg, mouseX, mouseY, partialTick);
                left += rowWidth > 367 ? 45 : rowWidth / 8;

                this.duplicateButton.setPosition(left, top);
                this.duplicateButton.render(gg, mouseX, mouseY, partialTick);
                left += rowWidth > 367 ? 65 : (int)(rowWidth / 5.5f);

                this.deleteButton.setPosition(left, top);
                this.deleteButton.render(gg, mouseX, mouseY, partialTick);
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
        public @NotNull List<? extends GuiEventListener> children() {
            if (special) {
                return Collections.emptyList();
            }
            return ImmutableList.of(editButton, duplicateButton, deleteButton);
        }

        @Override
        public @NotNull List<? extends NarratableEntry> narratables() {
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

    class CheckButton extends AbstractWidget {
        private final WidgetSprites sprites = new WidgetSprites(
                new ResourceLocation("widget/checkbox_selected"),
                new ResourceLocation("widget/checkbox"),
                new ResourceLocation("widget/checkbox_selected_highlighted"),
                new ResourceLocation("widget/checkbox_highlighted")
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
            guiGraphics.blitSprite(this.sprites.get(patternList.getSelected() == parent, this.isHovered), this.getX(), this.getY(), this.width, this.height);
        }

        @Override public void onClick(double d, double e) {
            patternList.setSelected(parent);
        }
    }
}
