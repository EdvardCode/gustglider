package com.edvardcode.gustglider.item;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

public class UmbrellaItem extends Item {

    public UmbrellaItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            boolean isOpen = stack.getOrCreateTag().getBoolean("isOpen");
            boolean newState = !isOpen;
            stack.getOrCreateTag().putBoolean("isOpen", newState);

            if (newState && level instanceof ServerLevel serverLevel) {
                spawnOpenParticles(serverLevel, player);
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.WOOL_PLACE, SoundSource.PLAYERS, 1.0F, 0.8F);
            } else if (!newState) {
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.WOOL_BREAK, SoundSource.PLAYERS, 0.8F, 1.2F);
            }
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (entity instanceof LivingEntity living) {
            if (living.getMainHandItem() != stack && living.getOffhandItem() != stack) {
                return;
            }

            boolean isOpen = stack.getOrCreateTag().getBoolean("isOpen");

            if (isOpen && !living.onGround() && living.getDeltaMovement().y < 0) {
                living.setDeltaMovement(living.getDeltaMovement().multiply(1.0, 0.8, 1.0));
                living.resetFallDistance();

                // Расход прочности: раз в 80 тиков (~4 секунды)
                if (level.getGameTime() % 40 == 0 && !level.isClientSide) {
                    stack.hurtAndBreak(1, living, (e) -> {
                        e.broadcastBreakEvent(living.getUsedItemHand());
                        level.playSound(null, living.getX(), living.getY(), living.getZ(),
                                SoundEvents.ITEM_BREAK, SoundSource.PLAYERS, 1.0F, 1.0F);
                    });
                }

                if (level instanceof ServerLevel serverLevel) {
                    spawnGlidingParticles(serverLevel, living);
                }
            }
        }

        super.inventoryTick(stack, level, entity, slot, selected);
    }


    @Override
    public int getMaxDamage(ItemStack stack) {
        return 128;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public int getEnchantmentValue() {
        return 1;
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack repairMaterial) {
        return repairMaterial.getItem() instanceof DyeItem
                || repairMaterial.getItem() == Items.WHITE_CARPET
                || repairMaterial.getItem() == Items.LIGHT_GRAY_CARPET
                || repairMaterial.getItem() == Items.GRAY_CARPET
                || repairMaterial.getItem() == Items.BLACK_CARPET
                || repairMaterial.getItem() == Items.BROWN_CARPET
                || repairMaterial.getItem() == Items.RED_CARPET
                || repairMaterial.getItem() == Items.ORANGE_CARPET
                || repairMaterial.getItem() == Items.YELLOW_CARPET
                || repairMaterial.getItem() == Items.LIME_CARPET
                || repairMaterial.getItem() == Items.GREEN_CARPET
                || repairMaterial.getItem() == Items.CYAN_CARPET
                || repairMaterial.getItem() == Items.LIGHT_BLUE_CARPET
                || repairMaterial.getItem() == Items.BLUE_CARPET
                || repairMaterial.getItem() == Items.PURPLE_CARPET
                || repairMaterial.getItem() == Items.MAGENTA_CARPET
                || repairMaterial.getItem() == Items.PINK_CARPET;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged || oldStack.getItem() != newStack.getItem();
    }


    private void spawnOpenParticles(ServerLevel level, Player player) {
        for (int i = 0; i < 15; i++) {
            double offsetX = (level.random.nextDouble() - 0.5) * 1.5;
            double offsetY = level.random.nextDouble() * 2.0;
            double offsetZ = (level.random.nextDouble() - 0.5) * 1.5;
            level.sendParticles(ParticleTypes.CLOUD,
                    player.getX() + offsetX,
                    player.getY() + offsetY,
                    player.getZ() + offsetZ,
                    1,
                    offsetX * 0.5, 0.1, offsetZ * 0.5,
                    0.05);
        }
    }

    private void spawnGlidingParticles(ServerLevel level, LivingEntity entity) {
        if (level.random.nextFloat() < 0.6F) {
            double angle = level.random.nextDouble() * Math.PI * 2;
            double radius = 0.8;
            double offsetX = Math.cos(angle) * radius;
            double offsetZ = Math.sin(angle) * radius;
            double offsetY = 1.5 + level.random.nextDouble() * 0.5;

            level.sendParticles(ParticleTypes.WHITE_ASH,
                    entity.getX() + offsetX,
                    entity.getY() + offsetY,
                    entity.getZ() + offsetZ,
                    1,
                    -offsetX * 0.3, -0.05, -offsetZ * 0.3,
                    0.02);
        }
    }
}