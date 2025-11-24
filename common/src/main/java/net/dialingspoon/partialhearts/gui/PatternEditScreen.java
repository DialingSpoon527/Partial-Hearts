package net.dialingspoon.partialhearts.gui;

import net.dialingspoon.partialhearts.PartialHearts;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PatternEditScreen extends Screen {
    private static final int GRID_SIZE = 9;
    private static final int MAX_VALUE = 81;
    private static final int BUTTON_SIZE = 16;

    private final PatternListScreen parent;
    private final String oldName;

    private final int[] oldData;

    private final List<Gui.HeartType> backgroundSprites = new ArrayList<>();
    private int spriteIndex = 0;

    private EditBox nameField;
    private List<NumberButton> buttons;

    private int topOffset;
    private int leftOffset;
    private boolean mouseDown = false;
    private NumberButton hoveredButton;
    private String currentInput = "";

    public PatternEditScreen(PatternListScreen parent) {
        this(parent, null, null);
    }

    public PatternEditScreen(PatternListScreen parent, String oldName, int[] oldData) {
        super(Component.translatable("pattern_edit_screen.title"));
        this.parent = parent;
        this.oldName = oldName;
        this.oldData = oldData;
    }

    @Override
    protected void init() {
        topOffset = 50;
        leftOffset = (this.width - (GRID_SIZE * BUTTON_SIZE)) / 2;

        ImageWidget infoSprite = ImageWidget.sprite(5, 5, ResourceLocation.fromNamespaceAndPath(PartialHearts.MOD_ID, "info"));
        infoSprite.setPosition((font.width(title) + width) / 2 + 2, 2);
        infoSprite.setTooltip(Tooltip.create(Component.translatable("pattern_edit_screen.info")));
        this.addRenderableWidget(infoSprite);

        this.nameField = new EditBox(this.font, this.width / 2 - 100, 20, 200, 20, Component.empty());
        this.nameField.setMaxLength(100);
        this.addRenderableWidget(this.nameField);

        WidgetSprites sprites = new WidgetSprites(ResourceLocation.fromNamespaceAndPath(PartialHearts.MOD_ID, "scroll_left"), ResourceLocation.fromNamespaceAndPath(PartialHearts.MOD_ID, "scroll_left_highlighted"));
        this.addRenderableWidget(new ArrowButton(width / 2 - 6 * BUTTON_SIZE - 32, topOffset + 4 * BUTTON_SIZE - 16, 32, 32, sprites, b -> decrementSpriteIndex()));

        sprites = new WidgetSprites(ResourceLocation.fromNamespaceAndPath(PartialHearts.MOD_ID, "scroll_right"), ResourceLocation.fromNamespaceAndPath(PartialHearts.MOD_ID, "scroll_right_highlighted"));
        this.addRenderableWidget(new ArrowButton(width / 2 + 6 * BUTTON_SIZE, topOffset + 4 * BUTTON_SIZE - 16, 32, 32, sprites, b -> incrementSpriteIndex()));

        this.buttons = new ArrayList<>();
        for (int y = 0; y < GRID_SIZE; y++) {
            for (int x = 0; x < GRID_SIZE; x++) {
                NumberButton btn = new NumberButton(
                        leftOffset + x * BUTTON_SIZE,
                        topOffset + y * BUTTON_SIZE,
                        BUTTON_SIZE, BUTTON_SIZE,
                        this::onButtonClick
                );
                this.buttons.add(btn);
                this.addRenderableWidget(btn);
            }
        }

        if (oldName != null && oldData != null) {
            this.nameField.setValue(oldName);

            for (int i = 0; i < oldData.length && i < 81; i++) {
                int val = oldData[i];
                if (val > 0) {
                    NumberButton btn = buttons.get(i);
                    btn.setValue(val);
                }
            }
        }

        for (Gui.HeartType type : Gui.HeartType.values()) {
            if (type != Gui.HeartType.CONTAINER) {
                if (!backgroundSprites.contains(type)) backgroundSprites.add(type);
            }
        }

        int buttonY = topOffset + (GRID_SIZE * BUTTON_SIZE) + 16;
        Button cancelButton = Button.builder(Component.translatable("gui.cancel"), b -> onClose())
                .pos(this.width / 2 - 55, buttonY)
                .size(50, 20)
                .build();
        Button saveButton = Button.builder(Component.translatable("gui.done"), b -> onSave())
                .pos(this.width / 2 + 5, buttonY)
                .size(50, 20)
                .build();

        this.addRenderableWidget(saveButton);
        this.addRenderableWidget(cancelButton);
    }

    public void incrementSpriteIndex() {
        spriteIndex++;
        if (spriteIndex >= backgroundSprites.size() * 4) {
            spriteIndex = 0;
        }
    }

    public void decrementSpriteIndex() {
        spriteIndex--;
        if (spriteIndex < 0) {
            spriteIndex = backgroundSprites.size() * 4 - 1;
        }
    }


    private void onButtonClick(NumberButton btn) {
        if (!btn.isPressed()) {
            int lowest = getLowestUnused();
            currentInput = String.valueOf(lowest);
            btn.setValue(lowest);
        } else {
            currentInput = "";
            btn.setValue(0);
        }
    }

    public int getLowestUnused() {
        boolean[] present = new boolean[81];

        for (NumberButton button : buttons) {
            if (button.isPressed()) {
                present[button.getValue()-1] = true;
            }
        }

        for (int i = 1; i <= 81; i++) {
            if (!present[i-1]) {
                return i;
            }
        }

        return 81;
    }

    private void onSave() {
        String newName = nameField.getValue().trim();
        if (newName.isEmpty()) {
            newName = "new pattern";
        }

        int[] values = new int[81];
        for (int i = 0; i < buttons.size() && i < 81; i++) {
            NumberButton btn = buttons.get(i);
            values[i] = (btn.isPressed()) ? btn.getValue() : 0;
        }

        parent.onPatternSaved(oldName, newName, values);

        this.minecraft.setScreen(parent);
    }

    @Override
    public void onClose() {
        parent.onPatternEditCanceled();
        this.minecraft.setScreen(parent);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.mouseDown = true;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.mouseDown = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        boolean buttonHovered = false;

        for (NumberButton btn : buttons) {
            if (btn.isMouseOver(mouseX, mouseY)) {
                buttonHovered = true;
                if (hoveredButton != btn) {
                    finalizeNumber();
                    hoveredButton = btn;
                    if (mouseDown) {
                        btn.onClick(mouseX, mouseY);
                    }
                    currentInput = btn.isPressed() ? String.valueOf(btn.getValue()) : "";
                }
                break;
            }
        }
        if (!buttonHovered) {
            finalizeNumber();
            hoveredButton = null;
            currentInput = "";
        }
    }

    private void finalizeNumber() {
        if (hoveredButton == null) return;
        int val = parseSafe(currentInput);
        if ((val < 1 || val > MAX_VALUE)) {
            hoveredButton.setValue(0);
        } else {
            hoveredButton.setValue(val);
        }
        currentInput = "";
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (hoveredButton != null) {
            if (keyCode >= GLFW.GLFW_KEY_0 && keyCode <= GLFW.GLFW_KEY_9) {
                nameField.setFocused(false);
                int digit = keyCode - GLFW.GLFW_KEY_0;
                handleDigitInput(digit);

                return true;
            }
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void handleDigitInput(int digit) {
        String newInput = currentInput + digit;

        int val = parseSafe(newInput);

        while (val > MAX_VALUE) {
            if (newInput.length() > 1) {
                newInput = newInput.substring(1);
            } else {
                newInput = "";
            }
            val = parseSafe(newInput);
        }

        if (val == 0) {
            currentInput = "";
            return;
        }

        currentInput = newInput;
    }

    private int parseSafe(String input) {
        if (input == null || input.isEmpty()) return 0;
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public void render(GuiGraphics gg, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(gg, mouseX, mouseY, partialTicks);

        Gui.HeartType heartType = backgroundSprites.get(spriteIndex / 4);
        boolean hardcore = ((spriteIndex) / 2) % 2 == 1;
        boolean blinking = (spriteIndex % 2) == 1;
        gg.blitSprite(RenderType::guiTextured, heartType.getSprite(hardcore, false, blinking), leftOffset, topOffset, GRID_SIZE*BUTTON_SIZE, GRID_SIZE*BUTTON_SIZE);

        for (Renderable renderable : this.renderables) {
            renderable.render(gg, mouseX, mouseY, partialTicks);
        }
        gg.drawCenteredString(this.font, this.title, this.width / 2, 5, 0xFFFFFF);
        this.nameField.render(gg, mouseX, mouseY, partialTicks);
    }

    public class ArrowButton extends Button {
        private final WidgetSprites sprites;

        public ArrowButton(int i, int j, int k, int l, WidgetSprites widgetSprites, Button.OnPress onPress) {
            super(i, j, k, l, CommonComponents.EMPTY, onPress, DEFAULT_NARRATION);
            this.sprites = widgetSprites;
        }

        @Override
        public void renderWidget(GuiGraphics guiGraphics, int i, int j, float f) {
            ResourceLocation resourceLocation = this.sprites.get(this.isActive(), this.isHovered && mouseDown);
            guiGraphics.blitSprite(RenderType::guiTextured, resourceLocation, this.getX(), this.getY(), this.width, this.height);
        }
    }

    class NumberButton extends AbstractWidget {
        private int value = 0;
        private final Consumer<NumberButton> onPress;

        public NumberButton(int x, int y, int width, int height, Consumer<NumberButton> onPress) {
            super(x, y, width, height, Component.empty());
            this.onPress = onPress;
        }

        @Override
        public void onClick(double mouseX, double mouseY) {
            this.onPress.accept(this);
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
            narrationElementOutput.add(NarratedElementType.TITLE, this.createNarrationMessage());
            narrationElementOutput.add(NarratedElementType.USAGE, Component.literal(String.valueOf(value)));
        }

        @Override
        public void renderWidget(GuiGraphics gg, int mouseX, int mouseY, float partialTicks) {
            int color = 0x80808080;

            int displayNumber = value;
            if (this == hoveredButton) {
                displayNumber = parseSafe(currentInput);
            }

            if (displayNumber > 0) color = 0x80404040;

            gg.fill(getX(), getY(), getX() + width, getY() + height, color);

            if (displayNumber > 0) {
                String numStr = String.valueOf(displayNumber);
                int textWidth = PatternEditScreen.this.font.width(numStr);
                int textX = getX() + (width - textWidth) / 2;
                int textY = getY() + (height - PatternEditScreen.this.font.lineHeight) / 2;
                gg.drawString(PatternEditScreen.this.font, numStr, textX, textY, 0xFFFFFFFF, false);
            }
        }

        public boolean isPressed() {
            return this.value > 0;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }
}

