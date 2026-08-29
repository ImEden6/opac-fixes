package com.mervyn.opac_fixes.mixin;

import com.mervyn.opac_fixes.OpacCompat;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import vazkii.botania.common.item.lens.BoreLens;

@Mixin(value = BoreLens.class, priority = 1000)
public class BoreLensMixin {

    // Redirect the actual block-destroy call rather than injecting at HEAD, so earlier
    // pass-through logic in collideBurst (e.g. the lens-warp piston bypass, which never
    // touches the claim at all) isn't affected - only a real break attempt is checked.
    @Redirect(method = "collideBurst", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;breakBlock(Lnet/minecraft/util/math/BlockPos;ZLnet/minecraft/entity/Entity;)Z"))
    private boolean opacfixes$destroyBlock(World world, BlockPos pos, boolean drop, Entity entity) {
        Entity owner = null;
        if (entity instanceof ProjectileEntity projectile) {
            owner = projectile.getOwner();
        }

        boolean isProtected = owner instanceof PlayerEntity player
                ? OpacCompat.isBlockBreakProtected(world, pos, player)
                : OpacCompat.isBlockBreakProtectedEntity(world, pos, entity);

        if (isProtected) {
            return false;
        }
        return world.breakBlock(pos, drop, entity);
    }
}
