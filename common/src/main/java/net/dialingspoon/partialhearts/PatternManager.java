package net.dialingspoon.partialhearts;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.*;
import java.util.*;

public class PatternManager {
    public static final String ORIGINAL_PATTERN = "original";
    public static final String RANDOM_PATTERN = "random";
    private static String selectedPatternName = ORIGINAL_PATTERN;
    private static int[] selectedPattern;

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    public static String getSelectedPatternName() {
        return selectedPatternName;
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

    public static int[] getUsedPixels() {
        int[] usedArray = new int[81];
        Arrays.fill(usedArray, 0);
        for (int[] spriteData : PartialHearts.CAPTURED_SPRITES.values()) {
            NativeImage image = PatternManager.loadImageFromArray(spriteData);

            for (int i = 0; i < 81; i++) {
                int x = i % 9;
                int y = i / 9;
                int pixel = image.getPixel(x, y);
                int alpha = (pixel >> 24) & 0xFF;

                if (alpha != 0) {
                    usedArray[i] = -1;
                }
            }
            image.close();
        }
        return usedArray;
    }

    public static void renderHeart(NativeImage heartImage, GuiGraphics guiGraphics, float health, int heartX, int heartY) {
        int[] pixelOrder = selectedPattern;

        long usedIndicesCount = Arrays.stream(pixelOrder)
                .max()
                .getAsInt();

        double lastHeartFraction = 1 - (health % 2) / 2.0;
        lastHeartFraction = lastHeartFraction == 1 ? 0 : lastHeartFraction;
        int fullPixelsToRemove = (int) Math.ceil(lastHeartFraction * (usedIndicesCount + 1));

        for (int i = 0; i < pixelOrder.length; i++) {
            if (pixelOrder[i] < fullPixelsToRemove && pixelOrder[i] != 0) {
                int x = i % 9;
                int y = i / 9;
                heartImage.setPixel(x, y, 0);
            }
        }

        DynamicTexture dynamicTexture = new DynamicTexture(() -> "heartTexture", heartImage);
        ResourceLocation textureLocation = ResourceLocation.fromNamespaceAndPath(PartialHearts.MOD_ID, "dynamic_heart_texture");
        Minecraft.getInstance().getTextureManager().register(textureLocation, dynamicTexture);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, textureLocation, heartX, heartY, 0, 0, 9, 9, 9, 9, 9, 9);

        heartImage.close();
    }

    public static void onResourceManagerReload(ResourceManager resourceManager) {
        Map<ResourceLocation, Resource> resources = resourceManager.listResources("textures/gui/sprites/hud/heart", fileName -> fileName.toString().endsWith(".png"));

        for (Map.Entry<ResourceLocation, Resource> entry : resources.entrySet()) {
            ResourceLocation original = entry.getKey();
            Resource resource = entry.getValue();

            String path = original.getPath();
            String cleanPath = path.substring("textures/gui/sprites/".length(), path.length() - ".png".length());
            ResourceLocation resourceLocation = ResourceLocation.fromNamespaceAndPath(original.getNamespace(), cleanPath);

            try (InputStream inputStream = resource.open()) {
                NativeImage image = NativeImage.read(inputStream);
                int[] spriteData = image.getPixels();
                image.close();

                PartialHearts.CAPTURED_SPRITES.put(resourceLocation, spriteData);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        loadPatterns();
    }

    public static NativeImage loadImageFromArray(int[] data) {
        NativeImage loadedImage = new NativeImage(9, 9, false);
        for (int i = 0; i < data.length; i++) {
            int x = i % 9;
            int y = i / 9;
            loadedImage.setPixel(x, y, data[i]);
        }
        return loadedImage;
    }

    public record PatternsConfig(Map<String, int[]> patterns, String selectedPattern) {}
}

