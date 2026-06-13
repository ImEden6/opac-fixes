package com.mervyn.opac_fixes.mixin;

import vazkii.botania.common.item.relic.RingOfLokiItem;
import vazkii.botania.common.helper.PlayerHelper;
import vazkii.botania.api.item.SequentialBreaker;
import vazkii.botania.common.item.equipment.tool.ToolCommons;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.block.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Predicate;
import com.mervyn.opac_fixes.OpacCompat;

@Mixin(value = RingOfLokiItem.class, priority = 1000)
public abstract class RingOfLokiMixin {

    @Redirect(method = "onPlayerInteract", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;useOnBlock(Lnet/minecraft/item/ItemUsageContext;)Lnet/minecraft/util/ActionResult;"))
    private static ActionResult redirectUseOnBlock(ItemStack stack, ItemUsageContext ctx) {
        if (OpacCompat.isBlockProtected(ctx.getWorld(), ctx.getBlockPos(), ctx.getPlayer(), ctx.getHand(), ctx.getSide())) {
            return ActionResult.FAIL;
        }
        return stack.useOnBlock(ctx);
    }

    @Redirect(method = "onPlayerInteract", at = @At(value = "INVOKE", target = "Lvazkii/botania/common/helper/PlayerHelper;substituteUse(Lnet/minecraft/item/ItemUsageContext;Lnet/minecraft/item/ItemStack;)Lnet/minecraft/util/ActionResult;"))
    private static ActionResult redirectSubstituteUse(ItemUsageContext ctx, ItemStack stack) {
        if (OpacCompat.isBlockProtected(ctx.getWorld(), ctx.getBlockPos(), ctx.getPlayer(), ctx.getHand(), ctx.getSide())) {
            return ActionResult.FAIL;
        }
        return PlayerHelper.substituteUse(ctx, stack);
    }

    @Redirect(method = "breakOnAllCursors", at = @At(value = "INVOKE", target = "Lvazkii/botania/api/item/SequentialBreaker;breakOtherBlock(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;)V"))
    private static void redirectBreakOtherBlock(SequentialBreaker breaker, PlayerEntity player, ItemStack stack, BlockPos coords, BlockPos pos, Direction side) {
        if (!OpacCompat.isBlockBreakProtected(player.getWorld(), coords, player)) {
            breaker.breakOtherBlock(player, stack, coords, pos, side);
        }
    }

    @Redirect(method = "breakOnAllCursors", at = @At(value = "INVOKE", target = "Lvazkii/botania/common/item/equipment/tool/ToolCommons;removeBlockWithDrops(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Ljava/util/function/Predicate;)V"))
    private static void redirectRemoveBlockWithDrops(PlayerEntity player, ItemStack stack, World world, BlockPos coords, Predicate<BlockState> predicate) {
        if (!OpacCompat.isBlockBreakProtected(world, coords, player)) {
            ToolCommons.removeBlockWithDrops(player, stack, world, coords, predicate);
        }
    }
}
