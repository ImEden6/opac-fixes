package com.mervyn.opac_fixes.mixin;

import com.mervyn.opac_fixes.OpacCompat;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vazkii.botania.common.block.block_entity.red_string.RedStringBlockEntity;

@Mixin(value = RedStringBlockEntity.class, priority = 1000)
public class RedStringBlockEntityMixin {

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

        BlockPos selfPos = self.getPos();

        if (OpacCompat.isConnectionProtected(world, selfPos, targetPos)) {
            cir.setReturnValue(null);
        }
    }
}
