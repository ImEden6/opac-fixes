package com.mervyn.opac_fixes.mixin;

import com.mervyn.opac_fixes.OpacCompat;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.botania.api.internal.ManaBurst;
import vazkii.botania.common.item.lens.BoreLens;

@Mixin(value = BoreLens.class, priority = 1000)
public class BoreLensMixin {

    @Inject(method = "collideBurst", at = @At("HEAD"), cancellable = true)
    private void onCollideBurst(ManaBurst burst, HitResult rtr, boolean isManaBlock, boolean shouldKill, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        Entity entity = burst.entity();
        World world = entity.getWorld();

        if (world.isClient() || rtr.getType() != HitResult.Type.BLOCK) {
            return;
        }

        BlockPos collidePos = ((BlockHitResult) rtr).getBlockPos();

        if (!isManaBlock && !burst.isFake()) {
            Entity owner = null;
            if (entity instanceof net.minecraft.entity.projectile.ProjectileEntity projectile) {
                owner = projectile.getOwner();
            }

            boolean isProtected;
            if (owner instanceof net.minecraft.entity.player.PlayerEntity player) {
                isProtected = OpacCompat.isBlockBreakProtected(world, collidePos, player);
            } else {
                isProtected = OpacCompat.isBlockBreakProtectedEntity(world, collidePos, entity);
            }

            if (isProtected) {
                // Return shouldKill (typically true for block collisions, which destroys the burst)
                cir.setReturnValue(shouldKill);
            }
        }
    }
}
