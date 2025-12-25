package net.dialingspoon.partialhearts;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import net.dialingspoon.partialhearts.rendering.DynamicCustomUniforms;
import net.dialingspoon.partialhearts.rendering.HeartMaskRenderState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;

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

    public static ArrayList<HeartMaskRenderState> heartUniformQueue = new ArrayList<>();
    public static final DynamicCustomUniforms ubo = new DynamicCustomUniforms();

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

    public static int[] createMaskBits(float health) {
        int[] pixelOrder = selectedPattern;
        int[] bits = new int[3];

        int usedIndicesCount = Arrays.stream(pixelOrder).max().orElse(0);

        double lastHeartFraction = 1 - (health % 2) / 2.0;
        lastHeartFraction = lastHeartFraction == 1 ? 0 : lastHeartFraction;

        int fullPixelsToRemove = (int) Math.ceil(lastHeartFraction * (usedIndicesCount + 1));

        for (int i = 0; i < pixelOrder.length; i++) {
            int idx = pixelOrder[i];
            if (idx < fullPixelsToRemove && idx != 0) {
                int bucket = i / 27;
                int bitPos = i % 27;
                bits[bucket] |= (1 << bitPos);
            }
        }

        return bits;
    }

    public static List<GpuBufferSlice> getUniformSlices() {
        ubo.reset();
        GpuBufferSlice[] gpuBufferSlices = ubo.writeHeartUniforms(heartUniformQueue.toArray(HeartMaskRenderState[]::new));
        heartUniformQueue.clear();
        return List.of(gpuBufferSlices);
    }

    public static void onResourceManagerReload() {
        ArrayList<Identifier> hearts = new ArrayList<>();

        for(Gui.HeartType heartType : Gui.HeartType.values()) {
            if (heartType != Gui.HeartType.CONTAINER) {
                hearts.add(heartType.getSprite(false, false, false));
                hearts.add(heartType.getSprite(true, false, false));
                hearts.add(heartType.getSprite(false, false, true));
                hearts.add(heartType.getSprite(true, false, false));
            }
        }

        TextureAtlas guiSprites = Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.GUI);

        int[] usedArray = new int[81];
        Arrays.fill(usedArray, 0);

        for (Identifier heartLocation : hearts) {
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
