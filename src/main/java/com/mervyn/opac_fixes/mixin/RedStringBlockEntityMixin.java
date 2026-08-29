package com.mervyn.opac_fixes.mixin;

import com.mervyn.opac_fixes.OpacCompat;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.botania.common.block.block_entity.red_string.RedStringBlockEntity;

@Mixin(value = RedStringBlockEntity.class, priority = 1000)
public class RedStringBlockEntityMixin {

    // getBinding() is on the item-transfer hot path (Fabric Transfer API queries it per attempt),
    // while the binding itself only changes once per tick in commonTick - so cache the protection
    // verdict per tick instead of re-running the OPAC reflection chain on every read.
    @Unique
    private BlockPos opacfixes$cachedBindingPos;
    @Unique
    private boolean opacfixes$cachedResult;
    @Unique
    private long opacfixes$cachedTick = -1;

    @Inject(method = "getBinding", at = @At("RETURN"), cancellable = true)
    private void onGetBinding(CallbackInfoReturnable<BlockPos> cir) {
        BlockPos targetPos = cir.getReturnValue();
        if (targetPos == null) {
            return;
        }

        RedStringBlockEntity self = (RedStringBlockEntity) (Object) this;
        World world = self.getWorld();
        if (world == null || world.isClient()) {
            return;
        }

        long tick = world.getTime();
        boolean isProtected;
        if (tick == opacfixes$cachedTick && targetPos.equals(opacfixes$cachedBindingPos)) {
            isProtected = opacfixes$cachedResult;
        } else {
            BlockPos selfPos = self.getPos();
            isProtected = OpacCompat.isConnectionProtected(world, selfPos, targetPos);
            opacfixes$cachedTick = tick;
            opacfixes$cachedBindingPos = targetPos;
            opacfixes$cachedResult = isProtected;
        }

        if (isProtected) {
            cir.setReturnValue(null);
        }
    }
}
