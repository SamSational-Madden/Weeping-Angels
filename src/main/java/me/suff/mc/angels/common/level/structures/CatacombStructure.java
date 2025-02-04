package me.suff.mc.angels.common.level.structures;

import com.mojang.serialization.Codec;
import me.suff.mc.angels.WeepingAngels;
import me.suff.mc.angels.common.WAObjects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.structures.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

import java.util.List;
import java.util.Objects;

public class CatacombStructure extends StructureFeature<NoneFeatureConfiguration> {

    private static final WeightedRandomList<MobSpawnSettings.SpawnerData> ENEMIES = WeightedRandomList.create(new MobSpawnSettings.SpawnerData(WAObjects.EntityEntries.WEEPING_ANGEL.get(), 10, 2, 3), new MobSpawnSettings.SpawnerData(EntityType.SKELETON, 5, 4, 4), new MobSpawnSettings.SpawnerData(EntityType.BAT, 8, 5, 5));


    protected static final String[] variants = new String[]{"flat", "clean", "broken", "normal", "classic"};

    public CatacombStructure(Codec<NoneFeatureConfiguration> p_67039_) {
        super(p_67039_);
    }

    @Override
    public GenerationStep.Decoration step() {
        return GenerationStep.Decoration.RAW_GENERATION;
    }

    @Override
    public StructureStartFactory<NoneFeatureConfiguration> getStartFactory() {
        return FeatureStart::new;
    }

    @Override
    public List<MobSpawnSettings.SpawnerData> getDefaultSpawnList(net.minecraft.world.entity.MobCategory category) {
        if (category == net.minecraft.world.entity.MobCategory.MONSTER)
            return ENEMIES.unwrap();
        return java.util.Collections.emptyList();
    }
    @Override
    protected boolean isFeatureChunk(ChunkGenerator p_160455_, BiomeSource p_160456_, long p_160457_, WorldgenRandom p_160458_, ChunkPos p_160459_, Biome p_160460_, ChunkPos p_160461_, NoneFeatureConfiguration p_160462_, LevelHeightAccessor p_160463_) {
        return super.isFeatureChunk(p_160455_, p_160456_, p_160457_, p_160458_, p_160459_, p_160460_, p_160461_, p_160462_, p_160463_);
    }

    public static class FeatureStart extends StructureStart<NoneFeatureConfiguration> {
        public FeatureStart(StructureFeature<NoneFeatureConfiguration> structureFeature, ChunkPos p_160490_, int p_160491_, long p_160492_) {
            super(structureFeature, p_160490_, p_160491_, p_160492_);
        }


        @Override
        public void generatePieces(RegistryAccess dynamicRegistryManager, ChunkGenerator chunkGenerator, StructureManager structureManager, ChunkPos p_160505_, Biome p_160506_, NoneFeatureConfiguration p_160507_, LevelHeightAccessor heightLimitView) {

            ChunkPos chunkPos = getChunkPos();
            int x = chunkPos.x * 16;
            int z = chunkPos.z * 16;


            BlockPos blockpos = new BlockPos(x, Mth.clamp(random.nextInt(25), 0, 25), z);
            String choosen = variants[random.nextInt(variants.length)];

            JigsawConfiguration structureSettingsAndStartPool = new JigsawConfiguration(() -> dynamicRegistryManager.registryOrThrow(Registry.TEMPLATE_POOL_REGISTRY).get(new ResourceLocation(WeepingAngels.MODID, "catacombs/" + choosen + "/catacomb")), Objects.equals(choosen, "classic") ? 20 : 10);

            JigsawPlacement.addPieces(
                    dynamicRegistryManager,
                    structureSettingsAndStartPool,
                    PoolElementStructurePiece::new,
                    chunkGenerator,
                    structureManager,
                    blockpos,
                    this,
                    this.random,
                    false,
                    false,
                    heightLimitView);


            this.createBoundingBox();
        }

    }
}