package com.mervyn.opac_fixes.mixin;

import net.fabricmc.loader.api.FabricLoader;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class OpacFixesMixinPlugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(String mixinPackage) {}

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.endsWith("MutatedExplosionMixin")) {
            return FabricLoader.getInstance().isModLoaded("mutantmonsters");
        }
        if (mixinClassName.endsWith("BombExplosionMixin")) {
            return FabricLoader.getInstance().isModLoaded("supplementaries");
        }
        if (mixinClassName.endsWith("RingOfLokiMixin") || mixinClassName.endsWith("BoreLensMixin") || mixinClassName.endsWith("RedStringBlockEntityMixin")) {
            return FabricLoader.getInstance().isModLoaded("botania");
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, org.objectweb.asm.tree.ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    @Override
    public void postApply(String targetClassName, org.objectweb.asm.tree.ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}
