package net.dialingspoon.partialhearts;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiSpriteManager;
import net.minecraft.client.renderer.CompiledShaderProgram;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.resources.ResourceLocation;

import java.io.*;
import java.util.*;

public class PatternManager {
    public static final String ORIGINAL_PATTERN = "original";
    public static final String RANDOM_PATTERN = "random";
    private static String selectedPatternName = ORIGINAL_PATTERN;
    private static int[] selectedPattern;
    private static int[] usedPixels;
    public static float displayHealthFloat = -1;
    public static float health = -1;

    public static int[] getUsedPixels() {
        return usedPixels.clone();
    }

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    public static String getSelectedPatternName() {
        return selectedPatternName;
    }

    public static void setSelectedPattern(int[] selectedPattern) {
        PatternManager.selectedPattern = selectedPattern;
    }

    public static void savePatterns(Map<String, int[]> patterns, String selectedName, int[] selectedPattern) {
        try {
            selectedPatternName = selectedName;
            PatternManager.selectedPattern = selectedPattern;

            File configFile = new File(Minecraft.getInstance().gameDirectory, "config/partialhearts.json");
            configFile.getParentFile().mkdirs();
            configFile.createNewFile();

            Map<String, int[]> savePatterns = new HashMap<>(patterns);
            savePatterns.remove(ORIGINAL_PATTERN);
            savePatterns.remove(RANDOM_PATTERN);

            PatternsConfig value = new PatternsConfig(savePatterns, selectedPatternName);

            try (Writer writer = new FileWriter(configFile)) {
                GSON.toJson(value, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, int[]> loadPatterns() {
        File configFile = new File(Minecraft.getInstance().gameDirectory, "config/partialhearts.json");
        Map<String, int[]> patterns = new HashMap<>();
        patterns.put(ORIGINAL_PATTERN, createOriginalPattern());
        patterns.put(RANDOM_PATTERN, createRandomPattern());

        if (configFile.exists()) {
            try (Reader reader = new FileReader(configFile)) {
                PatternsConfig loadedConfig = GSON.fromJson(reader, PatternsConfig.class);
                patterns.putAll(loadedConfig.patterns);
                selectedPatternName = loadedConfig.selectedPattern;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        selectedPattern = patterns.get(selectedPatternName);

        return patterns;
    }

    public static int[] createOriginalPattern() {
        int[] resultArray = getUsedPixels();

        int value = 1;
        for (int col = 8; col >= 0; col--) {
            for (int row = 0; row < 9; row++) {
                int index = row * 9 + col;
                if (resultArray[index] == -1) {
                    resultArray[index] = value++;
                }
            }
        }

        return resultArray;
    }


    public static int[] createRandomPattern() {
        int[] resultArray = getUsedPixels();

        long visibleCount = Arrays.stream(resultArray)
                .filter(value -> value == -1)
                .count();

        List<Integer> randomValues = new ArrayList<>();
        for (int i = 1; i <= visibleCount; i++) {
            randomValues.add(i);
        }
        Collections.shuffle(randomValues);

        int randomIndex = 0;
        for (int col = 8; col >= 0; col--) {
            for (int row = 0; row < 9; row++) {
                int index = row * 9 + col;
                if (resultArray[index] == -1) {
                    resultArray[index] = randomValues.get(randomIndex++);
                }
            }
        }

        return resultArray;
    }

    public static void prepareShader(float health, float u0, float u1, float v0, float v1) {
        CompiledShaderProgram program = Minecraft.getInstance().getShaderManager().getProgram(PartialHearts.SHADER);

        program.getUniform("UVStart").set(u0, v0);
        program.getUniform("UVEnd").set(u1, v1);

        program.getUniform("Mask").set(PatternManager.createMask(health));
    }

    public static float[] createMask(float health) {
        int[] pixelOrder = selectedPattern;
        float[] mask = new float[81];

        long usedIndicesCount = Arrays.stream(pixelOrder)
                .max()
                .getAsInt();

        double lastHeartFraction = 1 - (health % 2) / 2.0;
        lastHeartFraction = lastHeartFraction == 1 ? 0 : lastHeartFraction;
        int fullPixelsToRemove = (int) Math.ceil(lastHeartFraction * (usedIndicesCount + 1));

        for (int i = 0; i < pixelOrder.length; i++) {
            if (pixelOrder[i] < fullPixelsToRemove && pixelOrder[i] != 0) {
                mask[i] = 1;
            }
        }
        return mask;
    }

    public static void onResourceManagerReload() {
        ArrayList<ResourceLocation> hearts = new ArrayList<>();

        for(Gui.HeartType heartType : Gui.HeartType.values()) {
            if (heartType != Gui.HeartType.CONTAINER) {
                hearts.add(heartType.getSprite(false, false, false));
                hearts.add(heartType.getSprite(true, false, false));
                hearts.add(heartType.getSprite(false, false, true));
                hearts.add(heartType.getSprite(true, false, false));
            }
        }

        GuiSpriteManager guiSprites = Minecraft.getInstance().getGuiSprites();

        int[] usedArray = new int[81];
        Arrays.fill(usedArray, 0);

        for (ResourceLocation heartLocation : hearts) {
            SpriteContents sprite = guiSprites.getSprite(heartLocation).contents();

            sprite.getUniqueFrames().forEach(frame -> {
                for (int i = 0; i < 81; i++) {
                    if (!sprite.isTransparent(frame, i % 9, i / 9)) usedArray[i] = -1;
                }
            });

            usedPixels = usedArray;
        }

        loadPatterns();
    }

    public record PatternsConfig(Map<String, int[]> patterns, String selectedPattern) {}
}
