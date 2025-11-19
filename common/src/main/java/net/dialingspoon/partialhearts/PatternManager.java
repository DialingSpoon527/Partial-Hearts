package net.dialingspoon.partialhearts;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import terrails.colorfulhearts.CColorfulHearts;

import java.io.*;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import static net.dialingspoon.partialhearts.PlatformSpecific.isModLoaded;

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

    public static Supplier<ShaderInstance> getShader(float health, float u0, float u1, float v0, float v1) {
        return getShader(false, health, u0, u1, v0, v1);
    }

    public static Supplier<ShaderInstance> getShader(boolean color, float health, float u0, float u1, float v0, float v1) {
        return () -> {
            ShaderInstance shader = color ? PartialHearts.COLOR_SHADER : PartialHearts.SHADER;

            shader.safeGetUniform("UVStart").set(u0, v0);
            shader.safeGetUniform("UVEnd").set(u1, v1);

            shader.safeGetUniform("Mask").set(PatternManager.createMask(health));
            return shader;
        };
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

    public static void onResourceManagerReload(ResourceManager resourceManager) {
        ResourceLocation original;
        int[] xPositions;
        int[] yPositions;

        if (isModLoaded("colorfulhearts")) {
            original = CColorfulHearts.HEALTH_ICONS_LOCATION;

            xPositions = new int[]{0, 18, 36, 54};
            yPositions = new int[]{0, 9, 18, 36, 45, 54};
        } else {
            original = new ResourceLocation("textures/gui/icons.png");

            xPositions = Arrays.stream(Gui.HeartType.values())
                    .filter(heartType -> heartType != Gui.HeartType.CONTAINER)
                    .flatMapToInt(type -> IntStream.of(
                            type.getX(false, false),
                            type.getX(false, true),
                            type.getX(true, false),
                            type.getX(true, true)
                    ))
                    .toArray();
            yPositions = new int[]{0, 45};
        }

        NativeImage image;
        try {
            Resource resource = resourceManager.getResourceOrThrow(original);
            InputStream inputStream = resource.open();
            image = NativeImage.read(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        NativeImage cellImage = new NativeImage(9, 9, true);
        int[] usedArray = new int[81];
        Arrays.fill(usedArray, 0);

        for (int x : xPositions) {
            for (int y : yPositions) {
                image.copyRect(cellImage, x, y, 0, 0, 9, 9, false, false);

                for (int i = 0; i < 81; i++) {
                    int pixel = cellImage.getPixelRGBA(i % 9, i / 9);
                    if ((pixel >>> 24) != 0) usedArray[i] = -1;
                }
            }
        }
        usedPixels = usedArray;

        cellImage.close();
        image.close();

        loadPatterns();
    }

    public static final class PatternsConfig {
        private final Map<String, int[]> patterns;
        private final String selectedPattern;

        public PatternsConfig(Map<String, int[]> patterns, String selectedPattern) {
            this.patterns = Map.copyOf(patterns);
            this.selectedPattern = selectedPattern;
        }
    }
}

