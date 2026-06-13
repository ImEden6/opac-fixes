package com.mervyn.opac_fixes.mixin;

import fuzs.mutantmonsters.world.level.MutatedExplosion;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import java.util.List;
import com.mervyn.opac_fixes.OpacCompat;

@Mixin(value = MutatedExplosion.class, priority = 1000)
public abstract class MutatedExplosionMixin extends Explosion {

    @Shadow(remap = false)
    @Final
    private World world;

    private MutatedExplosionMixin() {
        super(null, null, null, null, 0, 0, 0, 0, false, Explosion.DestructionType.KEEP);
    }

    @ModifyVariable(method = "collectBlocksAndDamageEntities", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/World;getOtherEntities("
            + "Lnet/minecraft/entity/Entity;"
            + "Lnet/minecraft/util/math/Box;"
            + "Ljava/util/function/Predicate;"
            + ")Ljava/util/List;"))
    private List<Entity> filterProtectedEntities(List<Entity> entityList) {
        OpacCompat.onExplosionDetonate(this, entityList, this.world);
        return entityList;
    }
}
