package me.suff.mc.angels.common.blocks;

import me.suff.mc.angels.client.poses.WeepingAngelPose;
import me.suff.mc.angels.common.tileentities.StatueTile;
import me.suff.mc.angels.common.variants.AngelTypes;
import me.suff.mc.angels.utils.AngelUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;

import javax.annotation.Nullable;

/**
 * Created by Craig on 17/02/2020 @ 12:19
 */
public class StatueBlock extends Block implements SimpleWaterloggedBlock, EntityBlock {

    public static final IntegerProperty ROTATION = BlockStateProperties.ROTATION_16;

    public StatueBlock() {
        super(Properties.of(Material.STONE).noOcclusion().strength(3).sound(SoundType.STONE).requiresCorrectToolForDrops());
        this.registerDefaultState(this.defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, false));
    }

    @Override
    public boolean hasDynamicShape() {
        return true;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        FluidState fluid = context.getLevel().getFluidState(context.getClickedPos());
        return state.setValue(ROTATION, Mth.floor((double) (context.getRotation() * 16.0F / 360.0F) + 0.5D) & 15).setValue(BlockStateProperties.WATERLOGGED, fluid.is(FluidTags.WATER));
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.defaultFluidState() : Fluids.EMPTY.defaultFluidState();
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(ROTATION, rot.rotate(state.getValue(ROTATION), 16));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.setValue(ROTATION, mirrorIn.mirror(state.getValue(ROTATION), 16));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ROTATION);
        builder.add(BlockStateProperties.WATERLOGGED);
    }


    /**
     * Called by ItemBlocks after a block is set in the world, to allow post-place logic
     */
    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(world, pos, state, placer, stack);
        if (world.getBlockEntity(pos) instanceof StatueTile) {
            StatueTile statue = (StatueTile) world.getBlockEntity(pos);

            if (!world.isClientSide) {
                BlockPos position = statue.getBlockPos();

                if (stack.getTagElement("BlockEntityTag") != null) {
                    statue.load(stack.getTagElement("BlockEntityTag"));
                    //         statue.setPosition(position);
                } else {
                    statue.setAngelType(AngelUtil.randomType().name());
                    statue.setPose(WeepingAngelPose.getRandomPose(world.random));
                    statue.setAngelVarients(AngelTypes.getWeightedRandom());
                }

       /*         if (true) {
                    int offset = 0;
                    int many = 0;
                    for (AngelEnums.AngelType type : AngelEnums.AngelType.values()) {
                        for (AbstractVariant abstractVariant : AngelTypes.VARIANTS_REGISTRY.get()) {
                            for (WeepingAngelPose angelPose : WeepingAngelPose.values()) {
                                if (type == AngelEnums.AngelType.ANGELA_MC) {
                                    world.setBlockAndUpdate(pos.west(offset), WAObjects.Blocks.STATUE.get().defaultBlockState());
                                    StatueTile statueTile = (StatueTile) world.getBlockEntity(pos.west(offset));
                                    statueTile.setPose(angelPose);
                                    statueTile.setAngelType(type);
                                    statueTile.setAngelVarients(abstractVariant);
                                    world.setBlockAndUpdate(pos.west(offset).north(), Blocks.SPRUCE_WALL_SIGN.defaultBlockState());
                                    SignTileEntity signTileEntity = (SignTileEntity) world.getBlockEntity(pos.west(offset).north());
                                    signTileEntity.setMessage(0, new TranslationTextComponent(type.name()));
                                    signTileEntity.setMessage(1, new TranslationTextComponent(angelPose.name()));
                                    signTileEntity.setMessage(2, new TranslationTextComponent(abstractVariant.getRegistryName().getNamespace()));
                                    signTileEntity.setMessage(3, new TranslationTextComponent(abstractVariant.getRegistryName().getPath()));
                                    signTileEntity.requestModelDataUpdate();
                                    offset++;
                                    many++;
                                }
                            }
                        }
                    }
                    System.out.println(many);
                }*/
            }
        }
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new StatueTile(p_153215_, p_153216_);
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