package net.dialingspoon.partialhearts;

import dev.architectury.injectables.annotations.ExpectPlatform;

public class PlatformSpecific {
    @ExpectPlatform
    public static boolean isModLoaded(String modId) {
        throw new AssertionError();
    }
}
