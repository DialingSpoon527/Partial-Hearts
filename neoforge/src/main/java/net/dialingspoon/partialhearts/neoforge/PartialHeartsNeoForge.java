package net.dialingspoon.partialhearts.neoforge;

import net.dialingspoon.partialhearts.PartialHearts;
import net.dialingspoon.partialhearts.gui.PatternListScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = PartialHearts.MOD_ID, dist = Dist.CLIENT)
public final class PartialHeartsNeoForge {
    public PartialHeartsNeoForge(ModContainer modContainer) {
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, (container, screen) -> new PatternListScreen(screen));
        PartialHearts.init();
    }
}
