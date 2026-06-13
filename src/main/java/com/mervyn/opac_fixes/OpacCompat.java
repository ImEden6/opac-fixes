package com.mervyn.opac_fixes;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public class OpacCompat {
    private static final Field OPAC_INSTANCE_FIELD;
    private static final Class<?> OPAC_FABRIC_CLASS;
    private static final Method OPAC_GET_COMMON_EVENTS;
    private static final Method OPAC_ON_EXPLOSION_DETONATE;
    
    // Reflection targets for claims/protection API
    private static final Method GET_SERVER_DATA;
    private static final Method GET_CHUNK_PROTECTION;
    private static final Method ON_BLOCK_INTERACTION;
    private static final Method ON_ENTITY_DESTROY_BLOCK;

    public static final boolean AVAILABLE;

    static {
        Field instanceField = null;
        Class<?> fabricClass = null;
        Method getCommonEvents = null;
        Method onExplosionDetonate = null;
        
        Class<?> chunkProtectionClass = null;
        Method getServerData = null;
        Method getChunkProtection = null;
        Method onBlockInteraction = null;
        Method onEntityDestroyBlock = null;
        
        boolean available = false;

        try {
            Class<?> opacClass = Class.forName("xaero.pac.OpenPartiesAndClaims");
            instanceField = opacClass.getField("INSTANCE");

            fabricClass = Class.forName("xaero.pac.OpenPartiesAndClaimsFabric");
            getCommonEvents = fabricClass.getMethod("getCommonEvents");

            Class<?> commonEventsClass = Class.forName("xaero.pac.common.event.CommonEventsFabric");
            onExplosionDetonate = commonEventsClass.getMethod(
                    "onExplosionDetonate",
                    Explosion.class,
                    List.class,
                    World.class);
            
            // ServerData is used to obtain target server state for claims
            Class<?> serverDataClass = Class.forName("xaero.pac.common.server.ServerData");
            getServerData = serverDataClass.getMethod("from", net.minecraft.server.MinecraftServer.class);
            
            Class<?> iServerDataClass = Class.forName("xaero.pac.common.server.IServerData");
            getChunkProtection = iServerDataClass.getMethod("getChunkProtection");
            
            chunkProtectionClass = Class.forName("xaero.pac.common.server.claims.protection.ChunkProtection");
            onBlockInteraction = chunkProtectionClass.getMethod(
                    "onBlockInteraction",
                    iServerDataClass,
                    net.minecraft.block.BlockState.class,
                    Entity.class,
                    Hand.class,
                    ItemStack.class,
                    net.minecraft.server.world.ServerWorld.class,
                    BlockPos.class,
                    Direction.class,
                    boolean.class,
                    boolean.class);
            
            onEntityDestroyBlock = chunkProtectionClass.getMethod(
                    "onEntityDestroyBlock",
                    iServerDataClass,
                    net.minecraft.block.BlockState.class,
                    Entity.class,
                    net.minecraft.server.world.ServerWorld.class,
                    BlockPos.class,
                    boolean.class);
            
            available = true;
        } catch (ReflectiveOperationException e) {
            OpacFixes.LOGGER.error("OPAC compatibility initialization failed", e);
        }

        OPAC_INSTANCE_FIELD = instanceField;
        OPAC_FABRIC_CLASS = fabricClass;
        OPAC_GET_COMMON_EVENTS = getCommonEvents;
        OPAC_ON_EXPLOSION_DETONATE = onExplosionDetonate;
        
        GET_SERVER_DATA = getServerData;
        GET_CHUNK_PROTECTION = getChunkProtection;
        ON_BLOCK_INTERACTION = onBlockInteraction;
        ON_ENTITY_DESTROY_BLOCK = onEntityDestroyBlock;
        AVAILABLE = available;
    }

    public static void onExplosionDetonate(Explosion explosion, List<Entity> entityList, World world) {
        if (!AVAILABLE || world.isClient()) {
            return;
        }
        try {
            Object instance = OPAC_INSTANCE_FIELD.get(null);
            if (OPAC_FABRIC_CLASS.isInstance(instance)) {
                Object commonEvents = OPAC_GET_COMMON_EVENTS.invoke(instance);
                OPAC_ON_EXPLOSION_DETONATE.invoke(commonEvents, explosion, entityList, world);
            }
        } catch (ReflectiveOperationException e) {
            OpacFixes.LOGGER.error("Failed to invoke OPAC onExplosionDetonate", e);
        }
    }

    public static boolean isBlockProtected(World world, BlockPos pos, PlayerEntity player, Hand hand, Direction direction) {
        if (!AVAILABLE || world.isClient() || player.isSpectator()) {
            return false;
        }
        try {
            Object server = world.getServer();
            if (server == null) {
                return false;
            }
            Object serverData = GET_SERVER_DATA.invoke(null, server);
            if (serverData == null) {
                return false;
            }
            Object chunkProtection = GET_CHUNK_PROTECTION.invoke(serverData);
            if (chunkProtection == null) {
                return false;
            }
            net.minecraft.block.BlockState blockState = world.getBlockState(pos);
            net.minecraft.server.world.ServerWorld serverWorld = (net.minecraft.server.world.ServerWorld) world;
            
            // onBlockInteraction returns true if interaction/placement is BLOCKED
            return (boolean) ON_BLOCK_INTERACTION.invoke(
                    chunkProtection,
                    serverData,
                    blockState,
                    player,
                    hand,
                    null, // heldItem (can be null)
                    serverWorld,
                    pos,
                    direction,
                    false, // checkInteractExceptionsOnly
                    true  // messages
            );
        } catch (ReflectiveOperationException e) {
            OpacFixes.LOGGER.error("Failed to invoke OPAC onBlockInteraction", e);
            return false;
        }
    }

    public static boolean isBlockBreakProtected(World world, BlockPos pos, PlayerEntity player) {
        if (!AVAILABLE || world.isClient() || player.isSpectator()) {
            return false;
        }
        try {
            Object server = world.getServer();
            if (server == null) {
                return false;
            }
            Object serverData = GET_SERVER_DATA.invoke(null, server);
            if (serverData == null) {
                return false;
            }
            Object chunkProtection = GET_CHUNK_PROTECTION.invoke(serverData);
            if (chunkProtection == null) {
                return false;
            }
            net.minecraft.block.BlockState blockState = world.getBlockState(pos);
            net.minecraft.server.world.ServerWorld serverWorld = (net.minecraft.server.world.ServerWorld) world;
            
            // onEntityDestroyBlock returns true if breaking is BLOCKED
            return (boolean) ON_ENTITY_DESTROY_BLOCK.invoke(
                    chunkProtection,
                    serverData,
                    blockState,
                    player,
                    serverWorld,
                    pos,
                    true  // messages
            );
        } catch (ReflectiveOperationException e) {
            OpacFixes.LOGGER.error("Failed to invoke OPAC onEntityDestroyBlock", e);
            return false;
        }
    }
}
