package com.mervyn.opac_fixes.mixin;

import com.mervyn.opac_fixes.OpacCompat;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import pro.mikey.justhammers.HammerItem;

import java.util.stream.Stream;

@Mixin(value = HammerItem.class, priority = 1000)
public class HammerItemMixin {

    @Redirect(method = "findAndBreakNearBlocks", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/BlockPos;stream(Lnet/minecraft/util/math/BlockBox;)Ljava/util/stream/Stream;"))
    private Stream<BlockPos> opacfixes$filterProtectedBlocks(BlockBox boundingBox,
            BlockHitResult pick, BlockPos blockPos, ItemStack hammerStack, World level, LivingEntity livingEntity) {
        Stream<BlockPos> original = BlockPos.stream(boundingBox);
        return original.filter(pos -> !OpacCompat.isBlockBreakProtectedEntity(level, pos, livingEntity));
    }
}
