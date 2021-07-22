package me.suff.mc.angels.common.blocks;

import me.suff.mc.angels.common.entities.AngelEnums;
import me.suff.mc.angels.common.entities.WeepingAngelEntity;
import me.suff.mc.angels.common.tileentities.SnowArmTile;
import me.suff.mc.angels.common.variants.AngelTypes;
import me.suff.mc.angels.utils.AngelUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Random;

public class SnowArmBlock extends SnowLayerBlock implements EntityBlock {

    public SnowArmBlock() {
        super(BlockBehaviour.Properties.of(Material.SNOW).randomTicks().noOcclusion().strength(3).sound(SoundType.SNOW).requiresCorrectToolForDrops());
    }

    @Override
    public void entityInside(BlockState blockState, Level world, BlockPos blockPos, Entity entity) {
        if (world.getBlockEntity(blockPos) instanceof SnowArmTile) {
            SnowArmTile snowArmTile = (SnowArmTile) world.getBlockEntity(blockPos);
            if (snowArmTile.getSnowAngelStage() == SnowArmTile.SnowAngelStages.ARM) {
                entity.makeStuckInBlock(blockState, new Vec3(0.15D, 0.05F, 0.15D));
            }
        }
    }


    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {

        super.setPlacedBy(worldIn, pos, state, placer, stack);
        BlockEntity tile = worldIn.getBlockEntity(pos);
        if (tile instanceof SnowArmTile) {
            int rotation = Mth.floor(placer.yBodyRot);
            SnowArmTile snowArmTile = (SnowArmTile) tile;
            if (!snowArmTile.isHasSetup()) {
                snowArmTile.setSnowAngelStage(AngelUtil.randowSnowStage());
                snowArmTile.setRotation(rotation);
                snowArmTile.setHasSetup(true);
                snowArmTile.setVariant(AngelTypes.getWeightedRandom());
                snowArmTile.sendUpdates();
            }
        }
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            super.onRemove(state, worldIn, pos, newState, isMoving);
        }
    }

    @Override
    public void randomTick(BlockState state, ServerLevel worldIn, BlockPos pos, Random random) {
        if (worldIn.getBrightness(LightLayer.BLOCK, pos) > 11) {
            Containers.dropItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Blocks.SNOW));
            BlockEntity tile = worldIn.getBlockEntity(pos);
            if (tile instanceof SnowArmTile) {
                SnowArmTile snowArmTile = (SnowArmTile) tile;
                WeepingAngelEntity angel = new WeepingAngelEntity(worldIn);
                angel.setType(AngelEnums.AngelType.ANGELA_MC);
                angel.setVarient(snowArmTile.getVariant());
                angel.setPos(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
                worldIn.addFreshEntity(angel);
                worldIn.removeBlock(pos, false);
            }
        }

    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SnowArmTile(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<T> p_153214_) {
        return EntityBlock.super.getTicker(p_153212_, p_153213_, p_153214_);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> GameEventListener getListener(Level p_153210_, T p_153211_) {
        return EntityBlock.super.getListener(p_153210_, p_153211_);
    }
}
